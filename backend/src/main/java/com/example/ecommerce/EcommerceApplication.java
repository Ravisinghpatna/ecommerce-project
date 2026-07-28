package com.example.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ye application ka entry point hai.
 * `main()` method run hote hi Spring Boot:
 *  1. Embedded Tomcat server start karta hai (default port 8080)
 *  2. Saare @Component / @Service / @Repository / @Controller classes ko scan karke
 *     Spring Container (ApplicationContext) me register karta hai
 *  3. application.properties padh kar database se connect karta hai
 */
@SpringBootApplication
public class EcommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcommerceApplication.class, args);
        System.out.println(">>> E-commerce backend running on http://localhost:8080 <<<");
    }
}
