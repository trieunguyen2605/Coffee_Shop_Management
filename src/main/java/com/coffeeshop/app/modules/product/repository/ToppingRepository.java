package com.coffeeshop.app.modules.product.repository;

import com.coffeeshop.app.modules.product.entity.Topping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ToppingRepository extends JpaRepository<Topping, Long> {
    
    boolean existsByName(String name);

    List<Topping> findByIsActiveTrue();
}
