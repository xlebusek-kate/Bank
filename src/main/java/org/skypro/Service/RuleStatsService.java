package org.skypro.Service;

import org.skypro.Entity.EntityForMyBase.RuleQueryDTO;
import org.skypro.Entity.EntityForMyBase.RuleStats;
import org.skypro.Repository.RepositoryForMyBase.RuleStatsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервис статистики срабатываний динамических правил.
 */
@Service
public class RuleStatsService {

    private final RuleStatsRepository ruleStatsRepository;

    /**
     * Создаёт сервис.
     *
     * @param ruleStatsRepository репозиторий счётчиков срабатываний
     */
    public RuleStatsService(RuleStatsRepository ruleStatsRepository) {
        this.ruleStatsRepository = ruleStatsRepository;
    }

    /**
     * Увеличивает счётчик срабатываний правила на единицу.
     * Если счётчика для правила ещё нет, он создаётся.
     *
     * @param rule правило, которое сработало для пользователя
     */
    @Transactional
    public void incrementCounter(RuleQueryDTO rule) {
        RuleStats stats = ruleStatsRepository.findByRule(rule)
                .orElseGet(() -> new RuleStats(rule));
        stats.setCount(stats.getCount() + 1);
        ruleStatsRepository.save(stats);
    }
}