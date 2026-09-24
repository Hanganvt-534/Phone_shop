package com.project.phone_shop.Controller;

import com.project.phone_shop.DTO.Request.ApiResponse;
import com.project.phone_shop.DTO.Request.InventoryRequest;
import com.project.phone_shop.DTO.Response.InventoryResponse;
import com.project.phone_shop.Entity.Inventory;
import com.project.phone_shop.Service.InventoryService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class InventoryController {
    @Autowired
    InventoryService inventoryService;

    @GetMapping
    public ApiResponse<List<InventoryResponse>> getInventory() {
        List<InventoryResponse> inventories = inventoryService.getInventory();
        return ApiResponse.<List<InventoryResponse>>builder()
                .result(inventories)
                .build();
    }

    @GetMapping("/{productId}")
    public ApiResponse<InventoryResponse> getInventory(@PathVariable Long productId) {
        InventoryResponse inventoryResponse = inventoryService.getInventoryByProductId(productId);
        return ApiResponse.<InventoryResponse>builder()
                .result(inventoryResponse)
                .build();
       }

    @PostMapping("/import")
    public ApiResponse<InventoryResponse> importInventory(@RequestBody InventoryRequest inventoryRequest) {
        InventoryResponse inventoryResponse = inventoryService.importInventory(inventoryRequest);
        return ApiResponse.<InventoryResponse>builder()
                .result(inventoryResponse)
                .build();
       }

    @PutMapping("/{productId}")
    public ApiResponse<InventoryResponse> updateInventory(@PathVariable Long productId, @RequestBody InventoryRequest inventoryRequest) {
        InventoryResponse inventoryResponse = inventoryService.updateInventory(productId, inventoryRequest);
        return ApiResponse.<InventoryResponse>builder()
                .result(inventoryResponse)
                .build();
    }




}
