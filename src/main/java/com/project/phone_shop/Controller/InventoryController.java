package com.project.phone_shop.Controller;

import com.project.phone_shop.DTO.Request.ApiResponse;
import com.project.phone_shop.DTO.Request.InventoryRequest;
import com.project.phone_shop.DTO.Response.InventoryResponse;
import com.project.phone_shop.Service.InventoryService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InventoryController {

    InventoryService inventoryService;

    /**
     * GET /inventory → Xem toàn bộ tồn kho (ADMIN only)
     */
    @GetMapping
    public ApiResponse<List<InventoryResponse>> getInventory() {
        return ApiResponse.<List<InventoryResponse>>builder()
                .result(inventoryService.getInventory())
                .build();
    }

    /**
     * GET /inventory/{productId} → Xem tồn kho của sản phẩm
     */
    @GetMapping("/{productId}")
    public ApiResponse<InventoryResponse> getInventoryByProduct(@PathVariable Long productId) {
        return ApiResponse.<InventoryResponse>builder()
                .result(inventoryService.getInventoryByProductId(productId))
                .build();
    }

    /**
     * POST /inventory/import → Nhập kho lần đầu (ADMIN only)
     */
    @PostMapping("/import")
    public ApiResponse<InventoryResponse> importInventory(@Valid @RequestBody InventoryRequest request) {
        return ApiResponse.<InventoryResponse>builder()
                .result(inventoryService.importInventory(request))
                .build();
    }

    /**
     * PUT /inventory/{productId} → Đặt lại số lượng tồn kho (ADMIN only)
     */
    @PutMapping("/{productId}")
    public ApiResponse<InventoryResponse> updateInventory(
            @PathVariable Long productId,
            @Valid @RequestBody InventoryRequest request) {
        return ApiResponse.<InventoryResponse>builder()
                .result(inventoryService.updateInventory(productId, request))
                .build();
    }

    /**
     * PATCH /inventory/{productId}/add-stock → Nhập thêm hàng vào kho (ADMIN only)
     */
    @PatchMapping("/{productId}/add-stock")
    public ApiResponse<InventoryResponse> addStock(
            @PathVariable Long productId,
            @Valid @RequestBody InventoryRequest request) {
        return ApiResponse.<InventoryResponse>builder()
                .result(inventoryService.addStock(productId, request))
                .build();
    }
}
