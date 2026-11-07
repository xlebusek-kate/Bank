package org.skypro.Repository.RepositoryForMyBase;

import org.skypro.Entity.EntityForMyBase.RuleQueryDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface MyDataBaseRepository extends JpaRepository<RuleQueryDTO, Long> {

 void deleteByProductId(String productId);


 List<RuleQueryDTO> findByProductId(String productId);
}
