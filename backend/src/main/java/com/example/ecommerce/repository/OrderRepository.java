package com.example.ecommerce.repository;

import com.example.ecommerce.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // Customer ko sirf apni orders dikhani hain, sabki nahi
    List<Order> findByCustomerId(Long customerId);
}
