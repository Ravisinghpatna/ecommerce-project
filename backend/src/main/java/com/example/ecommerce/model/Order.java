package com.example.ecommerce.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

/**
 * Order entity — jab user "checkout" karta hai to cart ke items se
 * ek Order + uske OrderItems ban jaate hain, aur cart khaali ho jaata hai.
 * Ye e-commerce ka standard flow hai: Cart (temporary) -> Order (permanent record).
 */
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime orderDate;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    // Ye order kis customer ne place kiya — isi se "meri orders" filter hoti hai
    @Column(nullable = false)
    private Long customerId;

    // Checkout ke waqt liye gaye shipping/contact details
    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private String customerEmail;

    @Column(nullable = false)
    private String customerPhone;

    @Column(nullable = false, length = 500)
    private String shippingAddress;

    // Simple order status tracking (real app me ye enum ho sakta hai: PLACED, SHIPPED, DELIVERED...)
    @Column(nullable = false)
    private String status = "PLACED";
    
    private LocalDateTime estimatedDelivery;
    private String trackingId;
    
 @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
 @OrderBy("timestamp ASC")
 private List<OrderStatusHistory> statusHistory = new ArrayList<>();

    // OrderItem me hi @ManyToOne Order lagega -> One Order has Many OrderItems
    // mappedBy = "order" batata hai ki relationship OrderItem class ke `order` field se control hoti hai
    // cascade ALL -> Order save/delete hone par uske items bhi save/delete honge
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<OrderItem> items = new ArrayList<>();

    public Order() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getEstimatedDelivery() {
        return estimatedDelivery;
    }
     
    public void setEstimatedDelivery(LocalDateTime estimatedDelivery) {
        this.estimatedDelivery = estimatedDelivery;
    }
     
    public String getTrackingId() {
        return trackingId;
    }
     
    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }
    
    public List<OrderStatusHistory> getStatusHistory() {
        return statusHistory;
    }
     
    public void setStatusHistory(List<OrderStatusHistory> statusHistory) {
        this.statusHistory = statusHistory;
    }
     
    public void addStatusHistory(String status, LocalDateTime when) {
        OrderStatusHistory entry = new OrderStatusHistory(this, status, when);
        statusHistory.add(entry);
    }

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }
}
