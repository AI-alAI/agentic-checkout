package com.hackathon.agenticcheckout.repository;

import com.hackathon.agenticcheckout.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // هذا الاستعلام سيساعد الـ AI لاحقاً للبحث عن "لابتوب تحت 8000"
    @Query("SELECT p FROM Product p WHERE " +
           "(:category IS NULL OR p.category = :category) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice)")
    List<Product> searchProducts(@Param("category") String category, @Param("maxPrice") BigDecimal maxPrice);
}