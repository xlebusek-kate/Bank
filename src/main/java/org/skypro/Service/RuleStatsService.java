package org.skypro.Service;

import org.skypro.Entity.EntityForMyBase.RuleQueryDTO;
import org.skypro.Repository.RepositoryForMyBase.RuleStatsRepository;
import org.skypro.Entity.EntityForMyBase.RuleStats;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RuleStatsService {

    private final RuleStatsRepository ruleStatsRepository;

    public RuleStatsService(RuleStatsRepository ruleStatsRepository) {
        this.ruleStatsRepository = ruleStatsRepository;
    }

    @Transactional
    public void incrementCounter(RuleQueryDTO rule) {
        RuleStats stats = ruleStatsRepository.findByRule(rule)
                .orElseGet(() -> new RuleStats(rule));
        stats.setCount(stats.getCount() + 1);
        ruleStatsRepository.save(stats);
    }
}