package com.rs.ecommerce.courier;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * DUMMY implementation — kisi real courier se baat nahi karta, bas realistic
 * dikhne wala data simulate karta hai. Production me isko replace kar dena.
 */
@Component
public class DummyCourierServiceImpl implements CourierService {

    // Har courier company ka apna tracking ID prefix hota hai real life me bhi
    // (jaise Blue Dart ke "AWB" numbers ek pattern follow karte hain)
    private static final Map<String, String> PREFIXES = Map.of(
            "BLUE_DART", "BD",
            "DELHIVERY", "DL",
            "SPEED_POST", "SP",
            "DTDC", "DT"
    );

    // Simulated transit cities — real update me courier "kahan pahuncha" bhi batata hai
    private static final List<String> CITIES = List.of("Mumbai", "Delhi", "Bengaluru", "Pune", "Hyderabad");

    @Override
    public String createShipment(Long orderId, String courierPartner) {
        String prefix = PREFIXES.getOrDefault(courierPartner, "XX");
        long randomDigits = ThreadLocalRandom.current().nextLong(100_000_000L, 999_999_999L);
        return prefix + randomDigits;
    }

    @Override
    public TrackingStep getNextUpdate(String trackingId, String courierPartner, int stepNumber) {
        String city = CITIES.get(ThreadLocalRandom.current().nextInt(CITIES.size()));

        // Ek fixed sequence of stages — real courier bhi lagbhag isi tarah ke stages dikhata hai
        return switch (stepNumber) {
            case 0 -> new TrackingStep("Shipment picked up from seller warehouse", null);
            case 1 -> new TrackingStep("In transit — arrived at " + city + " sorting facility", null);
            case 2 -> new TrackingStep("Out for delivery", "OUT_FOR_DELIVERY");
            case 3 -> new TrackingStep("Delivered successfully", "DELIVERED");
            default -> null; // ab koi naya update nahi hai
        };
    }
}