package tz.co.flex.payment.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
public class PaymentMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Setter
    @Column(name = "updated_by")
    private String updatedBy;
    
    @Setter
    @Column(name = "last_update_comment", length = 500)
    private String lastUpdateComment;
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    @Setter
    @Column(name = "payment_id", nullable = false, unique = true)
    @JsonProperty("paymentId")
    private String paymentId;
    
    @Setter
    @Column(name = "customer_id")
    @JsonProperty("customerId")
    private String customerId;
    
    @Setter
    @Column(name = "order_id")
    @JsonProperty("orderId")
    private String orderId;
    
    @Setter
    @Column(nullable = false)
    @JsonProperty("amount")
    private BigDecimal amount;
    
    @Setter
    @Column(length = 3, nullable = false)
    @JsonProperty("currency")
    private String currency;
    
    @Setter
    @Column(name = "payment_method", nullable = false)
    @Enumerated(EnumType.STRING)
    @JsonProperty("paymentMethod")
    private PaymentMethod paymentMethod;
    
    @Column(name = "api_token", nullable = false)
    @JsonProperty(value = "apiToken", required = true)
    private String apiToken;      // API token for authentication
    
    @Setter
    @Column(name = "account_id")
    @JsonIgnore
    private String accountId;     // Populated during token validation

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @JsonProperty("status")
    private PaymentStatus status = PaymentStatus.PENDING;
    
    @Setter
    @Column(unique = true)
    @JsonProperty("reference")
    private String reference;
    
    @Setter
    @JsonProperty("description")
    private String description;
    
    @Setter
    @JsonProperty("timestamp")
    private LocalDateTime timestamp = LocalDateTime.now();

}
