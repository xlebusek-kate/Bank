package org.skypro.Service;

import org.skypro.Recommendations.RecommendationsDTO;
import org.skypro.Repository.BankRepository;
import org.skypro.Entity.EntityForMyBase.RuleQueryDTO;
import org.skypro.RuleEvaluator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class BankService {

    private final BankRepository bankRepository;
    private final RuleService ruleService;           // Чтобы достать правила из 2-й базы
    private final RuleEvaluator ruleEvaluator;       // Чтобы проверить правило
    private final RuleStatsService ruleStatsService; // Чтобы увеличить счетчик

    public BankService(BankRepository bankRepository,
                       RuleService ruleService,
                       RuleEvaluator ruleEvaluator,
                       RuleStatsService ruleStatsService) {
        this.bankRepository = bankRepository;
        this.ruleService = ruleService;
        this.ruleEvaluator = ruleEvaluator;
        this.ruleStatsService = ruleStatsService;
    }

    public List<RecommendationsDTO> getRecommendationsByUUID(UUID uuid) {
        List<RecommendationsDTO> list = new ArrayList<>();

        // --- ТВОИ СТАРЫЕ ЖЕСТКИЕ ПРАВИЛА ---
        if (bankRepository.isInvest500(uuid)) {
            list.add(new RecommendationsDTO("Invest 500", "147f6a0f-3b91-413b-ab99-87f081d60d5a", "Текст..."));
        }
        if (bankRepository.isTopSaving(uuid)) {
            list.add(new RecommendationsDTO("Top Saving", "59efc529-2fff-41af-baff-90ccd7402925", "Текст..."));
        }
        if (bankRepository.isSimpleCredit(uuid)) {
            list.add(new RecommendationsDTO("Простой кредит", "ab138afb-f3ba-4a93-b74f-0fcee86d447f", "Текст..."));
        }

        // --- НОВЫЕ ДИНАМИЧЕСКИЕ ПРАВИЛА ИЗ БАЗЫ ---
        List<RuleQueryDTO> dynamicRules = ruleService.getRuleQueryDTO();
        for (RuleQueryDTO rule : dynamicRules) {
            // 1. Проверяем, подходит ли правило пользователю
            if (ruleEvaluator.isRuleApplicable(rule, uuid)) {

                // 2. Добавляем рекомендацию в список
                list.add(new RecommendationsDTO(
                        rule.getProductName(),
                        rule.getProductId(),
                        rule.getProductText()
                ));

                // 3. 👉 ПУНКТ 5: Увеличиваем счетчик срабатывания
                ruleStatsService.incrementCounter(rule);
            }
        }

        return list;
    }
}