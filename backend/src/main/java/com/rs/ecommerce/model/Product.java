package com.rs.ecommerce.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Product entity — is class ka har object database ke "products" table
 * me ek row ke barabar hota hai. Ye JPA/Hibernate ka core concept hai:
 * Java object <-> Database row (ORM: Object Relational Mapping).
 */
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment primary key
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;
    
    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "image_url", length = 2000)
    private String imageUrl;

    @Column(nullable = false)
    private Integer stock;
    
 // Primary image alag hai (upar imageUrl) — ye extra/gallery images hain,
 // jo Product Detail Page pe thumbnail strip ki tarah dikhti hain
 @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
 private List<ProductImage> images = new ArrayList<>();

    // JPA ko no-arg constructor chahiye hota hai object banane ke liye
    public Product() {
    }

    public Product(String name, String description, String category, BigDecimal price, String imageUrl, Integer stock) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
        this.imageUrl = imageUrl;
        this.stock = stock;
    }

    // ---------------- Getters & Setters ----------------
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getCategory() {
        return category;
    }
     
    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
    
    public List<ProductImage> getImages() {
        return images;
    }
     
    public void setImages(List<ProductImage> images) {
        this.images = images;
    }
}
