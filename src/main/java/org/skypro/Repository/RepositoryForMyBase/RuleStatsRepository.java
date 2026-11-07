package org.skypro.Repository.RepositoryForMyBase;

import org.skypro.Entity.EntityForMyBase.RuleQueryDTO;
import org.skypro.Entity.EntityForMyBase.RuleStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RuleStatsRepository extends JpaRepository<RuleStats, Long> {
    Optional<RuleStats> findByRule(RuleQueryDTO rule);
}