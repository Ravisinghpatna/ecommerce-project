package com.example.ecommerce.dto;

/**
 * Register ke baad ab token nahi bhejte (user ko manually login karna hai),
 * bas ek confirmation message wapas bhejte hain.
 */
public class MessageResponse {

    private String message;

    public MessageResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
