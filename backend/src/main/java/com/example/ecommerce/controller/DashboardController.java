package com.example.ecommerce.controller;
 
import com.example.ecommerce.dto.DashboardResponse;
import com.example.ecommerce.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
 
@RestController
@RequestMapping("/api/admin")
public class DashboardController {
 
    private final DashboardService dashboardService;
 
    @Autowired
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }
 
    // GET /api/admin/dashboard -> sirf Admin access kar sakta hai (SecurityConfig me rule hai)
    @GetMapping("/dashboard")
    public DashboardResponse getDashboard() {
        return dashboardService.getDashboard();
    }
}