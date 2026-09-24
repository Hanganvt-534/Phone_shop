package com.project.phone_shop.DTO.Response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItemResponse {
    Long id;
    Long productId;
    String productName;
    String productImageUrl;
    Integer quantity;
    BigDecimal unitPrice;
    BigDecimal subtotal;
}
