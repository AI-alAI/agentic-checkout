package com.hackathon.agenticcheckout.service;

import com.hackathon.agenticcheckout.entity.Product;
import com.hackathon.agenticcheckout.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> searchProducts(String category, BigDecimal maxPrice) {
        return productRepository.searchProducts(category, maxPrice);
    }

    public Product addProduct(Product product) {
        return productRepository.save(product);
    }
}