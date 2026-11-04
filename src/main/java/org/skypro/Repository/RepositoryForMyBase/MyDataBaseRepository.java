package org.skypro.Repository.RepositoryForMyBase;

import org.skypro.Entity.EntityForMyBase.RuleQueryDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MyDataBaseRepository extends JpaRepository<RuleQueryDTO, Long> {

 void deleteByProductId(String productId);


 List<RuleQueryDTO> findByProductId(String productId);
}
