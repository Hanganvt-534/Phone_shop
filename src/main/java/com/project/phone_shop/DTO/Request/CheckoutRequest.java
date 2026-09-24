package com.project.phone_shop.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CheckoutRequest {

    @NotBlank(message = "Payment method is required")
    String paymentMethod; // CASH, CREDIT_CARD, etc.

    String shippingAddress;

    String note;
}
