package com.project.phone_shop.Controller;

import com.project.phone_shop.DTO.Request.ApiResponse;
import com.project.phone_shop.DTO.Request.ProductRequest;
import com.project.phone_shop.DTO.Request.ProductSearchRequest;
import com.project.phone_shop.DTO.Response.ProductResponse;
import com.project.phone_shop.Service.ProductService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;


@RestController
@RequestMapping("/products")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class ProductController {
    @Autowired
    ProductService productService;

    @GetMapping
    public ApiResponse<List<ProductResponse>> getProducts() {
        var result = productService.getProducts();
        return ApiResponse.<List<ProductResponse>>builder().result(result).build();
    }


    @GetMapping("/search")
    public ApiResponse<Page<ProductResponse>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        ProductSearchRequest searchRequest = ProductSearchRequest.builder()
                .name(name)
                .brand(brand)
                .category(category)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .build();

        Pageable pageable = PageRequest.of(page, size);

        return ApiResponse.<Page<ProductResponse>>builder()
                .result(productService.search(searchRequest, pageable))
                .build();
    }

    @PostMapping("/add")
    public ApiResponse<ProductResponse> addProduct(@RequestBody ProductRequest productRequest) {
        return ApiResponse.<ProductResponse>builder()
                .result(productService.addProduct(productRequest))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<ProductResponse> updateProduct(@PathVariable Long id, @RequestBody ProductRequest productRequest) {
        return ApiResponse.<ProductResponse>builder()
                .result(productService.updateProduct(id, productRequest))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ApiResponse.<Void>builder()
                .build();
    }


}
