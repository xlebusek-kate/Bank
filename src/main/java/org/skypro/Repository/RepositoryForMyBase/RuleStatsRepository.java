package org.skypro.Repository.RepositoryForMyBase;

import org.skypro.Entity.EntityForMyBase.RuleQueryDTO;
import org.skypro.Entity.EntityForMyBase.RuleStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA-репозиторий счётчиков срабатывания динамических правил (PostgreSQL).
 */
@Repository
public interface RuleStatsRepository extends JpaRepository<RuleStats, Long> {

    /**
     * Находит счётчик срабатываний для правила.
     *
     * @param rule динамическое правило
     * @return счётчик или пустой {@link Optional}, если правило ещё не срабатывало
     */
    Optional<RuleStats> findByRule(RuleQueryDTO rule);
}