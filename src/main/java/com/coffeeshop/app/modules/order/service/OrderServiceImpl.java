package com.coffeeshop.app.modules.order.service;

import com.coffeeshop.app.common.exception.AppException;
import com.coffeeshop.app.common.exception.ResourceNotFoundException;
import com.coffeeshop.app.modules.order.dto.*;
import com.coffeeshop.app.modules.order.entity.Order;
import com.coffeeshop.app.modules.order.entity.OrderItem;
import com.coffeeshop.app.modules.order.entity.OrderItemTopping;
import com.coffeeshop.app.modules.order.entity.OrderStatus;
import com.coffeeshop.app.modules.order.entity.OrderType;
import com.coffeeshop.app.modules.order.repository.OrderRepository;
import com.coffeeshop.app.modules.product.entity.ProductVariant;
import com.coffeeshop.app.modules.product.entity.Topping;
import com.coffeeshop.app.modules.product.repository.ProductVariantRepository;
import com.coffeeshop.app.modules.product.repository.ToppingRepository;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final TableRepository tableRepository;
    private final UserRepository userRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ToppingRepository toppingRepository;

    private static final Random RANDOM = new Random();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResponse createOrder(Long currentUserId, CreateOrderRequest request) {
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên với ID: " + currentUserId));

        CoffeeTable table = null;
        if (request.getOrderType() == OrderType.DINE_IN) {
            if (request.getTableId() == null) {
                throw new AppException(HttpStatus.BAD_REQUEST, "Đơn hàng tại bàn (DINE_IN) bắt buộc phải chỉ định tableId!");
            }

            table = tableRepository.findById(request.getTableId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bàn với ID: " + request.getTableId()));

            // Kiểm tra bàn có đang trống và chưa có đơn PENDING nào
            if (table.getStatus() == TableStatus.OCCUPIED || orderRepository.existsByTableIdAndOrderStatus(table.getId(), OrderStatus.PENDING)) {
                throw new AppException(HttpStatus.BAD_REQUEST, "Bàn '" + table.getName() + "' hiện đang có khách phục vụ!");
            }

            // Chuyển trạng thái bàn sang OCCUPIED
            table.setStatus(TableStatus.OCCUPIED);
            tableRepository.save(table);
        }

        String orderCode = generateOrderCode();

        Order order = Order.builder()
                .orderCode(orderCode)
                .orderType(request.getOrderType())
                .orderStatus(OrderStatus.PENDING)
                .table(table)
                .user(user)
                .note(request.getNote())
                .totalAmount(BigDecimal.ZERO)
                .discountPercentage(0)
                .finalAmount(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItemRequest itemReq : request.getItems()) {
            OrderItem orderItem = buildOrderItem(order, itemReq);
            order.getItems().add(orderItem);
            totalAmount = totalAmount.add(orderItem.getSubtotal());
        }

        order.setTotalAmount(totalAmount);
        order.setDiscountPercentage(0);
        order.setFinalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);
        return mapToOrderResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findByIdWithDetails(orderId)
                .orElseGet(() -> orderRepository.findById(orderId)
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với ID: " + orderId)));
        return mapToOrderResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResponse addItemsToOrder(Long orderId, AddItemsToOrderRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với ID: " + orderId));

        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Chỉ có thể thêm món vào đơn hàng đang ở trạng thái PENDING");
        }

        BigDecimal additionalAmount = BigDecimal.ZERO;
        for (OrderItemRequest itemReq : request.getItems()) {
            OrderItem orderItem = buildOrderItem(order, itemReq);
            order.getItems().add(orderItem);
            additionalAmount = additionalAmount.add(orderItem.getSubtotal());
        }

        BigDecimal updatedTotal = order.getTotalAmount().add(additionalAmount);
        order.setTotalAmount(updatedTotal);

        // Tính lại finalAmount theo % giảm giá hiện có
        int discount = order.getDiscountPercentage() != null ? order.getDiscountPercentage() : 0;
        BigDecimal finalAmount = calculateFinalAmount(updatedTotal, discount);
        order.setFinalAmount(finalAmount);

        Order saved = orderRepository.save(order);
        return mapToOrderResponse(saved);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResponse moveTable(Long orderId, MoveTableRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với ID: " + orderId));

        if (order.getOrderType() != OrderType.DINE_IN) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Chỉ đơn hàng DINE_IN (tại bàn) mới có thể chuyển bàn!");
        }

        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Chỉ đơn hàng đang phục vụ (PENDING) mới có thể chuyển bàn!");
        }

        CoffeeTable currentTable = order.getTable();
        if (currentTable != null && currentTable.getId().equals(request.getTargetTableId())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Bàn đích trùng với bàn hiện tại của đơn hàng!");
        }

        CoffeeTable targetTable = tableRepository.findById(request.getTargetTableId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bàn đích với ID: " + request.getTargetTableId()));

        if (targetTable.getStatus() != TableStatus.EMPTY || orderRepository.existsByTableIdAndOrderStatus(targetTable.getId(), OrderStatus.PENDING)) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Bàn đích '" + targetTable.getName() + "' đang có khách hoặc không trống!");
        }

        // Bàn cũ giải phóng về EMPTY
        if (currentTable != null) {
            currentTable.setStatus(TableStatus.EMPTY);
            tableRepository.save(currentTable);
        }

        // Bàn đích đổi thành OCCUPIED
        targetTable.setStatus(TableStatus.OCCUPIED);
        tableRepository.save(targetTable);

        // Gán bàn mới cho đơn hàng
        order.setTable(targetTable);
        Order saved = orderRepository.save(order);

        return mapToOrderResponse(saved);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResponse mergeTable(MergeTableRequest request) {
        if (request.getFromTableId().equals(request.getToTableId())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Không thể gộp một bàn vào chính nó!");
        }

        Order orderFrom = orderRepository.findByTableIdAndOrderStatus(request.getFromTableId(), OrderStatus.PENDING)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn hàng đang phục vụ tại bàn nguồn (ID: " + request.getFromTableId() + ")"));

        Order orderTo = orderRepository.findByTableIdAndOrderStatus(request.getToTableId(), OrderStatus.PENDING)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn hàng đang phục vụ tại bàn đích (ID: " + request.getToTableId() + ")"));

        // Dời toàn bộ items từ orderFrom sang orderTo
        List<OrderItem> itemsToMove = new ArrayList<>(orderFrom.getItems());
        orderFrom.getItems().clear();

        for (OrderItem item : itemsToMove) {
            item.setOrder(orderTo);
            orderTo.getItems().add(item);
        }

        // Tính lại tổng tiền cho orderTo
        BigDecimal newTotal = orderTo.getItems().stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        orderTo.setTotalAmount(newTotal);

        int discount = orderTo.getDiscountPercentage() != null ? orderTo.getDiscountPercentage() : 0;
        orderTo.setFinalAmount(calculateFinalAmount(newTotal, discount));

        // Hủy đơn orderFrom
        orderFrom.setOrderStatus(OrderStatus.CANCELLED);
        String updatedNote = (orderFrom.getNote() != null && !orderFrom.getNote().isBlank())
                ? orderFrom.getNote() + " | Đã gộp vào đơn " + orderTo.getOrderCode()
                : "Đã gộp vào đơn " + orderTo.getOrderCode();
        orderFrom.setNote(updatedNote);
        orderFrom.setTotalAmount(BigDecimal.ZERO);
        orderFrom.setFinalAmount(BigDecimal.ZERO);

        // Trả bàn nguồn về EMPTY
        CoffeeTable fromTable = tableRepository.findById(request.getFromTableId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bàn nguồn ID: " + request.getFromTableId()));
        fromTable.setStatus(TableStatus.EMPTY);
        tableRepository.save(fromTable);

        orderRepository.save(orderFrom);
        Order savedOrderTo = orderRepository.save(orderTo);

        return mapToOrderResponse(savedOrderTo);
    }

    private OrderItem buildOrderItem(Order order, OrderItemRequest itemReq) {
        ProductVariant variant = productVariantRepository.findById(itemReq.getProductVariantId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy biến thể sản phẩm ID: " + itemReq.getProductVariantId()));

        if (!variant.isActive()) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Biến thể sản phẩm '" + variant.getProduct().getName() + " - " + variant.getSize() + "' đã ngưng kinh doanh");
        }

        BigDecimal priceAtOrder = variant.getPrice();
        OrderItem orderItem = OrderItem.builder()
                .order(order)
                .productVariant(variant)
                .quantity(itemReq.getQuantity())
                .priceAtOrder(priceAtOrder)
                .note(itemReq.getNote())
                .toppings(new ArrayList<>())
                .build();

        BigDecimal toppingsSumPerUnit = BigDecimal.ZERO;
        if (itemReq.getToppingIds() != null && !itemReq.getToppingIds().isEmpty()) {
            for (Long toppingId : itemReq.getToppingIds()) {
                Topping topping = toppingRepository.findById(toppingId)
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Topping ID: " + toppingId));
                if (!topping.isActive()) {
                    throw new AppException(HttpStatus.BAD_REQUEST, "Topping '" + topping.getName() + "' đã ngưng kinh doanh");
                }
                OrderItemTopping itemTopping = OrderItemTopping.builder()
                        .orderItem(orderItem)
                        .topping(topping)
                        .toppingPriceAtOrder(topping.getPrice())
                        .build();
                orderItem.getToppings().add(itemTopping);
                toppingsSumPerUnit = toppingsSumPerUnit.add(topping.getPrice());
            }
        }

        BigDecimal subtotal = priceAtOrder.add(toppingsSumPerUnit).multiply(BigDecimal.valueOf(itemReq.getQuantity()));
        orderItem.setSubtotal(subtotal);
        return orderItem;
    }

    private BigDecimal calculateFinalAmount(BigDecimal totalAmount, int discountPercentage) {
        if (discountPercentage <= 0) {
            return totalAmount;
        }
        BigDecimal discountMultiplier = BigDecimal.valueOf(100 - discountPercentage)
                .divide(BigDecimal.valueOf(100));
        return totalAmount.multiply(discountMultiplier);
    }

    private String generateOrderCode() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int rand = 100 + RANDOM.nextInt(900);
        return "ORD-" + datePart + "-" + rand;
    }

    private OrderResponse mapToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderCode(order.getOrderCode());
        response.setOrderType(order.getOrderType());
        response.setOrderStatus(order.getOrderStatus());
        if (order.getTable() != null) {
            response.setTableId(order.getTable().getId());
            response.setTableName(order.getTable().getName());
        }
        if (order.getUser() != null) {
            response.setUserId(order.getUser().getId());
            response.setCreatorName(order.getUser().getFullName());
        }
        response.setTotalAmount(order.getTotalAmount());
        response.setDiscountPercentage(order.getDiscountPercentage());
        response.setFinalAmount(order.getFinalAmount());
        response.setNote(order.getNote());
        response.setCreatedAt(order.getCreatedAt());

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
