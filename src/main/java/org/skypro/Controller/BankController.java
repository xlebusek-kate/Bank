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


@RestController
public class BankController {

    private final BankService bankService;
    private final RuleService ruleService;

    public BankController(BankService bankService, RuleService ruleService) {
        this.bankService = bankService;
        this.ruleService = ruleService;
    }

    @GetMapping("/recommendation/{user_id}")
    public ResponseEntity<FullRecommendationsDTO> listRecommendation (@PathVariable(name = "user_id") UUID uuid){
        if (uuid == null ) return ResponseEntity.badRequest().build();
        FullRecommendationsDTO recommendationsDTO = new FullRecommendationsDTO(String.valueOf(uuid), bankService.getRecommendationsByUUID(uuid));
        return ResponseEntity.ok(recommendationsDTO);
    }

    @PostMapping("/rule")
    public ResponseEntity<RuleQueryDTO> saveRuleQueryDTO (@RequestBody RuleQueryDTO rule){
        RuleQueryDTO saves = ruleService.addRuleQueryDTO(rule);
        return ResponseEntity.ok(saves);
    }

    @GetMapping("/rule")
    public ResponseEntity<DataFromMethodGet> getRules(){
        List<ObjectFromMethodGet> list = ruleService.getRuleQueryDTO().stream()
                .map(ruleQueryDTO -> new ObjectFromMethodGet(ruleQueryDTO.getId(), ruleQueryDTO))
                .toList();
        DataFromMethodGet data = new DataFromMethodGet(list);
        return ResponseEntity.ok(data);
    }

    @DeleteMapping("/rule/{product_id}")
    public ResponseEntity<Void> delete(@RequestParam String productId){
        ruleService.deleteRuleQuery(productId);
        return ResponseEntity.status(204).build();
    }

}
