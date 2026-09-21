package com.project.phone_shop.DTO.Mapper;

import com.project.phone_shop.DTO.Request.InventoryRequest;
import com.project.phone_shop.DTO.Response.InventoryResponse;
import com.project.phone_shop.Entity.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    InventoryResponse toResponse(Inventory inventory);

    @Mapping(target = "product.id", source = "productId")
    Inventory toEntity(InventoryRequest request);
}
