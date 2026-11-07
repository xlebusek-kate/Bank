package org.skypro.Entity.EntityForMyBase;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "rule_stats")
public class RuleStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id", nullable = false, unique = true)
    private RuleQueryDTO rule;

    @Column(name = "count", nullable = false)
    private int count = 0;

    public RuleStats(RuleQueryDTO rule) {
        this.rule = rule;
        this.count = 0;
    }
}