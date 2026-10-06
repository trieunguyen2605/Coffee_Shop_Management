package com.coffeeshop.app.modules.table.repository;

import com.coffeeshop.app.modules.table.entity.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AreaRepository extends JpaRepository<Area, Long> {
    
    boolean existsByName(String name);

    // Dùng FETCH JOIN để giải quyết bài toán N+1 query khi lấy danh sách Area kèm Table
    @Query("SELECT DISTINCT a FROM Area a LEFT JOIN FETCH a.tables")
    List<Area> findAllAreasWithTables();
}
