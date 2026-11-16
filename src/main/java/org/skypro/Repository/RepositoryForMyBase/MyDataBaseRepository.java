package org.skypro.Repository.RepositoryForMyBase;

import org.skypro.Entity.EntityForMyBase.RuleQueryDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA-репозиторий динамических правил рекомендаций (PostgreSQL).
 */
@Repository
public interface MyDataBaseRepository extends JpaRepository<RuleQueryDTO, Long> {

 /**
  * Удаляет правила по идентификатору продукта.
  *
  * @param productId идентификатор продукта
  */
 void deleteByProductId(String productId);

 /**
  * Находит правила по идентификатору продукта.
  *
  * @param productId идентификатор продукта
  * @return список правил (пустой, если ничего не найдено)
  */
 List<RuleQueryDTO> findByProductId(String productId);
}