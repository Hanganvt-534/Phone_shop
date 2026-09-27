package com.project.phone_shop.Service;

import com.project.phone_shop.Config.SecurityUtils;
import com.project.phone_shop.DTO.Request.CartItemRequest;
import com.project.phone_shop.DTO.Request.CheckoutRequest;
import com.project.phone_shop.DTO.Request.DirectSaleRequest;
import com.project.phone_shop.DTO.Response.OrderItemResponse;
import com.project.phone_shop.DTO.Response.OrderResponse;
import com.project.phone_shop.Entity.*;
import com.project.phone_shop.Entity.Enum.OrderStatus;
import com.project.phone_shop.Repository.*;
import com.project.phone_shop.exception.AppException;
import com.project.phone_shop.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService {

    OrderRepository orderRepository;
    ProductRepository productRepository;
    InventoryRepository inventoryRepository;
    UserRepository userRepository;
    CartService cartService;

    /**
     * Checkout giỏ hàng → tạo đơn hàng + trừ kho
     */
    @Transactional
    public OrderResponse checkoutFromCart(CheckoutRequest request) {
        User user = SecurityUtils.getCurrentUser(userRepository);
        log.info("User '{}' (ID: {}) is checking out from cart", user.getUsername(), user.getId());
        Cart cart = cartService.getCartEntityForUser(user);

        if (cart.getItems().isEmpty()) {
            throw new AppException(ErrorCode.CART_EMPTY);
        }

        // Kiểm tra & trừ kho cho tất cả items
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            int qty = cartItem.getQuantity();

            Inventory inventory = inventoryRepository.findByProductId(product.getId())
                    .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOT_FOUND));

            if (inventory.getQuantity() < qty) {
                throw new AppException(ErrorCode.INSUFFICIENT_QUANTITY);
            }

            // Trừ số lượng trong kho
            inventory.setQuantity(inventory.getQuantity() - qty);
            inventoryRepository.save(inventory);

            // Trừ số lượng trong bảng Product (để đồng bộ)
            product.setQuantity(product.getQuantity() - qty);
            productRepository.save(product);

            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(qty));
            totalPrice = totalPrice.add(subtotal);

            orderItems.add(OrderItem.builder()
                    .product(product)
                    .quantity(qty)
                    .unitPrice(product.getPrice())
                    .build());
        }

        // Tạo đơn hàng
        Order order = Order.builder()
                .user(user)
                .totalPrice(totalPrice)
                .status(OrderStatus.PENDING)
                .items(new ArrayList<>())
                .build();

        order = orderRepository.save(order);

        // Gán order cho từng item và lưu
        for (OrderItem item : orderItems) {
            item.setOrder(order);
        }
        order.getItems().addAll(orderItems);
        order = orderRepository.save(order);

        // Xóa giỏ hàng sau khi checkout
        cartService.clearCart(cart);

        log.info("Order {} created successfully for user {}", order.getId(), user.getUsername());
        return toOrderResponse(order);
    }

    /**
     * Mua thẳng không qua giỏ hàng → tạo đơn hàng + trừ kho
     */
    /**
     * Mua thẳng không qua giỏ hàng → tạo đơn hàng + trừ kho
     */
    @Transactional
    public OrderResponse directSale(DirectSaleRequest request) {
        User user = SecurityUtils.getCurrentUser(userRepository);
        log.info("User '{}' (ID: {}) is performing direct sale order", user.getUsername(), user.getId());

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

            int qty = itemRequest.getQuantity();

            Inventory inventory = inventoryRepository.findByProductId(product.getId())
                    .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOT_FOUND));

            if (inventory.getQuantity() < qty) {
                throw new AppException(ErrorCode.INSUFFICIENT_QUANTITY);
            }

            // Trừ số lượng trong kho
            inventory.setQuantity(inventory.getQuantity() - qty);
            inventoryRepository.save(inventory);

            // Đồng bộ với Product
            product.setQuantity(product.getQuantity() - qty);
            productRepository.save(product);

            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(qty));
            totalPrice = totalPrice.add(subtotal);

            orderItems.add(OrderItem.builder()
                    .product(product)
                    .quantity(qty)
                    .unitPrice(product.getPrice())
                    .build());
        }

        Order order = Order.builder()
                .user(user)
                .totalPrice(totalPrice)
                .status(OrderStatus.PENDING)
                .items(new ArrayList<>())
                .build();

        order = orderRepository.save(order);

        for (OrderItem item : orderItems) {
            item.setOrder(order);
        }
        order.getItems().addAll(orderItems);
        order = orderRepository.save(order);

        log.info("Direct sale order {} created successfully for user {}", order.getId(), user.getUsername());
        return toOrderResponse(order);
    }

    /**
     * Lấy danh sách đơn hàng của user hiện tại
     */
    public List<OrderResponse> getMyOrders() {
        User user = SecurityUtils.getCurrentUser(userRepository);
        log.info("User '{}' (ID: {}) is retrieving their orders", user.getUsername(), user.getId());
        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::toOrderResponse)
                .toList();
    }

    /**
     * Lấy chi tiết đơn hàng
     */
    public OrderResponse getOrderById(Long orderId) {
        User user = SecurityUtils.getCurrentUser(userRepository);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        // User chỉ xem được đơn của mình, ADMIN xem được tất cả
        boolean isAdmin = SecurityUtils.isAdmin();

        if (!isAdmin && !order.getUser().getId().equals(user.getId())) {
            log.warn("Access denied: User '{}' does not own order {}", user.getUsername(), orderId);
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        log.info("User '{}' (ID: {}, isAdmin: {}) accessed order {}", user.getUsername(), user.getId(), isAdmin, orderId);
        return toOrderResponse(order);
    }

    /**
     * Cập nhật trạng thái đơn hàng (ADMIN only)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus) {
        String adminUsername = SecurityUtils.getCurrentUsername().orElse("ADMIN");
        log.info("Admin '{}' is updating status of order {} to {}", adminUsername, orderId, newStatus);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        // Nếu hủy đơn → hoàn kho
        if (newStatus == OrderStatus.CANCELLED && order.getStatus() != OrderStatus.CANCELLED) {
            for (OrderItem item : order.getItems()) {
                Inventory inventory = inventoryRepository.findByProductId(item.getProduct().getId())
                        .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOT_FOUND));
                inventory.setQuantity(inventory.getQuantity() + item.getQuantity());
                inventoryRepository.save(inventory);

                Product product = item.getProduct();
                product.setQuantity(product.getQuantity() + item.getQuantity());
                productRepository.save(product);
            }
        }

        order.setStatus(newStatus);
        order = orderRepository.save(order);
        return toOrderResponse(order);
    }

    /**
     * Lấy tất cả đơn hàng (ADMIN only)
     */
    @PreAuthorize("hasRole('ADMIN')")
    public List<OrderResponse> getAllOrders() {
        String adminUsername = SecurityUtils.getCurrentUsername().orElse("ADMIN");
        log.info("Admin '{}' is retrieving all orders in system", adminUsername);
        return orderRepository.findAll()
                .stream()
                .map(this::toOrderResponse)
                .toList();
    }

    private OrderResponse toOrderResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .productImageUrl(item.getProduct().getImageUrl())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .subtotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .build())
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .username(order.getUser().getUsername())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .items(itemResponses)
                .build();
    }
}
