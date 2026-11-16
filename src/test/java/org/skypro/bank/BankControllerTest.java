package org.skypro.bank;

import org.junit.jupiter.api.Test;
import org.skypro.Controller.BankController;
import org.skypro.Entity.EntityForMyBase.RuleQuery;
import org.skypro.Entity.EntityForMyBase.RuleQueryDTO;
import org.skypro.Recommendations.RecommendationsDTO;
import org.skypro.Service.BankService;
import org.skypro.Service.RuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BankController.class)
class BankControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BankService bankService;

    @MockitoBean
    private RuleService ruleService;

    @Test
    void listRecommendation_returnsRecommendations() throws Exception {
        UUID id = UUID.randomUUID();
        when(bankService.getRecommendationsByUUID(id))
                .thenReturn(List.of(new RecommendationsDTO("Invest 500", "id-1", "Текст")));

        mockMvc.perform(get("/recommendation/{user_id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value(id.toString()))
                .andExpect(jsonPath("$.recommendations[0].name").value("Invest 500"));
    }

    @Test
    void listRecommendation_noRecommendations_returnsEmptyList() throws Exception {
        UUID id = UUID.randomUUID();
        when(bankService.getRecommendationsByUUID(id)).thenReturn(List.of());

        mockMvc.perform(get("/recommendation/{user_id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recommendations").isEmpty());
    }

    @Test
    void listRecommendation_invalidUuid_returns400() throws Exception {
        mockMvc.perform(get("/recommendation/{user_id}", "not-a-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveRule_returnsSavedRule() throws Exception {
        RuleQueryDTO saved = new RuleQueryDTO();
        saved.setProductName("Новый продукт");
        when(ruleService.addRuleQueryDTO(any(RuleQueryDTO.class))).thenReturn(saved);

        String body = """
                {
                  "product_name": "Новый продукт",
                  "product_id": "147f6a0f-3b91-413b-ab99-87f081d60d5a",
                  "product_text": "Описание",
                  "rule": [
                    {"query": "USER_OF", "arguments": ["DEBIT"], "negate": false}
                  ]
                }
                """;

        mockMvc.perform(post("/rule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product_name").value("Новый продукт"));

        verify(ruleService).addRuleQueryDTO(any(RuleQueryDTO.class));
    }

    @Test
    void getRules_returnsDataWithRules() throws Exception {
        RuleQuery query = new RuleQuery();
        query.setQuery("USER_OF");
        query.setArguments(List.of("DEBIT"));
        query.setNegate(false);

        RuleQueryDTO rule = new RuleQueryDTO();
        rule.setProductName("Новый продукт");
        rule.setProductId("147f6a0f-3b91-413b-ab99-87f081d60d5a");
        rule.setProductText("Описание");
        rule.setRule(List.of(query));
        ReflectionTestUtils.setField(rule, "id", 1L); // у id нет сеттера
        when(ruleService.getRuleQueryDTO()).thenReturn(List.of(rule));

        mockMvc.perform(get("/rule"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].product_name").value("Новый продукт"))
                .andExpect(jsonPath("$.data[0].product_id").value("147f6a0f-3b91-413b-ab99-87f081d60d5a"))
                .andExpect(jsonPath("$.data[0].product_text").value("Описание"))
                .andExpect(jsonPath("$.data[0].rule[0].query").value("USER_OF"))
                .andExpect(jsonPath("$.data[0].rule[0].arguments[0]").value("DEBIT"))
                .andExpect(jsonPath("$.data[0].rule[0].negate").value(false));
    }

    @Test
    void deleteRule_returns204() throws Exception {
        String productId = UUID.randomUUID().toString();

        mockMvc.perform(delete("/rule/{product_id}", productId))
                .andExpect(status().isNoContent());

        verify(ruleService).deleteRuleQuery(productId);
    }
}