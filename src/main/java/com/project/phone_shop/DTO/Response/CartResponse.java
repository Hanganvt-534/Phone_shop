package com.project.phone_shop.DTO.Response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartResponse {
    Long id;
    Long userId;
    String username;
    List<CartItemResponse> items;
    BigDecimal totalPrice;
    Integer totalItems;
}
