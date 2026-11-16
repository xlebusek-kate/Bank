package org.skypro;

import org.skypro.Entity.EntityForMyBase.RuleQuery;
import org.skypro.Entity.EntityForMyBase.RuleQueryDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Проверяет, выполняется ли динамическое правило для пользователя.
 * Данные о транзакциях читаются из базы H2 через JdbcTemplate.
 */
@Service
public class RuleEvaluator {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Создаёт сервис.
     *
     * @param jdbcTemplate шаблон для запросов к базе с транзакциями
     */
    public RuleEvaluator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Проверяет правило для пользователя. Правило выполняется, только если
     * все его запросы вернули true (с учётом модификатора negate).
     *
     * @param rule   динамическое правило
     * @param userId идентификатор пользователя
     * @return true, если выполнены все запросы правила; false, если правило пустое
     *         или хотя бы один запрос не выполнен
     */
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
                return false; // достаточно одного невыполненного запроса
            }
        }
        return true;
    }

    /**
     * Выполняет один запрос правила. Сейчас поддерживается только тип USER_OF
     * (у пользователя есть хотя бы одна транзакция по продуктам указанного типа),
     * для остальных типов возвращается false.
     */
    private boolean evaluateQuery(RuleQuery query, UUID userId) {
        String type = query.getQuery();
        List<String> args = query.getArguments();

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