package com.project.phone_shop.DTO.Request;

import com.project.phone_shop.Entity.Enum.PaymentMethodEnum;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CheckoutRequest {

    @NotNull(message = "Payment method is required")
    PaymentMethodEnum paymentMethod;

    String shippingAddress;

    String note;
}
