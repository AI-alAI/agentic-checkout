package com.hackathon.agenticcheckout.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Data
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String category; // e.g., "Laptop", "Phone"
    private BigDecimal price;
    private Integer stockQuantity;
    private String imageUrl;
}