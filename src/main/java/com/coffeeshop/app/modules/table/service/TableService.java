package com.coffeeshop.app.modules.table.service;

import com.coffeeshop.app.common.exception.AppException;
import com.coffeeshop.app.common.exception.ResourceNotFoundException;
import com.coffeeshop.app.modules.table.dto.TableRequest;
import com.coffeeshop.app.modules.table.dto.TableResponse;
import com.coffeeshop.app.modules.table.dto.TableStatusUpdateRequest;
import com.coffeeshop.app.modules.table.entity.Area;
import com.coffeeshop.app.modules.table.entity.CoffeeTable;
import com.coffeeshop.app.modules.table.entity.TableStatus;
import com.coffeeshop.app.modules.table.repository.AreaRepository;
import com.coffeeshop.app.modules.table.repository.TableRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TableService {

    private final TableRepository tableRepository;
    private final AreaRepository areaRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public java.util.List<TableResponse> getAllTables() {
        return tableRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    private TableResponse mapToResponse(CoffeeTable table) {
        TableResponse response = modelMapper.map(table, TableResponse.class);
        if (table.getArea() != null) {
            response.setAreaId(table.getArea().getId());
            response.setAreaName(table.getArea().getName());
        }
        return response;
    }

    @Transactional
    public TableResponse createTable(TableRequest request) {
        Area area = areaRepository.findById(request.getAreaId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khu vực với ID: " + request.getAreaId()));

        if (tableRepository.existsByNameAndAreaId(request.getName(), request.getAreaId())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Tên bàn đã tồn tại trong khu vực này");
        }

        CoffeeTable table = CoffeeTable.builder()
                .name(request.getName())
                .area(area)
                .status(TableStatus.EMPTY)
                .build();

        CoffeeTable savedTable = tableRepository.save(table);
        return mapToResponse(savedTable);
    }

    @Transactional
    public TableResponse updateTableStatus(Long id, TableStatusUpdateRequest request) {
        CoffeeTable table = tableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bàn với ID: " + id));

        table.setStatus(request.getStatus());
        CoffeeTable updatedTable = tableRepository.save(table);
        return mapToResponse(updatedTable);
    }

    @Transactional
    public void deleteTable(Long id) {
        CoffeeTable table = tableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bàn với ID: " + id));

        if (table.getStatus() != TableStatus.EMPTY) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Chỉ có thể xóa bàn đang ở trạng thái trống (EMPTY)");
        }

        tableRepository.delete(table);
    }
}
