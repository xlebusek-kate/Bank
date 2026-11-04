package org.skypro.Entity.EntityForMyBase;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class RuleQuery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;
    private String query;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> arguments;
    private boolean negate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rule_query_dto_id")
    @JsonIgnore
    private RuleQueryDTO ruleQueryDTO;

    public String getQuery() {
        return query;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public Long getId() {
        return id;
    }

    public boolean isNegate() {
        return negate;
    }

    public RuleQuery() {
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }

    public void setNegate(boolean negate) {
        this.negate = negate;
    }

    public void setRuleQueryDTO(RuleQueryDTO ruleQueryDTO) {
        this.ruleQueryDTO = ruleQueryDTO;
    }
}
