package com.hackathon.agenticcheckout.controller;

import com.hackathon.agenticcheckout.entity.Order;
import com.hackathon.agenticcheckout.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/{userEmail}/checkout")
    public ResponseEntity<Order> checkout(@PathVariable String userEmail) {
        return ResponseEntity.ok(orderService.createOrderFromCart(userEmail));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @GetMapping("/user/{userEmail}")
    public ResponseEntity<List<Order>> getUserOrders(@PathVariable String userEmail) {
        return ResponseEntity.ok(orderService.getOrdersByUser(userEmail));
    }
}