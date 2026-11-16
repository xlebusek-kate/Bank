package org.skypro.Controller;

import org.skypro.Entity.EntityForMyBase.DataFromMethodGet;
import org.skypro.Entity.EntityForMyBase.ObjectFromMethodGet;
import org.skypro.Entity.EntityForMyBase.RuleQueryDTO;
import org.skypro.Recommendations.FullRecommendationsDTO;
import org.skypro.Service.BankService;
import org.skypro.Service.RuleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST-контроллер рекомендаций и управления динамическими правилами.
 */
@RestController
public class BankController {

    private final BankService bankService;
    private final RuleService ruleService;

    /**
     * Создаёт контроллер.
     *
     * @param bankService сервис формирования рекомендаций
     * @param ruleService сервис работы с динамическими правилами
     */
    public BankController(BankService bankService, RuleService ruleService) {
        this.bankService = bankService;
        this.ruleService = ruleService;
    }

    /**
     * Возвращает рекомендации банковских продуктов для пользователя.
     * Учитываются статические и динамические правила.
     *
     * @param uuid идентификатор пользователя
     * @return идентификатор пользователя и список рекомендаций (пустой, если ни одно правило не подошло)
     */
    @GetMapping("/recommendation/{user_id}")
    public ResponseEntity<FullRecommendationsDTO> listRecommendation(@PathVariable(name = "user_id") UUID uuid) {
        if (uuid == null) return ResponseEntity.badRequest().build();
        FullRecommendationsDTO recommendationsDTO = new FullRecommendationsDTO(String.valueOf(uuid), bankService.getRecommendationsByUUID(uuid));
        return ResponseEntity.ok(recommendationsDTO);
    }

    /**
     * Создаёт динамическое правило рекомендации и сохраняет его в базе.
     *
     * @param rule правило из тела запроса (продукт и список запросов)
     * @return сохранённое правило
     */
    @PostMapping("/rule")
    public ResponseEntity<RuleQueryDTO> saveRuleQueryDTO(@RequestBody RuleQueryDTO rule) {
        RuleQueryDTO saves = ruleService.addRuleQueryDTO(rule);
        return ResponseEntity.ok(saves);
    }

    /**
     * Возвращает все динамические правила в формате {@code {"data": [...]}}.
     *
     * @return список правил вместе с их идентификаторами
     */
    @GetMapping("/rule")
    public ResponseEntity<DataFromMethodGet> getRules() {
        List<ObjectFromMethodGet> list = ruleService.getRuleQueryDTO().stream()
                .map(ruleQueryDTO -> new ObjectFromMethodGet(ruleQueryDTO.getId(), ruleQueryDTO))
                .toList();
        DataFromMethodGet data = new DataFromMethodGet(list);
        return ResponseEntity.ok(data);
    }

    /**
     * Удаляет динамическое правило по идентификатору продукта.
     *
     * @param productId идентификатор продукта, для которого удаляется правило
     * @return ответ 204 No Content
     */
    @DeleteMapping("/rule/{product_id}")
    public ResponseEntity<Void> delete(@PathVariable("product_id") String productId) {
        ruleService.deleteRuleQuery(productId);
        return ResponseEntity.status(204).build();
    }

}