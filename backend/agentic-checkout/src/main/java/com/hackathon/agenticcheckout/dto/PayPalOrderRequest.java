package com.hackathon.agenticcheckout.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PayPalOrderRequest {
    private Long orderId;        // Notre ID de commande interne
    private BigDecimal amount;   // Montant à payer
    private String currency;     // "USD", "EUR", etc.
}