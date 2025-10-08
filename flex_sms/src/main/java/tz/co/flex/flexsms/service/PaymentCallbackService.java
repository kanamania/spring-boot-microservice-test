package tz.co.flex.flexsms.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.co.flex.flexsms.model.Payment;
import tz.co.flex.flexsms.model.PaymentStatus;
import tz.co.flex.flexsms.model.dto.PaymentStatusUpdate;
import tz.co.flex.flexsms.repository.PaymentRepository;

import java.util.Optional;

/**
 * Service for handling payment status updates from external payment services
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentCallbackService {

    private final PaymentRepository paymentRepository;
    private final NotificationService notificationService;

    @Value("${payment.callback.secret:}")
    private String callbackSecret;

    /**
     * Process a payment status update from an external service
     * @param statusUpdate The status update received
     * @param signature Optional signature for request verification
     */
    @Transactional
    public void processStatusUpdate(PaymentStatusUpdate statusUpdate, String signature) {
        // Verify the request if signature verification is enabled
        if (callbackSecret != null && !callbackSecret.isEmpty()) {
            verifySignature(statusUpdate, signature);
        }

        // Find the payment by transaction ID
        Optional<Payment> paymentOpt = paymentRepository.findByTransactionId(statusUpdate.getPaymentId());
        if (paymentOpt.isEmpty()) {
            log.warn("Received status update for unknown transaction ID: {}", statusUpdate.getPaymentId());
            return;
        }

        Payment payment = paymentOpt.get();
        PaymentStatus newStatus = mapToPaymentStatus(statusUpdate.getStatus());
        
        // Update payment status
        payment.setStatus(newStatus);
        payment.setLastStatusUpdate(statusUpdate.getTimestamp() != null ? 
                statusUpdate.getTimestamp() : java.time.LocalDateTime.now());
        
        // Store provider reference if available
        if (statusUpdate.getReference() != null) {
            payment.setProviderReference(statusUpdate.getReference());
        }
        
        // Save the updated payment
        paymentRepository.save(payment);
        
        log.info("Updated payment status: transactionId={}, newStatus={}", 
                payment.getTransactionId(), newStatus);
                
        // Notify relevant parties about the status update
        notifyStatusUpdate(payment, statusUpdate);
    }
    
    /**
     * Verify the signature of the callback request
     */
    private void verifySignature(PaymentStatusUpdate statusUpdate, String signature) {
        if (signature == null || signature.isBlank()) {
            throw new SecurityException("Missing signature for payment status update");
        }
        
        // In a real implementation, verify the signature using HMAC-SHA256
        // This is a simplified example
        String expectedSignature = generateSignature(statusUpdate);
        if (!expectedSignature.equals(signature)) {
            throw new SecurityException("Invalid signature for payment status update");
        }
    }
    
    /**
     * Generate a signature for the status update (example implementation)
     */
    private String generateSignature(PaymentStatusUpdate statusUpdate) {
        // In a real implementation, use HMAC-SHA256 with the callback secret
        // This is a simplified example
        String data = statusUpdate.getPaymentId() +
                     statusUpdate.getStatus() + 
                     statusUpdate.getTimestamp();
        return "generated-signature"; // Replace with actual signature generation
    }
    
    /**
     * Map external status to internal PaymentStatus enum
     */
    private PaymentStatus mapToPaymentStatus(String externalStatus) {
        if (externalStatus == null) {
            return PaymentStatus.PENDING;
        }
        
        return switch (externalStatus.toUpperCase()) {
            case "SUCCESS", "COMPLETED" -> PaymentStatus.COMPLETED;
            case "FAILED", "DECLINED", "REJECTED" -> PaymentStatus.FAILED;
            case "PENDING", "PROCESSING" -> PaymentStatus.PENDING;
            case "REFUNDED" -> PaymentStatus.REFUNDED;
            default -> {
                log.warn("Unknown payment status received: {}", externalStatus);
                yield PaymentStatus.PENDING;
            }
        };
    }
    
    /**
     * Notify relevant parties about the status update
     */
    private void notifyStatusUpdate(Payment payment, PaymentStatusUpdate statusUpdate) {
        try {
            // Notify the customer
            if (payment.getCustomer().getEmail() != null) {
                // Send email notification
                notificationService.sendPaymentStatusUpdateEmail(
                    payment.getCustomer(),
                    payment.getTransactionId(),
                    payment.getStatus()
                );
            }
            
            // You can add additional notification methods here (e.g., SMS, push notifications)
            
        } catch (Exception e) {
            log.error("Failed to send status update notification: {}", e.getMessage(), e);
        }
    }
}
