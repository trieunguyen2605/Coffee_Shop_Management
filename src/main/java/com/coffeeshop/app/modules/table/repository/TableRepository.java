package com.coffeeshop.app.modules.table.repository;

import com.coffeeshop.app.modules.table.entity.CoffeeTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TableRepository extends JpaRepository<CoffeeTable, Long> {

    boolean existsByNameAndAreaId(String name, Long areaId);

    List<CoffeeTable> findByAreaId(Long areaId);
}
