package com.project.phone_shop.Service;

import com.project.phone_shop.DTO.Mapper.InventoryMapper;
import com.project.phone_shop.DTO.Request.InventoryRequest;
import com.project.phone_shop.DTO.Response.InventoryResponse;
import com.project.phone_shop.Entity.Inventory;
import com.project.phone_shop.Repository.InventoryRepository;
import com.project.phone_shop.exception.AppException;
import com.project.phone_shop.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InventoryService {
    final InventoryRepository inventoryRepository;
    final InventoryMapper inventoryMapper;
    public List<InventoryResponse> getInventory() {
        return inventoryRepository.findAll().stream()
                .map(inventory -> InventoryResponse.builder()
                        .productId(inventory.getProduct().getId())
                        .quantity(inventory.getQuantity())
                        .build())
                .toList();
    }

    public InventoryResponse getInventoryByProductId(Long productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOT_FOUND));
        return InventoryResponse.builder()
                .productId(inventory.getProduct().getId())
                .quantity(inventory.getQuantity())
                .build();
    }

    public InventoryResponse importInventory(InventoryRequest inventoryRequest) {
        if(inventoryRepository.existsByProductId(inventoryRequest.getProductId())) {
            throw new AppException(ErrorCode.INVENTORY_ALREADY_EXISTS);
        }
        Inventory inventory = inventoryMapper.toEntity(inventoryRequest);
        inventory = inventoryRepository.save(inventory);
        return InventoryResponse.builder()
                .productId(inventory.getProduct().getId())
                .quantity(inventory.getQuantity())
                .build();
    }

    public InventoryResponse updateInventory(Long productId, InventoryRequest inventoryRequest) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOT_FOUND));
        inventory.setQuantity(inventoryRequest.getQuantity());
        inventory = inventoryRepository.save(inventory);
        return InventoryResponse.builder()
                .productId(inventory.getProduct().getId())
                .quantity(inventory.getQuantity())
                .build();

    }
}
