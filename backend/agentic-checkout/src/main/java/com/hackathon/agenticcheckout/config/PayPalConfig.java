package com.hackathon.agenticcheckout.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class PayPalConfig {

    @Value("${paypal.client-id}")
    private String clientId;

    @Value("${paypal.client-secret}")
    private String clientSecret;

    @Value("${paypal.base-url}")
    private String baseUrl;

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        // Connect timeout: 5 secondes
        factory.setConnectTimeout(5000); 
        // Read timeout: 15 secondes
        factory.setReadTimeout(15000);   
        return new RestTemplate(factory);
    }

    public String getClientId() { return clientId; }
    public String getClientSecret() { return clientSecret; }
    public String getBaseUrl() { return baseUrl; }
}