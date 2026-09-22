package com.project.phone_shop.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "inventory")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer quantity;
    @OneToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    private Integer reservedQuantity;
    @Column(name = "updated_at", nullable = false)
    private LocalDate updatedAt;

}
