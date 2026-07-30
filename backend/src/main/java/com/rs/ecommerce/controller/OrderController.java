package com.rs.ecommerce.controller;

import com.rs.ecommerce.dto.CheckoutRequest;
import com.rs.ecommerce.model.Order;
import com.rs.ecommerce.security.CurrentUser;
import com.rs.ecommerce.service.InvoiceService;
import com.rs.ecommerce.service.OrderService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final InvoiceService invoiceService;

    @Autowired
    public OrderController(OrderService orderService, InvoiceService invoiceService) {
        this.orderService = orderService;
        this.invoiceService = invoiceService;
    }
     

    // POST /api/orders/checkout -> logged-in customer ke cart se order banata hai
    @PostMapping("/checkout")
    public ResponseEntity<Order> checkout(@Valid @RequestBody CheckoutRequest request) {
        Order order = orderService.checkout(CurrentUser.id(), request);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    // GET /api/orders -> Customer ko sirf apni orders, Admin ko saari orders
    @GetMapping
    public List<Order> getOrders() {
        if (CurrentUser.isAdmin()) {
            return orderService.getAllOrders();
        }
        return orderService.getOrdersForCustomer(CurrentUser.id());
    }
    
    @PutMapping("/{id}/status")
    public Order updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return orderService.updateStatus(id, body.get("status"));
    }
    
 // PUT /api/orders/5/ship  { "courierPartner": "BLUE_DART" }  -- sirf Admin
    @PutMapping("/{id}/ship")
    public Order shipOrder(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return orderService.shipOrder(id, body.get("courierPartner"));
    }
    
 // PUT /api/orders/5/cancel -> Customer apna order cancel karta hai (agar abhi PLACED hai)
    @PutMapping("/{id}/cancel")
    public Order cancelOrder(@PathVariable Long id) {
        return orderService.cancelOrder(CurrentUser.id(), id);
    }
    
 // GET /api/orders/5/invoice -> PDF invoice download karo (Customer apna, Admin kisi ka bhi)
    @GetMapping("/{id}/invoice")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long id) {
        Order order = orderService.getOrderForInvoice(CurrentUser.id(), CurrentUser.isAdmin(), id);
        byte[] pdfBytes = invoiceService.generateInvoice(order);
     
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition.attachment().filename("invoice-order-" + order.getId() + ".pdf").build());
     
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
