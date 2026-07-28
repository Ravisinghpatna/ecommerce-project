package com.example.ecommerce.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Checkout karte waqt frontend se customer ki shipping/contact details
 * aati hain is shape me. Cart me kya hai ye backend khud cart table se
 * uthata hai — customer sirf "kahaan bhejna hai" wo details bhejta hai.
 */
public class CheckoutRequest {

    @NotBlank(message = "Name is required")
    private String customerName;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email")
    private String customerEmail;

    @NotBlank(message = "Phone number is required")
    private String customerPhone;

    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;

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
}
