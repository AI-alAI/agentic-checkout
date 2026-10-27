package com.hackathon.agenticcheckout.repository;

import com.hackathon.agenticcheckout.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserEmailOrderByCreatedAtDesc(String userEmail);
    Optional<Order> findByPaypalOrderId(String paypalOrderId);
}