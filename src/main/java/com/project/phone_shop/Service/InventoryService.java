package com.project.phone_shop.Service;

import com.project.phone_shop.DTO.Request.InventoryRequest;
import com.project.phone_shop.DTO.Response.InventoryResponse;
import com.project.phone_shop.Entity.Inventory;
import com.project.phone_shop.Entity.Product;
import com.project.phone_shop.Repository.InventoryRepository;
import com.project.phone_shop.Repository.ProductRepository;
import com.project.phone_shop.exception.AppException;
import com.project.phone_shop.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InventoryService {

    InventoryRepository inventoryRepository;
    ProductRepository productRepository;

    /**
     * Xem toàn bộ tồn kho (ADMIN only)
     */
    @PreAuthorize("hasRole('ADMIN')")
    public List<InventoryResponse> getInventory() {
        return inventoryRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Xem tồn kho theo productId
     */
    public InventoryResponse getInventoryByProductId(Long productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOT_FOUND));
        return toResponse(inventory);
    }

    /**
     * Nhập kho lần đầu cho một sản phẩm (ADMIN only)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public InventoryResponse importInventory(InventoryRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        if (inventoryRepository.existsByProductId(product.getId())) {
            throw new AppException(ErrorCode.INVENTORY_ALREADY_EXISTS);
        }

        Inventory inventory = Inventory.builder()
                .product(product)
                .quantity(request.getQuantity())
                .build();

        inventory = inventoryRepository.save(inventory);

        // Đồng bộ quantity trong Product
        product.setQuantity(request.getQuantity());
        productRepository.save(product);

        return toResponse(inventory);
    }

    /**
     * Cập nhật số lượng tồn kho (ADMIN only) - đặt lại số lượng
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public InventoryResponse updateInventory(Long productId, InventoryRequest request) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOT_FOUND));

        inventory.setQuantity(request.getQuantity());
        inventory = inventoryRepository.save(inventory);

        // Đồng bộ quantity trong Product
        Product product = inventory.getProduct();
        product.setQuantity(request.getQuantity());
        productRepository.save(product);

        return toResponse(inventory);
    }

    /**
     * Nhập thêm hàng vào kho (ADMIN only) - cộng thêm số lượng
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public InventoryResponse addStock(Long productId, InventoryRequest request) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOT_FOUND));

        int newQty = inventory.getQuantity() + request.getQuantity();
        inventory.setQuantity(newQty);
        inventory = inventoryRepository.save(inventory);

        // Đồng bộ quantity trong Product
        Product product = inventory.getProduct();
        product.setQuantity(newQty);
        productRepository.save(product);

        log.info("Added {} units to product {} inventory. New stock: {}", request.getQuantity(), productId, newQty);
        return toResponse(inventory);
    }

    private InventoryResponse toResponse(Inventory inventory) {
        return InventoryResponse.builder()
                .id(inventory.getId())
                .productId(inventory.getProduct().getId())
                .productName(inventory.getProduct().getName())
                .quantity(inventory.getQuantity())
                .build();
    }
}
