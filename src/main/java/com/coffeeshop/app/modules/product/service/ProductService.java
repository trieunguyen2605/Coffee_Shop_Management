package com.coffeeshop.app.modules.product.service;

import com.coffeeshop.app.common.exception.AppException;
import com.coffeeshop.app.common.exception.ResourceNotFoundException;
import com.coffeeshop.app.modules.product.dto.ProductCreateRequest;
import com.coffeeshop.app.modules.product.dto.ProductResponse;
import com.coffeeshop.app.modules.product.dto.VariantResponse;
import com.coffeeshop.app.modules.product.entity.Category;
import com.coffeeshop.app.modules.product.entity.Product;
import com.coffeeshop.app.modules.product.entity.ProductVariant;
import com.coffeeshop.app.modules.product.repository.CategoryRepository;
import com.coffeeshop.app.modules.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với ID: " + request.getCategoryId()));

        if (productRepository.existsByName(request.getName())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Tên sản phẩm đã tồn tại");
        }

        Product product = modelMapper.map(request, Product.class);
        product.setCategory(category);
        product.setActive(true);

        // Gán quan hệ 2 chiều cho biến thể
        if (product.getVariants() != null) {
            for (ProductVariant variant : product.getVariants()) {
                variant.setProduct(product);
                variant.setActive(true);
            }
        }

        Product savedProduct = productRepository.save(product);
        return mapToResponse(savedProduct);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getMenu() {
        return productRepository.findAllActiveMenu().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductResponse updateProductStatus(Long id, boolean isActive) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + id));

        product.setActive(isActive);
        Product updatedProduct = productRepository.save(product);
        return mapToResponse(updatedProduct);
    }

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<ProductResponse> getAllProducts(Long categoryId, String keyword, org.springframework.data.domain.Pageable pageable) {
        return productRepository.searchProducts(categoryId, keyword, pageable).map(this::mapToResponse);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, com.coffeeshop.app.modules.product.dto.ProductUpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + id));

        if (!product.getName().equals(request.getName()) && productRepository.existsByName(request.getName())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Tên sản phẩm đã tồn tại");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với ID: " + request.getCategoryId()));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(category);

        if (request.getVariants() != null) {
            java.util.Map<Long, ProductVariant> existingVariantsMap = product.getVariants().stream()
                    .collect(Collectors.toMap(ProductVariant::getId, v -> v));

            java.util.Set<Long> requestVariantIds = request.getVariants().stream()
                    .filter(v -> v.getId() != null)
                    .map(com.coffeeshop.app.modules.product.dto.VariantUpdateRequest::getId)
                    .collect(Collectors.toSet());

            // Soft delete các variant bị loại bỏ khỏi danh sách (tránh orphanRemoval gây lỗi khóa ngoại)
            for (ProductVariant existingVariant : product.getVariants()) {
                if (!requestVariantIds.contains(existingVariant.getId())) {
                    existingVariant.setActive(false);
                }
            }

            // Update hoặc thêm mới
            for (com.coffeeshop.app.modules.product.dto.VariantUpdateRequest variantReq : request.getVariants()) {
                if (variantReq.getId() != null && existingVariantsMap.containsKey(variantReq.getId())) {
                    ProductVariant existingVariant = existingVariantsMap.get(variantReq.getId());
                    existingVariant.setSize(variantReq.getSize());
                    existingVariant.setPrice(variantReq.getPrice());
                    if (variantReq.getIsActive() != null) {
                        existingVariant.setActive(variantReq.getIsActive());
                    }
                } else {
                    ProductVariant newVariant = ProductVariant.builder()
                            .product(product)
                            .size(variantReq.getSize())
                            .price(variantReq.getPrice())
                            .isActive(variantReq.getIsActive() != null ? variantReq.getIsActive() : true)
                            .build();
                    product.getVariants().add(newVariant);
                }
            }
        }

        return mapToResponse(productRepository.save(product));
    }

    // Helper map để xử lý vụ STRICT lồng nhau
    private ProductResponse mapToResponse(Product product) {
        ProductResponse response = modelMapper.map(product, ProductResponse.class);

        if (product.getCategory() != null) {
            response.setCategoryId(product.getCategory().getId());
            response.setCategoryName(product.getCategory().getName());
        }

        if (product.getVariants() != null) {
            List<VariantResponse> variantResponses = product.getVariants().stream()
                    .map(variant -> modelMapper.map(variant, VariantResponse.class))
                    .collect(Collectors.toList());
            response.setVariants(variantResponses);
        }

        return response;
    }
}
