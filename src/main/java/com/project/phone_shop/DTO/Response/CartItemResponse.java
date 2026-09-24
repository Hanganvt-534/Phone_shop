package com.project.phone_shop.DTO.Response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemResponse {
    Long id;
    Long productId;
    String productName;
    String productImageUrl;
    BigDecimal unitPrice;
    Integer quantity;
    BigDecimal subtotal;
}
