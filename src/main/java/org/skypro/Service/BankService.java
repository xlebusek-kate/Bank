package org.skypro.Service;

import org.skypro.Entity.EntityForMyBase.RuleQueryDTO;
import org.skypro.Recommendations.RecommendationsDTO;
import org.skypro.Repository.BankRepository;
import org.skypro.RuleEvaluator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Сервис формирования рекомендаций: проверяет статические правила,
 * заложенные в код, и динамические правила из базы данных.
 */
@Service
public class BankService {

    private final BankRepository bankRepository;
    private final RuleService ruleService;
    private final RuleEvaluator ruleEvaluator;
    private final RuleStatsService ruleStatsService;

    /**
     * Создаёт сервис.
     *
     * @param bankRepository   репозиторий базы знаний (статические правила)
     * @param ruleService      сервис получения динамических правил
     * @param ruleEvaluator    проверка динамического правила для пользователя
     * @param ruleStatsService счётчик срабатываний динамических правил
     */
    public BankService(BankRepository bankRepository,
                       RuleService ruleService,
                       RuleEvaluator ruleEvaluator,
                       RuleStatsService ruleStatsService) {
        this.bankRepository = bankRepository;
        this.ruleService = ruleService;
        this.ruleEvaluator = ruleEvaluator;
        this.ruleStatsService = ruleStatsService;
    }

    /**
     * Возвращает рекомендации для пользователя. Сначала проверяются три
     * статических правила (Invest 500, Top Saving, Простой кредит), затем все
     * динамические правила из базы. Для каждого сработавшего динамического
     * правила увеличивается счётчик срабатываний.
     *
     * @param uuid идентификатор пользователя
     * @return список рекомендаций, пустой, если ни одно правило не подошло
     */
    public List<RecommendationsDTO> getRecommendationsByUUID(UUID uuid) {
        List<RecommendationsDTO> list = new ArrayList<>();

        // Статические правила
        if (bankRepository.isInvest500(uuid)) {
            list.add(new RecommendationsDTO("Invest 500", "147f6a0f-3b91-413b-ab99-87f081d60d5a", "Текст..."));
        }
        if (bankRepository.isTopSaving(uuid)) {
            list.add(new RecommendationsDTO("Top Saving", "59efc529-2fff-41af-baff-90ccd7402925", "Текст..."));
        }
        if (bankRepository.isSimpleCredit(uuid)) {
            list.add(new RecommendationsDTO("Простой кредит", "ab138afb-f3ba-4a93-b74f-0fcee86d447f", "Текст..."));
        }

        // Динамические правила из базы данных
        List<RuleQueryDTO> dynamicRules = ruleService.getRuleQueryDTO();
        for (RuleQueryDTO rule : dynamicRules) {
            if (ruleEvaluator.isRuleApplicable(rule, uuid)) {
                list.add(new RecommendationsDTO(
                        rule.getProductName(),
                        rule.getProductId(),
                        rule.getProductText()
                ));
                // Правило сработало: учитываем срабатывание в статистике
                ruleStatsService.incrementCounter(rule);
            }
        }

        return list;
    }
}