package com.example.ecommerce.dto;
 
import java.math.BigDecimal;
import java.util.List;
 
/**
 * Admin Dashboard ke liye saara summary data ek hi response me — taaki
 * frontend ko baar-baar alag-alag API calls na karni padein.
 */
public class DashboardResponse {
 
    private BigDecimal totalSales;
    private long totalOrders;
    private long totalProducts;
    private long totalCustomers;
    private List<TopProduct> topProducts;
 
    public DashboardResponse(BigDecimal totalSales, long totalOrders, long totalProducts,
                              long totalCustomers, List<TopProduct> topProducts) {
        this.totalSales = totalSales;
        this.totalOrders = totalOrders;
        this.totalProducts = totalProducts;
        this.totalCustomers = totalCustomers;
        this.topProducts = topProducts;
    }
 
    public BigDecimal getTotalSales() {
        return totalSales;
    }
 
    public long getTotalOrders() {
        return totalOrders;
    }
 
    public long getTotalProducts() {
        return totalProducts;
    }
 
    public long getTotalCustomers() {
        return totalCustomers;
    }
 
    public List<TopProduct> getTopProducts() {
        return topProducts;
    }
 
    // Nested class — ek "top selling product" ki entry (naam + kitna becha)
    public static class TopProduct {
        private String productName;
        private int totalQuantitySold;
 
        public TopProduct(String productName, int totalQuantitySold) {
            this.productName = productName;
            this.totalQuantitySold = totalQuantitySold;
        }
 
        public String getProductName() {
            return productName;
        }
 
        public int getTotalQuantitySold() {
            return totalQuantitySold;
        }
    }
}
 