package com.example.ecommerce.model;
 
import jakarta.persistence.*;
 
/**
 * WishlistItem — customer ne kaunse products "save for later" kiye hain.
 * CartItem jaisa hi structure hai, bas quantity nahi hoti — bas
 * "ye product mujhe pasand hai" wala ek simple flag/row.
 */
@Entity
@Table(name = "wishlist_items")
public class WishlistItem {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @Column(nullable = false)
    private Long customerId;
 
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
 
    public WishlistItem() {
    }
 
    public WishlistItem(Long customerId, Product product) {
        this.customerId = customerId;
        this.product = product;
    }
 
    public Long getId() {
        return id;
    }
 
    public void setId(Long id) {
        this.id = id;
    }
 
    public Long getCustomerId() {
        return customerId;
    }
 
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
 
    public Product getProduct() {
        return product;
    }
 
    public void setProduct(Product product) {
        this.product = product;
    }
}
 