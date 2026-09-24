package com.project.phone_shop.Controller;

import com.project.phone_shop.DTO.Request.ApiResponse;
import com.project.phone_shop.DTO.Request.CheckoutRequest;
import com.project.phone_shop.DTO.Request.DirectSaleRequest;
import com.project.phone_shop.DTO.Response.OrderResponse;
import com.project.phone_shop.Entity.Enum.OrderStatus;
import com.project.phone_shop.Service.OrderService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {

    OrderService orderService;


    @PostMapping("/checkout")
    public ApiResponse<OrderResponse> checkout(@Valid @RequestBody CheckoutRequest request) {
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.checkoutFromCart(request))
                .build();
    }

    @PostMapping("/direct")
    public ApiResponse<OrderResponse> directSale(@Valid @RequestBody DirectSaleRequest request) {
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.directSale(request))
                .build();
    }


    @GetMapping("/my")
    public ApiResponse<List<OrderResponse>> getMyOrders() {
        return ApiResponse.<List<OrderResponse>>builder()
                .result(orderService.getMyOrders())
                .build();
    }


    @GetMapping("/{orderId}")
    public ApiResponse<OrderResponse> getOrder(@PathVariable Long orderId) {
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.getOrderById(orderId))
                .build();
    }


    @GetMapping
    public ApiResponse<List<OrderResponse>> getAllOrders() {
        return ApiResponse.<List<OrderResponse>>builder()
                .result(orderService.getAllOrders())
                .build();
    }


    @PutMapping("/{orderId}/status")
    public ApiResponse<OrderResponse> updateStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.updateOrderStatus(orderId, status))
                .build();
    }
}
