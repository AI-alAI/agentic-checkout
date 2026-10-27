package com.hackathon.agenticcheckout.controller;

import com.hackathon.agenticcheckout.dto.PayPalOrderRequest;
import com.hackathon.agenticcheckout.dto.PayPalOrderResponse;
import com.hackathon.agenticcheckout.service.PayPalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/paypal")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PayPalController {

    private final PayPalService payPalService;

    @PostMapping("/create")
    public ResponseEntity<PayPalOrderResponse> createOrder(@RequestBody PayPalOrderRequest request) {
        return ResponseEntity.ok(payPalService.createPayPalOrder(request));
    }

    @PostMapping("/capture/{paypalOrderId}")
    public ResponseEntity<Map<String, String>> capture(@PathVariable String paypalOrderId) {
        String status = payPalService.capturePayment(paypalOrderId);
        return ResponseEntity.ok(Map.of("status", status));
    }

    // Webhook pour PayPal (si l'utilisateur ferme l'onglet, PayPal appelle cette URL)
    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(@RequestBody Map<String, Object> payload) {
        String eventType = (String) payload.get("event_type");
        System.out.println("🔔 Webhook reçu de PayPal: " + eventType);
        
        // La vraie vérification de signature (paypal-transmission-sig) se ferait ici.
        if ("PAYMENT.CAPTURE.COMPLETED".equals(eventType)) {
            // On pourrait déclencher la capture en arrière-plan ici
        }
        return ResponseEntity.ok().build();
    }
}