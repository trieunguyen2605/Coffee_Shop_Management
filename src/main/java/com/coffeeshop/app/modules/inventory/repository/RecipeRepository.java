package com.coffeeshop.app.modules.inventory.repository;

import com.coffeeshop.app.modules.inventory.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    
    @Query("SELECT r FROM Recipe r JOIN FETCH r.ingredient WHERE r.productVariant.id = :productVariantId")
    List<Recipe> findByProductVariantId(@Param("productVariantId") Long productVariantId);

    @Modifying
    @Query("DELETE FROM Recipe r WHERE r.productVariant.id = :productVariantId")
    void deleteByProductVariantId(@Param("productVariantId") Long productVariantId);
}
