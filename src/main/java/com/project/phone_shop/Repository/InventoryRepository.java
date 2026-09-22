package com.project.phone_shop.Repository;

import com.project.phone_shop.Entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    boolean existsByProductId(Long productId);

    Optional<Inventory> findByProductId(Long product_id);
}
