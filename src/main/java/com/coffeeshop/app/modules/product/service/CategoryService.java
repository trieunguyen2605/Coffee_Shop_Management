package com.coffeeshop.app.modules.product.service;

import com.coffeeshop.app.common.exception.AppException;
import com.coffeeshop.app.modules.product.dto.CategoryRequest;
import com.coffeeshop.app.modules.product.dto.CategoryResponse;
import com.coffeeshop.app.modules.product.entity.Category;
import com.coffeeshop.app.modules.product.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Tên danh mục đã tồn tại");
        }

        Category category = modelMapper.map(request, Category.class);
        category.setActive(true);

        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryResponse.class);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(category -> modelMapper.map(category, CategoryResponse.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new com.coffeeshop.app.common.exception.ResourceNotFoundException("Không tìm thấy danh mục với ID: " + id));

        if (!category.getName().equals(request.getName()) && categoryRepository.existsByName(request.getName())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Tên danh mục đã tồn tại");
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        
        return modelMapper.map(categoryRepository.save(category), CategoryResponse.class);
    }

    @Transactional
    public CategoryResponse updateCategoryStatus(Long id, boolean isActive) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new com.coffeeshop.app.common.exception.ResourceNotFoundException("Không tìm thấy danh mục với ID: " + id));
        category.setActive(isActive);
        return modelMapper.map(categoryRepository.save(category), CategoryResponse.class);
    }
}
