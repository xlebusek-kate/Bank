package org.skypro.Entity.EntityForMyBase;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;


import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rule_query_dto")
public class RuleQueryDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("product_id")
    private String productId;

    @JsonProperty("product_text")
    private String productText;

    @OneToMany(mappedBy = "ruleQueryDTO", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonProperty("rule")
    private List<RuleQuery> rule = new ArrayList<>();

    public RuleQueryDTO() {
    }

    @JsonProperty("product_name")
    public String getProductName() {
        return productName;
    }

    public Long getId() {
        return id;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductText() {
        return productText;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setProductText(String productText) {
        this.productText = productText;
    }

    public List<RuleQuery> getRule() {
        return rule;
    }

    public void setRule(List<RuleQuery> rule) {
        this.rule = rule;
    }
}