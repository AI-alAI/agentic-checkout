package com.hackathon.agenticcheckout.controller;

import com.hackathon.agenticcheckout.entity.Cart;
import com.hackathon.agenticcheckout.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CartController {

    private final CartService cartService;

    @GetMapping("/{userEmail}")
    public ResponseEntity<Cart> getCart(@PathVariable String userEmail) {
        return ResponseEntity.ok(cartService.getOrCreateCart(userEmail));
    }

    @PostMapping("/{userEmail}/add")
    public ResponseEntity<Cart> addToCart(
            @PathVariable String userEmail,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(cartService.addItemToCart(userEmail, productId, quantity));
    }
}