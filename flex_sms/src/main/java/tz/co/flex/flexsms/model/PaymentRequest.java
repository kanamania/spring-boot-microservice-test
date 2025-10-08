package tz.co.flex.flexsms.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payment request details")
public class PaymentRequest {
    
    @Schema(description = "Unique payment identifier (auto-generated if not provided)", 
            example = "PAY-12345-67890")
    private String paymentId;
    
    @Schema(description = "Customer identifier", 
            example = "CUST-001", 
            required = true)
    private String customerId;
    
    @Schema(description = "Associated order identifier", 
            example = "ORD-2025-001")
    private String orderId;
    
    @Schema(description = "Payment amount (must be greater than zero)", 
            example = "50000.00", 
            required = true)
    private BigDecimal amount;
    
    @Schema(description = "Currency code (ISO 4217)", 
            example = "TZS", 
            required = true,
            allowableValues = {"TZS", "USD", "EUR", "KES", "UGX"})
    private String currency;
    
    @Schema(description = "Payment method type", 
            example = "MOBILE_MONEY",
            allowableValues = {"MOBILE_MONEY", "CREDIT_CARD", "BANK_TRANSFER"})
    private String paymentMethod;
    
    @Schema(description = "Service identification token",
            example = "Identification token for service to service")
    private String apiToken;
}
