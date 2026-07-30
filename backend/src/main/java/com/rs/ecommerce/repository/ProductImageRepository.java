package com.rs.ecommerce.repository;
 
import org.springframework.data.jpa.repository.JpaRepository;

import com.rs.ecommerce.model.ProductImage;
 
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    void deleteByProductId(Long productId);
}