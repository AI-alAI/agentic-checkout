package com.hackathon.agenticcheckout.dto;

import lombok.Data;

@Data
public class PayPalOrderResponse {
    private String id;           // PayPal Order ID
    private String status;       // "CREATED", "APPROVED", "COMPLETED"
    private String approveUrl;   // URL pour approuver le paiement
}