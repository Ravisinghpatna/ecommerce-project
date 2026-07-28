package com.example.ecommerce.exception;

/**
 * Jab bhi koi Product, CartItem, ya Order dhoondha jaaye aur na mile,
 * hum ye exception throw karenge. Neeche GlobalExceptionHandler ise
 * catch karke clean 404 JSON response bana dega.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
