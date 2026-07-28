package com.example.ecommerce.repository;

import com.example.ecommerce.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // Sirf isi customer ke cart items lao (dusre customers ka cart nahi dikhna chahiye)
    List<CartItem> findByCustomerId(Long customerId);

    // Add-to-cart pe check karne ke liye: ye product isi customer ke cart me pehle se hai kya
    Optional<CartItem> findByCustomerIdAndProductId(Long customerId, Long productId);

    void deleteByCustomerId(Long customerId);
}
