package com.project.phone_shop.DTO.Response;

import com.project.phone_shop.Entity.Enum.OrderStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {
    Long id;
    Long userId;
    String username;
    BigDecimal totalPrice;
    OrderStatus status;
    LocalDateTime createdAt;
    List<OrderItemResponse> items;
}
