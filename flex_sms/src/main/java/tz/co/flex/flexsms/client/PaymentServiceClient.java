package tz.co.flex.flexsms.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import tz.co.flex.flexsms.config.PaymentServiceConfig;
import tz.co.flex.flexsms.model.dto.PaymentRequest;
import tz.co.flex.flexsms.model.dto.PaymentResponse;

@Component
public class PaymentServiceClient {

    private final RestTemplate restTemplate;
    private final PaymentServiceConfig config;

    @Autowired
    public PaymentServiceClient(RestTemplate paymentServiceRestTemplate, PaymentServiceConfig config) {
        this.restTemplate = paymentServiceRestTemplate;
        this.config = config;
    }

    public PaymentResponse processPayment(PaymentRequest request) {
        String url = config.getPaymentServiceBaseUrl() + "/api/v1/payments/process";
        return exchange(url, HttpMethod.POST, request, PaymentResponse.class);
    }

    public PaymentResponse checkPaymentStatus(String transactionId) {
        String url = String.format("%s/api/v1/payments/%s/status", config.getPaymentServiceBaseUrl(), transactionId);
        return exchange(url, HttpMethod.GET, null, PaymentResponse.class);
    }

    public PaymentResponse processRefund(String transactionId, double amount, String reason) {
        String url = String.format("%s/api/v1/payments/%s/refund", config.getPaymentServiceBaseUrl(), transactionId);
        RefundRequest refundRequest = new RefundRequest(amount, reason);
        return exchange(url, HttpMethod.POST, refundRequest, PaymentResponse.class);
    }

    private <T> T exchange(String url, HttpMethod method, Object request, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-API-Key", config.getApiKey());

        HttpEntity<?> entity = new HttpEntity<>(request, headers);
        ResponseEntity<T> response = restTemplate.exchange(url, method, entity, responseType);
        
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Payment service returned error: " + response.getStatusCode());
        }
        
        return response.getBody();
    }

    private static class RefundRequest {
        private final double amount;
        private final String reason;

        public RefundRequest(double amount, String reason) {
            this.amount = amount;
            this.reason = reason;
        }

        // Getters for JSON serialization
        public double getAmount() { return amount; }
        public String getReason() { return reason; }
    }
}
