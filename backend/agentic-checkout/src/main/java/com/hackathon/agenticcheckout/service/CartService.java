package com.hackathon.agenticcheckout.service;

import com.hackathon.agenticcheckout.entity.Cart;
import com.hackathon.agenticcheckout.entity.CartItem;
import com.hackathon.agenticcheckout.entity.Product;
import com.hackathon.agenticcheckout.repository.CartItemRepository;
import com.hackathon.agenticcheckout.repository.CartRepository;
import com.hackathon.agenticcheckout.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public Cart getOrCreateCart(String userEmail) {
        return cartRepository.findByUserEmail(userEmail)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUserEmail(userEmail);
                    return cartRepository.save(cart);
                });
    }

    @Transactional
    public Cart addItemToCart(String userEmail, Long productId, Integer quantity) {
        Cart cart = getOrCreateCart(userEmail);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        Optional<CartItem> existingItem = cartItemRepository.findByCartAndProduct(cart, product);

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            item.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
            cartItemRepository.save(newItem);
        }

        Cart updatedCart = cartRepository.findByUserEmail(userEmail).orElseThrow();
        BigDecimal total = updatedCart.getItems().stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        updatedCart.setTotalPrice(total);
        
        return cartRepository.save(updatedCart);
    }
}