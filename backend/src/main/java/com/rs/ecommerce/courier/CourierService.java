package com.rs.ecommerce.courier;

/**
 * Ye interface hamara "swap point" hai — jaise PaymentGateway tha payment ke liye.
 *
 * Aaj isko DummyCourierServiceImpl implement karta hai (fake tracking ID +
 * simulated updates deta hai). Kal jab REAL courier API lagana ho
 * (Blue Dart, Delhivery, DTDC, Speed Post — sabki apni-apni REST API hoti hai),
 * bas ek nayi class banao jo isi interface ko implement kare
 * (e.g. BlueDartCourierServiceImpl.java), aur @Primary usi par laga do.
 * OrderService, Scheduler, Controller — kahin kuch badalna nahi padega.
 */
public interface CourierService {

    /**
     * Order ko courier ke paas "book" karta hai — real API me ye unke
     * shipment-creation endpoint ko call karta. Return: tracking ID.
     */
    String createShipment(Long orderId, String courierPartner);

    /**
     * Ek tracking ID ka "agla" status update poochta hai. stepNumber batata
     * hai ab tak kitne updates aa chuke hain (0 = pehla update abhi lena hai).
     * Real API me ye unka "track shipment" endpoint call karta.
     * Return null matlab abhi koi naya update nahi hai (ya delivery ho chuki hai).
     */
    TrackingStep getNextUpdate(String trackingId, String courierPartner, int stepNumber);
}