package tz.co.flex.flexsms.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Represents a payment status update received from an external payment service
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentStatusUpdate {
    
    @JsonProperty("paymentId")
    private String paymentId;          // Payment ID from the payment processor
    
    @JsonProperty("customerId")
    private String customerId;         // Customer ID from the payment processor
    
    @JsonProperty("orderId")
    private String orderId;            // Order ID associated with the payment
    
    @JsonProperty("reference")
    private String reference;          // Payment reference number
    
    @JsonProperty("amount")
    private BigDecimal amount;         // Payment amount
    
    @JsonProperty("currency")
    private String currency;           // Currency code (e.g., USD, TZS)
    
    @JsonProperty("status")
    private String status;             // COMPLETED, FAILED, PENDING, etc.
    
    @JsonProperty("paymentMethod")
    private String paymentMethod;      // CARD, MOBILE_MONEY, etc.
    
    @JsonProperty("description")
    private String description;        // Description of the payment
    
    @JsonProperty("timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    private LocalDateTime timestamp;   // When the status was updated
    
    @JsonProperty("metadata")
    private Map<String, Object> metadata; // Additional metadata
    
    /**
     * Get the processed at timestamp from metadata
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    public LocalDateTime getProcessedAt() {
        if (metadata != null && metadata.containsKey("processedAt")) {
            return LocalDateTime.parse(metadata.get("processedAt").toString());
        }
        return null;
    }
    
    /**
     * Get the system that processed the payment from metadata
     */
    public String getProcessingSystem() {
        if (metadata != null) {
            return (String) metadata.getOrDefault("system", null);
        }
        return null;
    }
    
    /**
     * Check if the payment was successful
     */
    public boolean isSuccessful() {
        return "COMPLETED".equalsIgnoreCase(status) || "SUCCESS".equalsIgnoreCase(status);
    }
    
    /**
     * Check if the payment failed
     */
    public boolean isFailed() {
        return "FAILED".equalsIgnoreCase(status) || "DECLINED".equalsIgnoreCase(status);
    }
    
    /**
     * Check if the payment is still pending
     */
    public boolean isPending() {
        return "PENDING".equalsIgnoreCase(status) || "PROCESSING".equalsIgnoreCase(status);
    }
}
