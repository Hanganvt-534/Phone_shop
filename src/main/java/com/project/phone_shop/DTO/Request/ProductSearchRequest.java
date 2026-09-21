package com.project.phone_shop.DTO.Request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductSearchRequest {

    String name;
    String brand;
    String category;
    BigDecimal minPrice;
    BigDecimal maxPrice;
}
