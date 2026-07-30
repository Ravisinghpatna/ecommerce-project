package com.rs.ecommerce.service;
import org.springframework.transaction.annotation.Transactional;

import com.rs.ecommerce.model.Product;
import com.rs.ecommerce.model.WishlistItem;
import com.rs.ecommerce.repository.WishlistRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 
import java.util.List;
 
@Service
public class WishlistService {
 
    private final WishlistRepository wishlistRepository;
    private final ProductService productService;
 
    @Autowired
    public WishlistService(WishlistRepository wishlistRepository, ProductService productService) {
        this.wishlistRepository = wishlistRepository;
        this.productService = productService;
    }
 
    public List<WishlistItem> getWishlist(Long customerId) {
        return wishlistRepository.findByCustomerId(customerId);
    }
 
    public WishlistItem addToWishlist(Long customerId, Long productId) {
        // Agar pehle se wishlist me hai to dobara add mat karo (duplicate rok do)
        return wishlistRepository.findByCustomerIdAndProductId(customerId, productId)
                .orElseGet(() -> {
                    Product product = productService.getProductById(productId); // 404 agar exist nahi karta
                    return wishlistRepository.save(new WishlistItem(customerId, product));
                });
    }
    @Transactional
    public void removeFromWishlist(Long customerId, Long productId) {
        wishlistRepository.deleteByCustomerIdAndProductId(customerId, productId);
    }
}
 