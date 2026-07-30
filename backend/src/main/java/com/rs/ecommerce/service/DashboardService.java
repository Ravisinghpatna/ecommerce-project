package com.rs.ecommerce.service;
 
import com.rs.ecommerce.dto.DashboardResponse;
import com.rs.ecommerce.model.Order;
import com.rs.ecommerce.model.OrderItem;
import com.rs.ecommerce.repository.CustomerRepository;
import com.rs.ecommerce.repository.OrderRepository;
import com.rs.ecommerce.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
 
@Service
public class DashboardService {
 
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
 
    @Autowired
    public DashboardService(OrderRepository orderRepository, ProductRepository productRepository,
                             CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }
 
    // @Transactional(readOnly = true) -> Order ke andar ka "items" list LAZY load
    // hota hai (matlab jab tak zaroorat na ho, database se load hi nahi hota).
    // Isse access karne ke liye database session "open" rehna chahiye — ye
    // annotation hi wo session poore method ke chalte rakhta hai.
    @Transactional(readOnly = true)
    public DashboardResponse getDashboard() {
        List<Order> allOrders = orderRepository.findAll();
 
        // Total sales = saari orders ke totalAmount ka sum
        BigDecimal totalSales = allOrders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
 
        // Top selling products: har order ke items nikaal ke product-naam ke
        // hisaab se group karo, phir quantities jod do
        Map<String, Integer> quantityByProduct = allOrders.stream()
                .flatMap(order -> order.getItems().stream())
                .collect(Collectors.groupingBy(
                        item -> item.getProduct().getName(),
                        Collectors.summingInt(OrderItem::getQuantity)
                ));
 
        List<DashboardResponse.TopProduct> topProducts = quantityByProduct.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .map(entry -> new DashboardResponse.TopProduct(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
 
        return new DashboardResponse(
                totalSales,
                orderRepository.count(),
                productRepository.count(),
                customerRepository.count(),
                topProducts
        );
    }
}