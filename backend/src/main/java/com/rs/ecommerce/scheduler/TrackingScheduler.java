package com.rs.ecommerce.scheduler;

import com.rs.ecommerce.courier.CourierService;
import com.rs.ecommerce.courier.TrackingStep;
import com.rs.ecommerce.model.Order;
import com.rs.ecommerce.repository.OrderRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Ye class har ghante khud-ba-khud chalti hai (Spring ka @Scheduled) —
 * saare "SHIPPED" ya "OUT_FOR_DELIVERY" orders ko check karti hai aur
 * courier se "agla update kya hai" poochti hai. Real courier API me ye
 * unke tracking endpoint ko call karta, abhi DummyCourierServiceImpl
 * simulate kar raha hai.
 */
@Component
public class TrackingScheduler {

    private final OrderRepository orderRepository;
    private final CourierService courierService;

    @Autowired
    public TrackingScheduler(OrderRepository orderRepository, CourierService courierService) {
        this.orderRepository = orderRepository;
        this.courierService = courierService;
    }

    /**
     * fixedRate = 3600000 -> 3600000 ms = 60 minutes = 1 ghanta.
     * TESTING KE LIYE: is number ko temporarily 60000 (1 minute) kar do,
     * taaki turant result dikh jaaye — baad me wapas 3600000 kar dena.
     */
    @Transactional
   // @Scheduled(fixedRate = 3600000)
    @Scheduled(fixedRate = 60000)
    public void pollTrackingUpdates() {
        List<Order> activeOrders = orderRepository.findByStatusIn(List.of("SHIPPED", "OUT_FOR_DELIVERY"));

        for (Order order : activeOrders) {
            // "Shipment booked with..." wala pehla message shipOrder() me already add ho chuka hai,
            // isliye us hisaab se agla step number nikaalte hain
            int stepNumber = order.getTrackingUpdates().size() - 1;

            TrackingStep step = courierService.getNextUpdate(order.getTrackingId(), order.getCourierPartner(), stepNumber);
            if (step == null) {
                continue; // is order ke liye abhi koi naya update nahi hai
            }

            LocalDateTime now = LocalDateTime.now();
            order.addTrackingUpdate(step.getMessage(), now);

            if (step.getNewStatus() != null) {
                order.setStatus(step.getNewStatus());
                order.addStatusHistory(step.getNewStatus(), now);
            }

            orderRepository.save(order);
        }
    }
}