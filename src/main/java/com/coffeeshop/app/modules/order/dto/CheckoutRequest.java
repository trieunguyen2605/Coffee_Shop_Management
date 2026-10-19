package com.coffeeshop.app.modules.order.dto;

import com.coffeeshop.app.modules.order.entity.PaymentMethod;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckoutRequest {

    @NotNull(message = "Phương thức thanh toán không được để trống")
    private PaymentMethod paymentMethod;

    @Min(value = 0, message = "Phần trăm giảm giá tối thiểu là 0%")
    @Max(value = 100, message = "Phần trăm giảm giá tối đa là 100%")
    private Integer discountPercentage;

    private String note;
}
