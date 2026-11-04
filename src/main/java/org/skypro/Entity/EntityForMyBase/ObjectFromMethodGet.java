package org.skypro.Entity.EntityForMyBase;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class ObjectFromMethodGet {

    private final Long id;
    @JsonUnwrapped
    private final RuleQueryDTO ruleQueryDTO;

    public ObjectFromMethodGet(Long id, RuleQueryDTO ruleQueryDTO) {
        this.id = id;
        this.ruleQueryDTO = ruleQueryDTO;
    }

    public Long getId() {
        return id;
    }

    public RuleQueryDTO getRuleQueryDTO() {
        return ruleQueryDTO;
    }
}
