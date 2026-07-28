package com.example.ecommerce.repository;
 
import com.example.ecommerce.model.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
 
public interface WishlistRepository extends JpaRepository<WishlistItem, Long> {
    List<WishlistItem> findByCustomerId(Long customerId);
 
    Optional<WishlistItem> findByCustomerIdAndProductId(Long customerId, Long productId);
 
    @Modifying
    @Transactional
    void deleteByCustomerIdAndProductId(Long customerId, Long productId);
}