package tz.co.flex.payment.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import tz.co.flex.payment.exception.UnauthorizedException;
import tz.co.flex.payment.model.Account;
import tz.co.flex.payment.model.PaymentMessage;
import tz.co.flex.payment.model.PaymentStatus;
import tz.co.flex.payment.service.ApiTokenService;
import tz.co.flex.payment.service.PaymentService;
import tz.co.flex.payment.service.WebhookService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class PaymentMessageListener {
    private static final Logger log = LoggerFactory.getLogger(PaymentMessageListener.class);
    
    private final WebhookService webhookService;
    private final ApiTokenService apiTokenService;
    private final PaymentService paymentService;

    @Autowired
    public PaymentMessageListener(WebhookService webhookService, 
                                ApiTokenService apiTokenService,
                                PaymentService paymentService) {
        this.webhookService = webhookService;
        this.apiTokenService = apiTokenService;
        this.paymentService = paymentService;
    }

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void handlePaymentMessage(@Payload PaymentMessage paymentMessage, @Header(value = "X-Forwarded-For", required = false) String clientIp) {
        String webhookUrl = null;
        try {
            // Validate API token
            Account account = apiTokenService.validateApiToken(paymentMessage.getApiToken());
            
            // If IP restrictions are set, validate the client IP
            if (!apiTokenService.isIpAllowed(account, clientIp)) {
                throw new UnauthorizedException("Access denied from this IP address");
            }
            
            // Set the account ID for tracking
            paymentMessage.setAccountId(account.getId());
            
            // Save the payment request
            paymentMessage = paymentService.savePayment(paymentMessage);
            log.info("Saved payment request with ID: {}", paymentMessage.getPaymentId());
            
            // Get the webhook URL from the account
            webhookUrl = account.getWebhookUrl();
            if (webhookUrl == null || webhookUrl.trim().isEmpty()) {
                log.warn("No webhook URL configured for account: {}", account.getId());
                throw new IllegalStateException("No webhook URL configured for this account");
            }
            
            log.info("Processing payment message: {}", paymentMessage.getPaymentId());
            
            // Update status to PENDING if not set
            if (paymentMessage.getStatus() == null) {
                paymentMessage.setStatus(PaymentStatus.PENDING);
            }
            
            // Process the payment (simulated)
            PaymentStatus newStatus = processPayment(paymentMessage);
            paymentMessage.setStatus(newStatus);
            
            // Prepare and send success webhook
            Map<String, Object> webhookPayload = createWebhookPayload(paymentMessage);
            webhookService.triggerWebhook(webhookUrl, webhookPayload);
            
            log.info("Successfully processed payment {} with status: {}", 
                    paymentMessage.getPaymentId(), newStatus);
            
        } catch (Exception e) {
            log.error("Error processing payment {}: {}", 
                    paymentMessage.getPaymentId(), e.getMessage(), e);
            
            // Update status to FAILED on error
            paymentMessage.setStatus(PaymentStatus.FAILED);
            paymentMessage.setDescription("Payment processing failed: " + e.getMessage());
            
            // Only send failure webhook if we have a valid webhook URL
            if (webhookUrl != null && !webhookUrl.trim().isEmpty()) {
                try {
                    Map<String, Object> errorPayload = createWebhookPayload(paymentMessage);
                    webhookService.triggerWebhook(webhookUrl, errorPayload);
                } catch (Exception ex) {
                    log.error("Failed to send error webhook for payment: {}", paymentMessage.getPaymentId(), ex);
                }
            }
            
            throw new RuntimeException("Failed to process payment message", e);
        }
    }
    
    private PaymentStatus processPayment(PaymentMessage payment) {
        // Generate a reference if not provided
        if (payment.getReference() == null || payment.getReference().isEmpty()) {
            payment.setReference("PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        
        // Validate required fields
        if (payment.getAmount() == null || payment.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            payment.setDescription("Invalid or missing amount");
            return PaymentStatus.FAILED;
        }
        
        if (payment.getPaymentMethod() == null || payment.getPaymentMethod().toString().trim().isEmpty()) {
            payment.setDescription("Payment method is required");
            return PaymentStatus.FAILED;
        }
        
        // Simulate different processing based on payment method
        return switch (payment.getPaymentMethod().toString().toUpperCase()) {
            case "CARD" -> processCardPayment(payment);
            case "MOBILE_MONEY" -> processMobileMoneyPayment(payment);
            case "BANK_TRANSFER" -> processBankTransferPayment(payment);
            default -> {
                payment.setDescription("Unsupported payment method: " + payment.getPaymentMethod());
                yield PaymentStatus.FAILED;
            }
        };
    }
    
    private PaymentStatus processCardPayment(PaymentMessage payment) {
        // Simulate card payment processing
        BigDecimal amount = payment.getAmount();
        
        // Simulate different scenarios based on amount
        if (amount.compareTo(new BigDecimal("10000")) > 0) {
            payment.setDescription("Card payment requires manual review");
            return PaymentStatus.PENDING;
        } else if (amount.compareTo(new BigDecimal("5000")) > 0) {
            // Simulate 3D Secure verification
            payment.setDescription("3D Secure verification required");
            return PaymentStatus.PENDING;
        } else if (Math.random() < 0.1) { // 10% chance of failure for demo
            payment.setDescription("Card declined: Insufficient funds");
            return PaymentStatus.FAILED;
        }
        
        payment.setDescription("Card payment processed successfully");
        return PaymentStatus.COMPLETED;
    }
    
    private PaymentStatus processMobileMoneyPayment(PaymentMessage payment) {
        // Simulate mobile money processing
        BigDecimal amount = payment.getAmount();
        
        // Simulate different scenarios based on amount
        if (amount.compareTo(new BigDecimal("50000")) > 0) {
            payment.setDescription("Mobile money payment exceeds limit");
            return PaymentStatus.FAILED;
        } else if (Math.random() < 0.8) { // 80% success rate for demo
            payment.setDescription("Mobile money payment processed. Awaiting customer confirmation");
            return PaymentStatus.PENDING;
        }
        
        payment.setDescription("Mobile money payment failed. Please try again");
        return PaymentStatus.FAILED;
    }
    
    private PaymentStatus processBankTransferPayment(PaymentMessage payment) {
        // Simulate bank transfer processing
        BigDecimal amount = payment.getAmount();
        
        if (amount.compareTo(new BigDecimal("100000")) > 0) {
            payment.setDescription("Bank transfer requires manual processing");
            return PaymentStatus.PENDING;
        }
        
        payment.setDescription("Bank transfer initiated. Processing may take 1-3 business days");
        return PaymentStatus.PENDING;
    }
    
    private Map<String, Object> createWebhookPayload(PaymentMessage payment) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("paymentId", payment.getPaymentId());
        payload.put("reference", payment.getReference());
        payload.put("amount", payment.getAmount());
        payload.put("currency", payment.getCurrency());
        payload.put("status", payment.getStatus().name());
        payload.put("description", payment.getDescription());
        payload.put("timestamp", LocalDateTime.now().toString());
        
        // Add additional metadata
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("processedAt", LocalDateTime.now().toString());
        metadata.put("system", "Flex Payment Processor");
        payload.put("metadata", metadata);
        
        return payload;
    }
}
