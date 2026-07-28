package com.example.ecommerce.service;
 
import com.example.ecommerce.dto.ProductRequest;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.ProductImage;
import com.example.ecommerce.repository.ProductImageRepository;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import java.util.List;
 
/**
 * Service layer = business logic yahan rehti hai.
 * Controller sirf HTTP request/response handle karta hai, actual "kaam"
 * (validation, calculations, DB calls arrange karna) Service karta hai.
 * Ye layering testing aur maintainability ke liye important hai.
 */
@Service
public class ProductService {
 
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
 
    // Constructor injection — Spring khud dono Repository ka instance
    // yahan "inject" (pass) kar deta hai jab ye Service bean banata hai.
    @Autowired
    public ProductService(ProductRepository productRepository, ProductImageRepository productImageRepository) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
    }
 
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
 
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }
 
    @Transactional
    public Product createProduct(ProductRequest request) {
        Product product = new Product(
                request.getName(),
                request.getDescription(),
                request.getCategory(),
                request.getPrice(),
                request.getImageUrl(),
                request.getStock()
        );
        Product saved = productRepository.save(product);
        saveAdditionalImages(saved, request.getAdditionalImageUrls());
        return saved;
    }
 
    @Transactional
    public Product updateProduct(Long id, ProductRequest request) {
        Product existing = getProductById(id); // 404 agar exist nahi karta
        existing.setName(request.getName());
        existing.setDescription(request.getDescription());
        existing.setCategory(request.getCategory());
        existing.setPrice(request.getPrice());
        existing.setImageUrl(request.getImageUrl());
        existing.setStock(request.getStock());
        productRepository.save(existing); // id same hone se ye UPDATE karega, INSERT nahi
 
        // Simplest approach: purani additional images hata ke, jo naya list aaya hai wo daal do
        productImageRepository.deleteByProductId(id);
        saveAdditionalImages(existing, request.getAdditionalImageUrls());
        return existing;
    }
 
    public void deleteProduct(Long id) {
        Product existing = getProductById(id);
        productRepository.delete(existing);
    }
 
    private void saveAdditionalImages(Product product, List<String> urls) {
        if (urls == null) {
            return;
        }
        for (String url : urls) {
            if (url != null && !url.isBlank()) {
                productImageRepository.save(new ProductImage(product, url.trim()));
            }
        }
    }
}
 