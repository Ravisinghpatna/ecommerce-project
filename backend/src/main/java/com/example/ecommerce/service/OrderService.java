package com.example.ecommerce.service;

import com.example.ecommerce.dto.CheckoutRequest;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.OrderItem;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private static final List<String> VALID_STATUSES =List.of("PLACED", "SHIPPED", "OUT_FOR_DELIVERY", "DELIVERED", "CANCELLED");

    @Autowired
    public OrderService(OrderRepository orderRepository, CartItemRepository cartItemRepository,
                         ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    /**
     * @Transactional -> saare DB operations is method ke andar ek hi
     * "transaction" me chalte hain. Agar beech me kuch fail ho jaaye
     * (e.g. stock kam pada), to sab kuch rollback ho jaata hai —
     * na order banega, na stock kam hoga, na cart khaali hoga.
     */
    @Transactional
    public Order checkout(Long customerId, CheckoutRequest request) {
        List<CartItem> cartItems = cartItemRepository.findByCustomerId(customerId);
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cart is empty, cannot checkout");
        }

        // Order confirm karne se PEHLE har item ka stock check kar lo —
        // taaki kisi aur ne beech me wahi product na khareed liya ho
        for (CartItem cartItem : cartItems) {
            if (cartItem.getProduct().getStock() < cartItem.getQuantity()) {
                throw new IllegalStateException(
                        "Not enough stock for " + cartItem.getProduct().getName()
                                + " (available: " + cartItem.getProduct().getStock() + ")");
            }
        }

        Order order = new Order();
        order.setCustomerId(customerId);
        LocalDateTime now = LocalDateTime.now();
        order.setOrderDate(now);
        order.setEstimatedDelivery(now.plusDays(5)); // 5-din ka estimate — jitna chaho utna rakh sakte ho
        order.setCustomerName(request.getCustomerName());
        order.setCustomerEmail(request.getCustomerEmail());
        order.setCustomerPhone(request.getCustomerPhone());
        order.setShippingAddress(request.getShippingAddress());
        order.setStatus("PLACED");
        order.addStatusHistory("PLACED", now);

        BigDecimal total = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            total = total.add(lineTotal);

            OrderItem orderItem = new OrderItem(product, cartItem.getQuantity(), product.getPrice());
            order.addItem(orderItem);

            // Stock kam karo — order confirm hote hi inventory update hona chahiye
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
        }
        order.setTotalAmount(total);

        Order savedOrder = orderRepository.save(order); // cascade = ALL -> items bhi save ho jaate hain
        cartItemRepository.deleteByCustomerId(customerId); // sirf isi customer ka cart khaali hoga
        return savedOrder;
    }

    public List<Order> getOrdersForCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
 // Invoice download karne se pehle check: ye order isi customer ka hai (ya request Admin ki taraf se hai)
    public Order getOrderForInvoice(Long customerId, boolean isAdmin, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
     
        if (!isAdmin && !order.getCustomerId().equals(customerId)) {
            throw new IllegalStateException("You can only view your own invoices");
        }
        return order;
    }
    
    public Order updateStatus(Long orderId, String newStatus) {
        if (!VALID_STATUSES.contains(newStatus)) {
            throw new IllegalStateException("Invalid status: " + newStatus);
        }
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
     
        order.setStatus(newStatus);
        order.addStatusHistory(newStatus, LocalDateTime.now());
     
        if ("SHIPPED".equals(newStatus) && order.getTrackingId() == null) {
            order.setTrackingId(generateTrackingId());
        }
     
        return orderRepository.save(order);
    }
     
    private String generateTrackingId() {
        long randomDigits = java.util.concurrent.ThreadLocalRandom.current().nextLong(100_000_000L, 999_999_999L);
        return "TRK" + randomDigits;
    }
    
    /**
     * Customer apna order cancel karta hai — lekin sirf tab jab abhi tak
     * SHIPPED nahi hua ho. Stock bhi WAPAS add kar dete hain, kyunki ab wo
     * items becheinge nahi.
     */
    @Transactional
    public Order cancelOrder(Long customerId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
     
        // Security check: customer sirf apna hi order cancel kar sakta hai, kisi aur ka nahi
        if (!order.getCustomerId().equals(customerId)) {
            throw new IllegalStateException("You can only cancel your own orders");
        }
     
        // Sirf PLACED status wale order hi cancel ho sakte hain
        if (!"PLACED".equals(order.getStatus())) {
            throw new IllegalStateException(
                    "Order cannot be cancelled — it has already been " + order.getStatus().toLowerCase());
        }
     
        // Stock wapas badha do — jo bhi quantity is order me thi, wo dobara available ho jaaye
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }
     
        order.setStatus("CANCELLED");
        order.addStatusHistory("CANCELLED", LocalDateTime.now());
        return orderRepository.save(order);
    }
     
}
