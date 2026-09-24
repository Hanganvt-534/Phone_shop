package com.project.phone_shop.Controller;

import com.project.phone_shop.DTO.Request.ApiResponse;
import com.project.phone_shop.DTO.Request.CartItemRequest;
import com.project.phone_shop.DTO.Response.CartResponse;
import com.project.phone_shop.Service.CartService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart") // gio hang
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController {

    CartService cartService;

    @GetMapping
    public ApiResponse<CartResponse> getMyCart() {
        return ApiResponse.<CartResponse>builder()
                .result(cartService.getMyCart())
                .build();
    }


    @PostMapping("/items")
    public ApiResponse<CartResponse> addToCart(@Valid @RequestBody CartItemRequest request) {
        return ApiResponse.<CartResponse>builder()
                .result(cartService.addToCart(request))
                .build();
    }


    @PutMapping("/items/{itemId}")
    public ApiResponse<CartResponse> updateCartItem(
            @PathVariable Long itemId,
            @Valid @RequestBody CartItemRequest request) {
        return ApiResponse.<CartResponse>builder()
                .result(cartService.updateCartItem(itemId, request))
                .build();
    }

    @DeleteMapping("/items/{itemId}")
    public ApiResponse<CartResponse> removeFromCart(@PathVariable Long itemId) {
        return ApiResponse.<CartResponse>builder()
                .result(cartService.removeFromCart(itemId))
                .build();
    }
}
