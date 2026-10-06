package com.coffeeshop.app.modules.table.service;

import com.coffeeshop.app.common.exception.AppException;
import com.coffeeshop.app.modules.table.dto.AreaRequest;
import com.coffeeshop.app.modules.table.dto.AreaResponse;
import com.coffeeshop.app.modules.table.dto.AreaWithTablesResponse;
import com.coffeeshop.app.modules.table.entity.Area;
import com.coffeeshop.app.modules.table.repository.AreaRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AreaService {

    private final AreaRepository areaRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public AreaResponse createArea(AreaRequest request) {
        if (areaRepository.existsByName(request.getName())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Tên khu vực đã tồn tại");
        }

        Area area = modelMapper.map(request, Area.class);
        Area savedArea = areaRepository.save(area);
        return modelMapper.map(savedArea, AreaResponse.class);
    }

    @Transactional(readOnly = true)
    public List<AreaResponse> getAllAreas() {
        return areaRepository.findAll().stream()
                .map(area -> modelMapper.map(area, AreaResponse.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AreaWithTablesResponse> getAreaWithTables() {
        return areaRepository.findAllAreasWithTables().stream()
                .map(area -> {
                    AreaWithTablesResponse response = modelMapper.map(area, AreaWithTablesResponse.class);
                    // Sửa lỗi null areaId và areaName cho danh sách bàn nằm bên trong khu vực
                    if (area.getTables() != null) {
                        java.util.List<com.coffeeshop.app.modules.table.dto.TableResponse> tableResponses = area.getTables().stream()
                                .map(table -> {
                                    com.coffeeshop.app.modules.table.dto.TableResponse tr = modelMapper.map(table, com.coffeeshop.app.modules.table.dto.TableResponse.class);
                                    tr.setAreaId(area.getId());
                                    tr.setAreaName(area.getName());
                                    return tr;
                                }).collect(Collectors.toList());
                        response.setTables(tableResponses);
                    }
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteArea(Long id) {
        Area area = areaRepository.findById(id)
                .orElseThrow(() -> new com.coffeeshop.app.common.exception.ResourceNotFoundException("Không tìm thấy khu vực với ID: " + id));

        if (!area.getTables().isEmpty()) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Không thể xóa khu vực đang chứa bàn. Vui lòng di chuyển hoặc xóa bàn trước.");
        }

        areaRepository.delete(area);
    }
}
