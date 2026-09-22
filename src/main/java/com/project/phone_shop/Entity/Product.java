package com.project.phone_shop.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id", nullable = false, updatable = false)
    Long id;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "description")
    String description;

    @Column(name = "price", nullable = false, precision = 15, scale = 2)
    BigDecimal price;

    @Column(name = "quantity", nullable = false)
    Integer quantity;

    @Column(name = "brand")
    String brand;

    @Column(name = "category")
    String category;

    @Column(name = "image_url")
    String imageUrl;
}