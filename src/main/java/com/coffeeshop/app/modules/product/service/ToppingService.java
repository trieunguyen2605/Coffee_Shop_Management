package com.coffeeshop.app.modules.product.service;

import com.coffeeshop.app.common.exception.AppException;
import com.coffeeshop.app.modules.product.dto.ToppingRequest;
import com.coffeeshop.app.modules.product.dto.ToppingResponse;
import com.coffeeshop.app.modules.product.entity.Topping;
import com.coffeeshop.app.modules.product.repository.ToppingRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ToppingService {

    private final ToppingRepository toppingRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public ToppingResponse createTopping(ToppingRequest request) {
        if (toppingRepository.existsByName(request.getName())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Tên Topping đã tồn tại");
        }

        Topping topping = modelMapper.map(request, Topping.class);
        topping.setActive(true);

        Topping savedTopping = toppingRepository.save(topping);
        return modelMapper.map(savedTopping, ToppingResponse.class);
    }

    @Transactional(readOnly = true)
    public List<ToppingResponse> getActiveToppings() {
        return toppingRepository.findByIsActiveTrue().stream()
                .map(topping -> modelMapper.map(topping, ToppingResponse.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public ToppingResponse updateTopping(Long id, ToppingRequest request) {
        Topping topping = toppingRepository.findById(id)
                .orElseThrow(() -> new com.coffeeshop.app.common.exception.ResourceNotFoundException("Không tìm thấy Topping với ID: " + id));

        if (!topping.getName().equals(request.getName()) && toppingRepository.existsByName(request.getName())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Tên Topping đã tồn tại");
        }

        topping.setName(request.getName());
        topping.setPrice(request.getPrice());
        
        return modelMapper.map(toppingRepository.save(topping), ToppingResponse.class);
    }

    @Transactional
    public ToppingResponse updateToppingStatus(Long id, boolean isActive) {
        Topping topping = toppingRepository.findById(id)
                .orElseThrow(() -> new com.coffeeshop.app.common.exception.ResourceNotFoundException("Không tìm thấy Topping với ID: " + id));
        topping.setActive(isActive);
        return modelMapper.map(toppingRepository.save(topping), ToppingResponse.class);
    }
}
