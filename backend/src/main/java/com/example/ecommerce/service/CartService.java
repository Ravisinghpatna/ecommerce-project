package com.example.ecommerce.service;

import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductService productService;

    @Autowired
    public CartService(CartItemRepository cartItemRepository, ProductService productService) {
        this.cartItemRepository = cartItemRepository;
        this.productService = productService;
    }

    public List<CartItem> getCartItems(Long customerId) {
        return cartItemRepository.findByCustomerId(customerId);
    }

    /**
     * Agar product pehle se ISI customer ke cart me hai -> quantity badha do.
     * Agar nahi hai -> naya CartItem row bana do (isi customerId ke saath).
     */
    public CartItem addToCart(Long customerId, Long productId, Integer quantity) {
        Product product = productService.getProductById(productId); // exist check + fetch

        return cartItemRepository.findByCustomerIdAndProductId(customerId, productId)
                .map(existingItem -> {
                    existingItem.setQuantity(existingItem.getQuantity() + quantity);
                    return cartItemRepository.save(existingItem);
                })
                .orElseGet(() -> cartItemRepository.save(new CartItem(customerId, product, quantity)));
    }

    // customerId bhi check karte hain taaki koi customer dusre ke cart item ko edit na kar sake
    public CartItem updateQuantity(Long customerId, Long cartItemId, Integer quantity) {
        CartItem item = getOwnedCartItem(customerId, cartItemId);
        item.setQuantity(quantity);
        return cartItemRepository.save(item);
    }

    public void removeFromCart(Long customerId, Long cartItemId) {
        CartItem item = getOwnedCartItem(customerId, cartItemId);
        cartItemRepository.delete(item);
    }

    public void clearCart(Long customerId) {
        cartItemRepository.deleteByCustomerId(customerId);
    }

    // Helper: cart item dhoondo AUR check karo ki ye isi customer ka hai
    private CartItem getOwnedCartItem(Long customerId, Long cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        if (!item.getCustomerId().equals(customerId)) {
            throw new ResourceNotFoundException("Cart item not found with id: " + cartItemId);
        }
        return item;
    }
}
