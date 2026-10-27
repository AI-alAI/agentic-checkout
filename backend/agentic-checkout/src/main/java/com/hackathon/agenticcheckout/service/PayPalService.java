package com.hackathon.agenticcheckout.service;

import com.hackathon.agenticcheckout.config.PayPalConfig;
import com.hackathon.agenticcheckout.dto.PayPalOrderRequest;
import com.hackathon.agenticcheckout.dto.PayPalOrderResponse;
import com.hackathon.agenticcheckout.entity.Order;
import com.hackathon.agenticcheckout.exception.BadRequestException;
import com.hackathon.agenticcheckout.exception.ConflictException;
import com.hackathon.agenticcheckout.exception.PaymentGatewayException;
import com.hackathon.agenticcheckout.exception.ResourceNotFoundException;
import com.hackathon.agenticcheckout.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PayPalService {

    private final PayPalConfig payPalConfig;
    private final RestTemplate restTemplate;
    private final OrderRepository orderRepository;

    // Cache pour le token OAuth
    private String cachedToken;
    private Instant tokenExpiry = Instant.now();

    public synchronized String getAccessToken() {
        // On utilise le token en cache s'il est encore valide (avec 60s de marge)
        if (cachedToken != null && Instant.now().isBefore(tokenExpiry.minusSeconds(60))) {
            return cachedToken;
        }

        String auth = payPalConfig.getClientId() + ":" + payPalConfig.getClientSecret();
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encodedAuth);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    payPalConfig.getBaseUrl() + "/v1/oauth2/token", request, Map.class);

            Map<String, Object> responseBody = response.getBody();
            cachedToken = (String) responseBody.get("access_token");
            Integer expiresIn = (Integer) responseBody.get("expires_in");
            tokenExpiry = Instant.now().plusSeconds(expiresIn);

            return cachedToken;
        } catch (Exception e) {
            throw new PaymentGatewayException("Impossible d'obtenir le token PayPal", e);
        }
    }

    public PayPalOrderResponse createPayPalOrder(PayPalOrderRequest request) {
        // 1. Valider la commande locale D'ABORD
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + request.getOrderId()));

        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new ConflictException("Order is not in PENDING state. Current: " + order.getStatus());
        }

        if (order.getPaypalOrderId() != null) {
            throw new ConflictException("A PayPal order already exists for this order.");
        }

        String accessToken = getAccessToken();
        
        // 2. Le backend calcule le montant (l'IA n'a pas son mot à dire)
        String amountStr = order.getTotalAmount().setScale(2, RoundingMode.HALF_UP).toPlainString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("PayPal-Request-Id", UUID.randomUUID().toString()); // Idempotence

        Map<String, Object> body = Map.of(
            "intent", "CAPTURE",
            "purchase_units", List.of(
                Map.of(
                    "amount", Map.of("currency_code", "USD", "value", amountStr),
                    "reference_id", String.valueOf(order.getId())
                )
            ),
            "application_context", Map.of(
                "return_url", "http://localhost:4200/success",
                "cancel_url", "http://localhost:4200/cancel"
            )
        );

        HttpEntity<Map<String, Object>> httpRequest = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    payPalConfig.getBaseUrl() + "/v2/checkout/orders", httpRequest, Map.class);

            Map<String, Object> responseBody = response.getBody();
            String paypalOrderId = (String) responseBody.get("id");
            String status = (String) responseBody.get("status");

            String approveUrl = null;
            List<Map<String, String>> links = (List<Map<String, String>>) responseBody.get("links");
            if (links != null) {
                for (Map<String, String> link : links) {
                    if ("approve".equals(link.get("rel"))) {
                        approveUrl = link.get("href");
                        break;
                    }
                }
            }

            order.setPaypalOrderId(paypalOrderId);
            orderRepository.save(order);

            PayPalOrderResponse payPalResponse = new PayPalOrderResponse();
            payPalResponse.setId(paypalOrderId);
            payPalResponse.setStatus(status);
            payPalResponse.setApproveUrl(approveUrl);
            return payPalResponse;

        } catch (HttpClientErrorException e) {
            throw new PaymentGatewayException("Erreur PayPal lors de la création: " + e.getResponseBodyAsString(), e);
        }
    }

    public String capturePayment(String paypalOrderId) {
        Order order = orderRepository.findByPaypalOrderId(paypalOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with PayPal ID: " + paypalOrderId));

        // Idempotence: si déjà payé, on renvoie COMPLETED immédiatement
        if (order.getStatus() == Order.OrderStatus.PAID) {
            return "COMPLETED";
        }

        String accessToken = getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("PayPal-Request-Id", UUID.randomUUID().toString());

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    payPalConfig.getBaseUrl() + "/v2/checkout/orders/" + paypalOrderId + "/capture",
                    request, Map.class);

            Map<String, Object> body = response.getBody();
            String status = (String) body.get("status");

            if ("COMPLETED".equals(status)) {
                // Vérification stricte du montant et de la devise capturés
                Map<String, Object> purchaseUnits = ((List<Map<String, Object>>) body.get("purchase_units")).get(0);
                Map<String, Object> payments = (Map<String, Object>) purchaseUnits.get("payments");
                List<Map<String, Object>> captures = (List<Map<String, Object>>) payments.get("captures");
                Map<String, Object> capture = captures.get(0);
                
                Map<String, String> amountMap = (Map<String, String>) capture.get("amount");
                BigDecimal capturedAmount = new BigDecimal(amountMap.get("value"));
                String capturedCurrency = amountMap.get("currency_code");
                
                if (capturedCurrency.equals("USD") && order.getTotalAmount().compareTo(capturedAmount) == 0) {
                    String captureId = (String) capture.get("id");
                    order.setStatus(Order.OrderStatus.PAID);
                    order.setPaypalCaptureId(captureId);
                    orderRepository.save(order);
                } else {
                    throw new BadRequestException("Amount mismatch: DB=" + order.getTotalAmount() + " USD, PayPal=" + capturedAmount + " " + capturedCurrency);
                }
            }
            return status;

        } catch (HttpClientErrorException e) {
            throw new PaymentGatewayException("Erreur PayPal lors de la capture: " + e.getResponseBodyAsString(), e);
        }
    }
}