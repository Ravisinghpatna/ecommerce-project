package com.example.ecommerce.model;
 
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
 
/**
* ProductImage — ek product ki EXTRA images (primary image Product.imageUrl
* me hi rehta hai, ye sirf additional gallery images ke liye hai).
* Product Detail Page pe in sabko ek gallery/thumbnail strip me dikhaya jaata hai.
*/
@Entity
@Table(name = "product_images")
public class ProductImage {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore // warna Product -> images -> product -> images... infinite loop ban jaata JSON me
    private Product product;
 
    @Column(nullable = false, length = 2000)
    private String imageUrl;
 
    public ProductImage() {
    }
 
    public ProductImage(Product product, String imageUrl) {
        this.product = product;
        this.imageUrl = imageUrl;
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
 
    public String getImageUrl() {
        return imageUrl;
    }
 
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}