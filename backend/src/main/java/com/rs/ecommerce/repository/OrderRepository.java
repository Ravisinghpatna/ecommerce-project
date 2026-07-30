package com.rs.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rs.ecommerce.model.Order;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // Customer ko sirf apni orders dikhani hain, sabki nahi
    List<Order> findByCustomerId(Long customerId);
 // Scheduler in statuses wale orders ko hi check karega (jinhe abhi tracking update mil sakta hai)
    List<Order> findByStatusIn(List<String> statuses);
}
