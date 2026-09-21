package com.project.phone_shop.Specification;

import com.project.phone_shop.DTO.Request.ProductSearchRequest;
import com.project.phone_shop.Entity.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;


public class ProductSpecification {
    private ProductSpecification() {}

    public static Specification<Product> hasName(String name) {
        return (root, query, cb) -> (name == null || name.isEmpty()) ? null
                : cb.like(cb.lower(root.get("name")), "%" + name.trim().toLowerCase() + "%");
    }

    public static Specification<Product> hasCategory(String category) {
        return (root, query, cb) -> (category == null || category.isEmpty()) ? null
                : cb.equal(cb.lower(root.get("category")), category.trim().toLowerCase());
    }

    public static Specification<Product> hasBrand(String brand) {
        return (root, query, cb) -> (brand == null || brand.isEmpty()) ? null
                : cb.equal(cb.lower(root.get("brand")), brand.trim().toLowerCase());
    }

    public static Specification<Product> priceBetween(BigDecimal min, BigDecimal max) {
        return (root, query, cb) -> (min == null || max == null) ? null
                : cb.between(root.get("price"), min, max);
    }

    // Ghép tất cả điều kiện lại; điều kiện null sẽ được bỏ qua.
    public static Specification<Product> search(ProductSearchRequest filter) {
        return Specification
                .where(hasName(filter.getName()))
                .and(hasCategory(filter.getCategory()))
                .and(hasBrand(filter.getBrand()))
                .and(priceBetween(filter.getMinPrice(), filter.getMaxPrice()));
    }
}
