package org.skypro;

import org.skypro.Entity.EntityForMyBase.RuleQuery;
import org.skypro.Entity.EntityForMyBase.RuleQueryDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RuleEvaluator {

    private final JdbcTemplate jdbcTemplate;

    public RuleEvaluator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean isRuleApplicable(RuleQueryDTO rule, UUID userId) {
        List<RuleQuery> queries = rule.getRule();
        if (queries == null || queries.isEmpty()) {
            return false;
        }

        for (RuleQuery query : queries) {
            boolean result = evaluateQuery(query, userId);
            if (query.isNegate()) {
                result = !result;
            }
            if (!result) {
                return false; // Если хотя бы одно условие не выполнено, правило не подходит
            }
        }
        return true; // Все условия выполнены
    }

    private boolean evaluateQuery(RuleQuery query, UUID userId) {
        String type = query.getQuery();
        List<String> args = query.getArguments();

        // Пока реализован только один тип (USER_OF), остальные допишешь по аналогии
        if ("USER_OF".equals(type)) {
            String productType = args.get(0);
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT count(*) FROM TRANSACTIONS t INNER JOIN PRODUCTS p ON t.PRODUCT_ID = p.ID " +
                            "WHERE p.TYPE = ? AND t.USER_ID = ?",
                    Integer.class, productType, userId);
            return count != null && count > 0;
        }

        return false;
    }
}