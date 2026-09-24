package com.project.phone_shop.Service;

import com.project.phone_shop.DTO.Mapper.ProductMapper;
import com.project.phone_shop.DTO.Request.ProductRequest;
import com.project.phone_shop.DTO.Request.ProductSearchRequest;
import com.project.phone_shop.DTO.Response.ProductResponse;
import com.project.phone_shop.Entity.Product;
import com.project.phone_shop.Repository.ProductRepository;
import com.project.phone_shop.Specification.ProductSpecification;
import com.project.phone_shop.exception.AppException;
import com.project.phone_shop.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    final ProductRepository productRepository;
    final ProductMapper productMapper;

    public List<ProductResponse> getProducts() {
        return productRepository.findAll().stream().map(productMapper::toProductResponse).toList();
    }

    public Page<ProductResponse> search(ProductSearchRequest productSearchRequest, Pageable pageable) {
        Specification<Product> spec = ProductSpecification.search(productSearchRequest);
        return productRepository.findAll(spec, pageable).map(productMapper::toProductResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse addProduct(ProductRequest productRequest) {
       if(productRepository.existsByName(productRequest.getName())) {
          throw new AppException(ErrorCode.PRODUCT_EXISTS);
       }
         Product product = productMapper.toProduct(productRequest);
          product = productRepository.save(product);
          return productMapper.toProductResponse(product);
    }




    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        productMapper.updateProduct(product, productRequest);
        Product updatedProduct = productRepository.save(product);
        return productMapper.toProductResponse(updatedProduct);
    }


    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        productRepository.delete(product);
    }

    public void sellProduct(Long id, ProductRequest productRequest) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        if (product.getQuantity() < productRequest.getQuantity()) {
            throw new AppException(ErrorCode.INSUFFICIENT_QUANTITY);
        }

        product.setQuantity(product.getQuantity() - productRequest.getQuantity());
        productRepository.save(product);

    }

}
