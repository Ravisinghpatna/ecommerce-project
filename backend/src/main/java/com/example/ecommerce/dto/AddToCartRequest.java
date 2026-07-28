package com.example.ecommerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO (Data Transfer Object) — ye class sirf ek "shape" hai jo frontend se
 * aane wale JSON request body ko represent karti hai. Entity classes ko
 * directly request body me use karna generally achi practice nahi hai,
 * isliye alag DTOs banate hain.
 */
public class AddToCartRequest {

    @NotNull(message = "productId is required")
    private Long productId;

    @NotNull(message = "quantity is required")
    @Min(value = 1, message = "quantity must be at least 1")
    private Integer quantity;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
