package com.coffeeshop.app.modules.inventory.service;

import com.coffeeshop.app.common.exception.AppException;
import com.coffeeshop.app.common.exception.ResourceNotFoundException;
import com.coffeeshop.app.modules.inventory.dto.IngredientImportRequest;
import com.coffeeshop.app.modules.inventory.dto.IngredientRequest;
import com.coffeeshop.app.modules.inventory.dto.IngredientResponse;
import com.coffeeshop.app.modules.inventory.entity.Ingredient;
import com.coffeeshop.app.modules.inventory.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IngredientServiceImpl implements IngredientService {

    private final IngredientRepository ingredientRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public IngredientResponse createIngredient(IngredientRequest request) {
        if (ingredientRepository.existsByName(request.getName())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Tên nguyên liệu đã tồn tại");
        }
        Ingredient ingredient = modelMapper.map(request, Ingredient.class);
        Ingredient savedIngredient = ingredientRepository.save(ingredient);
        return modelMapper.map(savedIngredient, IngredientResponse.class);
    }

    @Override
    @Transactional
    public IngredientResponse importStock(Long id, IngredientImportRequest request) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nguyên liệu với ID: " + id));
        ingredient.setCurrentStock(ingredient.getCurrentStock().add(request.getAmount()));
        return modelMapper.map(ingredientRepository.save(ingredient), IngredientResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<IngredientResponse> getAllIngredients(Pageable pageable) {
        return ingredientRepository.findAll(pageable)
                .map(ingredient -> modelMapper.map(ingredient, IngredientResponse.class));
    }

    @Override
    @Transactional(readOnly = true)
    public List<IngredientResponse> getLowStockIngredients() {
        return ingredientRepository.findLowStockIngredients().stream()
                .map(i -> modelMapper.map(i, IngredientResponse.class))
                .collect(Collectors.toList());
    }
}
