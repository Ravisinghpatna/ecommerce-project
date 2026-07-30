package com.rs.ecommerce.courier;

/**
 * Ek tracking update ka result — message (jo customer ko dikhega) aur
 * optionally naya order status (agar is update se order ka status bhi
 * badalna chahiye, e.g. "Out for delivery" -> status OUT_FOR_DELIVERY).
 */
public class TrackingStep {

    private final String message;
    private final String newStatus; // null ho sakta hai — matlab sirf message hai, status nahi badla

    public TrackingStep(String message, String newStatus) {
        this.message = message;
        this.newStatus = newStatus;
    }

    public String getMessage() {
        return message;
    }

    public String getNewStatus() {
        return newStatus;
    }
}