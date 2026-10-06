package com.coffeeshop.app.modules.table.controller;

import com.coffeeshop.app.common.response.ApiResponse;
import com.coffeeshop.app.modules.table.dto.AreaRequest;
import com.coffeeshop.app.modules.table.dto.AreaResponse;
import com.coffeeshop.app.modules.table.dto.AreaWithTablesResponse;
import com.coffeeshop.app.modules.table.service.AreaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/areas")
@RequiredArgsConstructor
public class AreaController {

    private final AreaService areaService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AreaResponse>> createArea(@Valid @RequestBody AreaRequest request) {
        AreaResponse areaResponse = areaService.createArea(request);
        return new ResponseEntity<>(ApiResponse.success("Tạo khu vực thành công", areaResponse), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<AreaResponse>>> getAllAreas() {
        List<AreaResponse> areas = areaService.getAllAreas();
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách khu vực thành công", areas));
    }

    @GetMapping("/tables-view")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<AreaWithTablesResponse>>> getAreaWithTables() {
        List<AreaWithTablesResponse> areaWithTables = areaService.getAreaWithTables();
        return ResponseEntity.ok(ApiResponse.success("Lấy sơ đồ bàn thành công", areaWithTables));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteArea(@PathVariable Long id) {
        areaService.deleteArea(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa khu vực thành công", null));
    }
}
