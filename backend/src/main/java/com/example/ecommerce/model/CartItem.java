package com.example.ecommerce.model;

import jakarta.persistence.*;

/**
 * CartItem entity — cart me har row ek customer + product + quantity
 * represent karti hai. Har customer ka apna alag cart hota hai (customerId
 * se link), taaki ek customer doosre ka cart na dekh sake.
 */
@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Ye cart item kis customer ka hai — isi se har customer ka cart alag rehta hai
    @Column(nullable = false)
    private Long customerId;

    // Many CartItems ek Product ko point kar sakte hain (yahan practically ek hi karega,
    // kyunki hum add-to-cart pe existing item dhoondh kar quantity badhate hain)
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    public CartItem() {
    }

    public CartItem(Long customerId, Product product, Integer quantity) {
        this.customerId = customerId;
        this.product = product;
        this.quantity = quantity;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
