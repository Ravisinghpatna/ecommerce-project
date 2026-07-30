package com.rs.ecommerce.model;
 
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
 
/**
* OrderStatusHistory — har baar jab order ka status badalta hai
* (Placed -> Shipped -> Out for Delivery -> Delivered), ek naya row
* yahan ban jaata hai apne timestamp ke saath. Isi se Amazon/Flipkart
* jaisa "Shipped on 24 July, 3:45 PM" wala real tracking timeline banta hai
* — sirf current status nahi, balki HAR stage ka actual time pata rehta hai.
*/
@Entity
@Table(name = "order_status_history")
public class OrderStatusHistory {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore // warna Order -> history -> order -> history... infinite loop ban jaata JSON me
    private Order order;
 
    @Column(nullable = false)
    private String status;
 
    @Column(nullable = false)
    private LocalDateTime timestamp;
 
    public OrderStatusHistory() {
    }
 
    public OrderStatusHistory(Order order, String status, LocalDateTime timestamp) {
        this.order = order;
        this.status = status;
        this.timestamp = timestamp;
    }
 
    public Long getId() {
        return id;
    }
 
    public void setId(Long id) {
        this.id = id;
    }
 
    public Order getOrder() {
        return order;
    }
 
    public void setOrder(Order order) {
        this.order = order;
    }
 
    public String getStatus() {
        return status;
    }
 
    public void setStatus(String status) {
        this.status = status;
    }
 
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
 
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}