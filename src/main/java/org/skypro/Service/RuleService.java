package org.skypro.Service;

import org.skypro.Entity.EntityForMyBase.RuleQuery;
import org.skypro.Entity.EntityForMyBase.RuleQueryDTO;
import org.skypro.Repository.RepositoryForMyBase.MyDataBaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Сервис работы с динамическими правилами рекомендаций,
 * которые хранятся в базе данных PostgreSQL.
 */
@Service
public class RuleService {

    private final MyDataBaseRepository myDataBaseRepository;

    /**
     * Создаёт сервис.
     *
     * @param myDataBaseRepository репозиторий динамических правил
     */
    public RuleService(MyDataBaseRepository myDataBaseRepository) {
        this.myDataBaseRepository = myDataBaseRepository;
    }

    /**
     * Сохраняет динамическое правило вместе с его запросами.
     *
     * @param ruleQueryDTO правило из тела запроса
     * @return сохранённое правило
     */
    public RuleQueryDTO addRuleQueryDTO(RuleQueryDTO ruleQueryDTO) {
        List<RuleQuery> queries = ruleQueryDTO.getRule();
        if (queries != null && !queries.isEmpty()) {
            // Связь двусторонняя: без этого в базе у запроса не заполнится ссылка на правило
            for (RuleQuery query : queries) {
                query.setRuleQueryDTO(ruleQueryDTO);
            }
        }
        return myDataBaseRepository.save(ruleQueryDTO);
    }

    /**
     * Возвращает все динамические правила.
     *
     * @return список правил
     */
    public List<RuleQueryDTO> getRuleQueryDTO() {
        return myDataBaseRepository.findAll();
    }

    /**
     * Удаляет все правила, созданные для указанного продукта.
     *
     * @param productId идентификатор продукта
     */
    @Transactional
    public void deleteRuleQuery(String productId) {
        List<RuleQueryDTO> list = myDataBaseRepository.findByProductId(productId);
        for (RuleQueryDTO rule : list) {
            myDataBaseRepository.delete(rule);
        }
    }

}