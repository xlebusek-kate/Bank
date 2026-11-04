package org.skypro.Service;

import org.skypro.Entity.EntityForMyBase.RuleQuery;
import org.skypro.Entity.EntityForMyBase.RuleQueryDTO;
import org.skypro.Repository.RepositoryForMyBase.MyDataBaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.Objects;

@Service
public class RuleService {

    private final MyDataBaseRepository myDataBaseRepository;

    public RuleService(MyDataBaseRepository myDataBaseRepository) {
        this.myDataBaseRepository = myDataBaseRepository;
    }

    public RuleQueryDTO addRuleQueryDTO(RuleQueryDTO ruleQueryDTO) {
        List<RuleQuery> queries = ruleQueryDTO.getRule();
        if (queries != null && !queries.isEmpty()) {
            for (RuleQuery query : queries) {
                // 3. Говорим каждому условию: "Твое главное правило — вот это"
                query.setRuleQueryDTO(ruleQueryDTO);
            }
        }
        return myDataBaseRepository.save(ruleQueryDTO);
    }

    public List<RuleQueryDTO> getRuleQueryDTO() {
        return myDataBaseRepository.findAll();
    }

    @Transactional
    public void deleteRuleQuery(String productId) {
        List<RuleQueryDTO> list = myDataBaseRepository.findByProductId(productId);
        for(RuleQueryDTO rule: list){
                myDataBaseRepository.delete(rule);
        }
    }


}

