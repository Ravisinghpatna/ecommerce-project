package com.example.ecommerce.controller;
 
import com.example.ecommerce.model.WishlistItem;
import com.example.ecommerce.security.CurrentUser;
import com.example.ecommerce.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
 
import java.util.List;
import java.util.Map;
 
@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {
 
    private final WishlistService wishlistService;
 
    @Autowired
    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }
 
    // GET /api/wishlist -> logged-in customer ki poori wishlist
    @GetMapping
    public List<WishlistItem> getWishlist() {
        return wishlistService.getWishlist(CurrentUser.id());
    }
 
    // POST /api/wishlist/add  { "productId": 5 }
    @PostMapping("/add")
    public WishlistItem addToWishlist(@RequestBody Map<String, Long> body) {
        return wishlistService.addToWishlist(CurrentUser.id(), body.get("productId"));
    }
 
    // DELETE /api/wishlist/remove/5  (5 = productId)
    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<Void> removeFromWishlist(@PathVariable Long productId) {
        wishlistService.removeFromWishlist(CurrentUser.id(), productId);
        return ResponseEntity.noContent().build();
    }
}