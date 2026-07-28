package com.example.ecommerce.controller;

import com.example.ecommerce.dto.AddToCartRequest;
import com.example.ecommerce.dto.UpdateCartRequest;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.security.CurrentUser;
import com.example.ecommerce.service.CartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Har endpoint yahan CurrentUser.id() use karta hai — ye JWT token se nikaala
 * gaya logged-in customer ka id hai. Isi wajah se har customer ka cart alag
 * rehta hai: koi bhi query hamesha "sirf isi customer ke items" tak simit hai.
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // GET /api/cart -> sirf logged-in customer ke cart items
    @GetMapping
    public List<CartItem> getCart() {
        return cartService.getCartItems(CurrentUser.id());
    }

    // POST /api/cart/add  { "productId": 1, "quantity": 2 }
    @PostMapping("/add")
    public CartItem addToCart(@Valid @RequestBody AddToCartRequest request) {
        return cartService.addToCart(CurrentUser.id(), request.getProductId(), request.getQuantity());
    }

    // PUT /api/cart/update/3  { "quantity": 5 }
    @PutMapping("/update/{cartItemId}")
    public CartItem updateQuantity(@PathVariable Long cartItemId, @Valid @RequestBody UpdateCartRequest request) {
        return cartService.updateQuantity(CurrentUser.id(), cartItemId, request.getQuantity());
    }

    // DELETE /api/cart/remove/3
    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long cartItemId) {
        cartService.removeFromCart(CurrentUser.id(), cartItemId);
        return ResponseEntity.noContent().build();
    }

    // DELETE /api/cart/clear
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart() {
        cartService.clearCart(CurrentUser.id());
        return ResponseEntity.noContent().build();
    }
}
