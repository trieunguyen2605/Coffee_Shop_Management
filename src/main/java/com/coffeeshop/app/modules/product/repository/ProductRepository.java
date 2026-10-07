package com.coffeeshop.app.modules.product.repository;

import com.coffeeshop.app.modules.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    boolean existsByName(String name);

    @Query("SELECT DISTINCT p FROM Product p JOIN FETCH p.category LEFT JOIN FETCH p.variants WHERE p.isActive = true AND p.category.isActive = true")
    List<Product> findAllActiveMenu();

    @Query("SELECT p FROM Product p WHERE (:categoryId IS NULL OR p.category.id = :categoryId) AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    org.springframework.data.domain.Page<Product> searchProducts(
            @org.springframework.data.repository.query.Param("categoryId") Long categoryId, 
            @org.springframework.data.repository.query.Param("keyword") String keyword, 
            org.springframework.data.domain.Pageable pageable);
}
