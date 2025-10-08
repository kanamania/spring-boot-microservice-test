package tz.co.flex.flexsms.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a payment transaction in the system
 */
@Data
@Entity
@Table(name = "payments")
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String transactionId;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = false)
    private Customer customer;
    
    @Column(nullable = false)
    private BigDecimal amount;
    
    @Column(length = 3)
    private String currency = "TZS";
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;
    
    private String paymentMethod;
    private String paymentReference;
    private String providerReference;
    private String description;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    private LocalDateTime completedAt;
    private LocalDateTime lastStatusUpdate;
    
    // Additional fields can be added as needed
    private String errorMessage;
    
    /**
     * Update the payment status and set timestamps accordingly
     */
    public void updateStatus(PaymentStatus newStatus, String message) {
        this.status = newStatus;
        this.lastStatusUpdate = LocalDateTime.now();
        
        if (newStatus == PaymentStatus.COMPLETED) {
            this.completedAt = LocalDateTime.now();
        }
        
        if (message != null) {
            this.errorMessage = message;
        }
    }
}
