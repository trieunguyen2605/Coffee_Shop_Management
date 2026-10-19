package com.coffeeshop.app.modules.order.service;

import com.coffeeshop.app.common.exception.AppException;
import com.coffeeshop.app.common.exception.ResourceNotFoundException;
import com.coffeeshop.app.modules.inventory.service.InventoryService;
import com.coffeeshop.app.modules.order.dto.BillResponse;
import com.coffeeshop.app.modules.order.dto.CheckoutRequest;
import com.coffeeshop.app.modules.order.dto.OrderItemResponse;
import com.coffeeshop.app.modules.order.dto.OrderItemToppingResponse;
import com.coffeeshop.app.modules.order.entity.Order;
import com.coffeeshop.app.modules.order.entity.OrderItem;
import com.coffeeshop.app.modules.order.entity.OrderItemTopping;
import com.coffeeshop.app.modules.order.entity.OrderStatus;
import com.coffeeshop.app.modules.order.entity.OrderType;
import com.coffeeshop.app.modules.order.repository.OrderRepository;
import com.coffeeshop.app.modules.table.entity.CoffeeTable;
import com.coffeeshop.app.modules.table.entity.TableStatus;
import com.coffeeshop.app.modules.table.repository.TableRepository;
import com.coffeeshop.app.modules.user.entity.User;
import com.coffeeshop.app.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final TableRepository tableRepository;
    private final InventoryService inventoryService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BillResponse checkoutOrder(Long orderId, Long cashierUserId, CheckoutRequest request) {
        // Bước 1: Tìm Order theo orderId và tìm User thu ngân
        Order order = orderRepository.findByIdWithDetails(orderId)
                .orElseGet(() -> orderRepository.findById(orderId)
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với ID: " + orderId)));

        User cashier = userRepository.findById(cashierUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên thu ngân với ID: " + cashierUserId));

        // Bước 2: Kiểm tra trạng thái đơn: Bắt buộc PENDING
        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Đơn hàng này đã được thanh toán hoặc đã bị hủy!");
        }

        // Bước 3: Áp dụng chiết khấu và tính tiền
        int discountPercentage = (request.getDiscountPercentage() != null) ? request.getDiscountPercentage() : 0;
        BigDecimal totalAmount = order.getTotalAmount();
        BigDecimal discountAmount = totalAmount.multiply(BigDecimal.valueOf(discountPercentage))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal finalAmount = totalAmount.subtract(discountAmount);

        order.setDiscountPercentage(discountPercentage);
        order.setFinalAmount(finalAmount);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setPaidAt(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.COMPLETED);
        if (request.getNote() != null && !request.getNote().isBlank()) {
            String updatedNote = (order.getNote() != null && !order.getNote().isBlank())
                    ? order.getNote() + " | " + request.getNote()
                    : request.getNote();
            order.setNote(updatedNote);
        }

        // Bước 4: Tự động trừ kho nguyên liệu (Inventory Deduction)
        // Nếu không đủ kho, InventoryService ném AppException và toàn bộ thao tác được ROLLBACK
        for (OrderItem item : order.getItems()) {
            inventoryService.checkAndDeductStock(item.getProductVariant().getId(), item.getQuantity());
        }

        // Bước 5: Giải phóng bàn (nếu là đơn tại bàn - DINE_IN)
        if (order.getOrderType() == OrderType.DINE_IN && order.getTable() != null) {
            CoffeeTable table = order.getTable();
            table.setStatus(TableStatus.EMPTY);
            tableRepository.save(table);
        }

        // Bước 6: Lưu Order và trả về BillResponse
        Order savedOrder = orderRepository.save(order);
        return mapToBillResponse(savedOrder, cashier, discountAmount);
    }

    private BillResponse mapToBillResponse(Order order, User cashier, BigDecimal discountAmount) {
        BillResponse response = new BillResponse();
        response.setOrderCode(order.getOrderCode());
        response.setTableName(order.getTable() != null ? order.getTable().getName() : "Mang đi");
        response.setCashierName(cashier.getFullName());
        response.setCreatedAt(order.getCreatedAt());
        response.setPaidAt(order.getPaidAt());
        response.setPaymentMethod(order.getPaymentMethod());
        response.setTotalAmount(order.getTotalAmount());
        response.setDiscountPercentage(order.getDiscountPercentage());
        response.setDiscountAmount(discountAmount);
        response.setFinalAmount(order.getFinalAmount());

        List<OrderItemResponse> itemResponses = new ArrayList<>();
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                OrderItemResponse itemResp = new OrderItemResponse();
                itemResp.setId(item.getId());
                if (item.getProductVariant() != null) {
                    itemResp.setProductVariantId(item.getProductVariant().getId());
                    itemResp.setSize(item.getProductVariant().getSize());
                    if (item.getProductVariant().getProduct() != null) {
                        itemResp.setProductName(item.getProductVariant().getProduct().getName());
                    }
                }
                itemResp.setQuantity(item.getQuantity());
                itemResp.setPriceAtOrder(item.getPriceAtOrder());
                itemResp.setNote(item.getNote());
                itemResp.setSubtotal(item.getSubtotal());

                List<OrderItemToppingResponse> toppingResponses = new ArrayList<>();
                if (item.getToppings() != null) {
                    for (OrderItemTopping topping : item.getToppings()) {
                        OrderItemToppingResponse toppingResp = new OrderItemToppingResponse();
                        toppingResp.setId(topping.getId());
                        if (topping.getTopping() != null) {
                            toppingResp.setToppingId(topping.getTopping().getId());
                            toppingResp.setToppingName(topping.getTopping().getName());
                        }
                        toppingResp.setToppingPriceAtOrder(topping.getToppingPriceAtOrder());
                        toppingResponses.add(toppingResp);
                    }
                }
                itemResp.setToppings(toppingResponses);
                itemResponses.add(itemResp);
            }
        }
        response.setItems(itemResponses);
        return response;
    }
}
