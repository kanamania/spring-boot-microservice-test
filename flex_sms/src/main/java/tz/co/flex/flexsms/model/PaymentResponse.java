package tz.co.flex.flexsms.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payment processing response")
public class PaymentResponse {
    
    @Schema(description = "Unique payment identifier", 
            example = "PAY-12345-67890")
    private String paymentId;
    
    @Schema(description = "Payment processing status", 
            example = "PENDING",
            allowableValues = {"SUCCESS", "FAILED", "PENDING"})
    private String status;
    
    @Schema(description = "Status message or error description", 
            example = "Payment request received and is being processed")
    private String message;
    
    @Schema(description = "Transaction identifier from payment provider", 
            example = "TXN-98765-43210")
    private String transactionId;
    
    @Schema(description = "Timestamp when payment was processed", 
            example = "2025-10-06T10:06:17")
    private LocalDateTime processedAt;
    
    @Schema(description = "Payment amount", 
            example = "50000.00")
    private BigDecimal amount;
    
    @Schema(description = "Currency code", 
            example = "TZS")
    private String currency;
}
