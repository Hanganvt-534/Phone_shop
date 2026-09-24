package com.project.phone_shop.DTO.Request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DirectSaleRequest {

    @NotEmpty(message = "Items must not be empty")
    @Valid
    List<CartItemRequest> items;

    String paymentMethod;

    String shippingAddress;

    String note;
}
