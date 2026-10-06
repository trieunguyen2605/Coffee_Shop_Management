package com.coffeeshop.app.modules.table.controller;

import com.coffeeshop.app.common.response.ApiResponse;
import com.coffeeshop.app.modules.table.dto.TableRequest;
import com.coffeeshop.app.modules.table.dto.TableResponse;
import com.coffeeshop.app.modules.table.dto.TableStatusUpdateRequest;
import com.coffeeshop.app.modules.table.service.TableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tables")
@RequiredArgsConstructor
public class TableController {

    private final TableService tableService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TableResponse>> createTable(@Valid @RequestBody TableRequest request) {
        TableResponse tableResponse = tableService.createTable(request);
        return new ResponseEntity<>(ApiResponse.success("Tạo bàn thành công", tableResponse), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<java.util.List<TableResponse>>> getAllTables() {
        java.util.List<TableResponse> tables = tableService.getAllTables();
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách bàn thành công", tables));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<TableResponse>> updateTableStatus(
            @PathVariable Long id, 
            @Valid @RequestBody TableStatusUpdateRequest request) {
        
        TableResponse tableResponse = tableService.updateTableStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái bàn thành công", tableResponse));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteTable(@PathVariable Long id) {
        tableService.deleteTable(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa bàn thành công", null));
    }
}
