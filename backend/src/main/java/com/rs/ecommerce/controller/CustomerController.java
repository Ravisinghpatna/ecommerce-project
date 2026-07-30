package com.rs.ecommerce.controller;

import com.rs.ecommerce.dto.CustomerLoginRequest;
import com.rs.ecommerce.dto.CustomerProfileResponse;
import com.rs.ecommerce.dto.LoginResponse;
import com.rs.ecommerce.dto.MessageResponse;
import com.rs.ecommerce.dto.RegisterRequest;
import com.rs.ecommerce.dto.UpdateProfileRequest;
import com.rs.ecommerce.exception.ResourceNotFoundException;
import com.rs.ecommerce.model.Customer;
import com.rs.ecommerce.repository.CustomerRepository;
import com.rs.ecommerce.security.CurrentUser;
import com.rs.ecommerce.security.JwtUtil;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public CustomerController(CustomerRepository customerRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // POST /api/customers/register  { name, email, password, phone }
    // Ab yahan token NAHI bhejte — sirf account bana kar confirm kar dete hain.
    // User ko iske baad seedha /customer-login se manually login karna hoga.
    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody RegisterRequest request) {
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("Already have an Account from this Email Id. Try to Log-in.");
        }

        Customer customer = new Customer(
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()), // kabhi bhi plain password store mat karo
                request.getPhone()
        );
        customerRepository.save(customer);

        return new ResponseEntity<>(
                new MessageResponse("Account is Created Successfully! Try to Log-in."),
                HttpStatus.CREATED
        );
    }

    // POST /api/customers/login  { email, password }
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody CustomerLoginRequest request) {
        Customer customer = customerRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), customer.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(customer.getEmail(), "CUSTOMER", customer.getId());
        return new LoginResponse(token, customer.getName(), "CUSTOMER", customer.getId());
    }
    
 // GET /api/customers/me -> logged-in customer ki apni profile
    @GetMapping("/me")
    public CustomerProfileResponse getProfile() {
        Customer customer = customerRepository.findById(CurrentUser.id())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return new CustomerProfileResponse(customer.getId(), customer.getName(), customer.getEmail(), customer.getPhone());
    }
     
    // PUT /api/customers/me  { name, phone } -> naam/phone update karo
    @PutMapping("/me")
    public CustomerProfileResponse updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        Customer customer = customerRepository.findById(CurrentUser.id())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        customer.setName(request.getName());
        customer.setPhone(request.getPhone());
        customerRepository.save(customer);
        return new CustomerProfileResponse(customer.getId(), customer.getName(), customer.getEmail(), customer.getPhone());
    }
}
