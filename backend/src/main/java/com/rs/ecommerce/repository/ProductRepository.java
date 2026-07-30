package com.rs.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rs.ecommerce.model.Product;

/**
 * JpaRepository<Product, Long> extend karte hi humein FREE me mil jaate hain:
 * save(), findById(), findAll(), deleteById(), count() etc.
 * Spring Boot runtime pe iska implementation khud generate kar deta hai —
 * hume ek line bhi SQL likhne ki zaroorat nahi padti.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Zaroorat padne par yahan custom finder methods add kar sakte hain, e.g.:
    // List<Product> findByNameContainingIgnoreCase(String keyword);
}
