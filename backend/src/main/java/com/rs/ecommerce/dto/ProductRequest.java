package com.rs.ecommerce.dto;
 
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
 
/**
* Product add/edit karte waqt frontend se ye shape aati hai — Product entity
* seedha request body me use karne ke bajaye, ye DTO use karte hain taaki
* "additionalImageUrls" jaisi extra cheez bhi aasani se le sakein.
*/
public class ProductRequest {
 
    @NotBlank(message = "Name is required")
    private String name;
 
    private String description;
 
    @NotBlank(message = "Category is required")
    private String category;
 
    @NotNull(message = "Price is required")
    private BigDecimal price;
 
    private String imageUrl; // primary/thumbnail image
 
    @NotNull(message = "Stock is required")
    private Integer stock;
 
    // Extra gallery images — frontend se ek list of URLs aati hai
    private List<String> additionalImageUrls;
 
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
 
    public List<String> getAdditionalImageUrls() {
        return additionalImageUrls;
    }
 
    public void setAdditionalImageUrls(List<String> additionalImageUrls) {
        this.additionalImageUrls = additionalImageUrls;
    }
}