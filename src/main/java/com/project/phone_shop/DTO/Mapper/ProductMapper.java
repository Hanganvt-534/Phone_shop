package com.project.phone_shop.DTO.Mapper;

import com.project.phone_shop.DTO.Request.ProductRequest;
import com.project.phone_shop.DTO.Response.ProductResponse;
import com.project.phone_shop.Entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductResponse toProductResponse(Product product);


    Product toProduct(ProductRequest productRequest);

    void updateProduct(@MappingTarget Product product, ProductRequest productRequest);
}
