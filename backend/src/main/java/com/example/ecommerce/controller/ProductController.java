package com.example.ecommerce.controller;

import com.example.ecommerce.model.Product;
import com.example.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.ecommerce.dto.ProductRequest;

import java.util.List;

/**
 * @RestController = @Controller + @ResponseBody -> har method ka return
 * value directly JSON me convert ho kar response body me chala jaata hai.
 * @RequestMapping("/api/products") -> is controller ke saare endpoints
 * "/api/products" se shuru honge.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // GET /api/products -> saare products ki list (Shop page ke liye)
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    // GET /api/products/5 -> ek product ki detail
    @GetMapping("/{id}")
    public Product getProduct(@PathVariable Long id) {
        return productService.getProductById(id);
    }

 // POST /api/products -> naya product banao (Admin panel)
    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductRequest request) {
        Product created = productService.createProduct(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
     
    // PUT /api/products/5 -> existing product update karo
    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return productService.updateProduct(id, request);
    }

    // DELETE /api/products/5
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build(); // 204
    }
}
