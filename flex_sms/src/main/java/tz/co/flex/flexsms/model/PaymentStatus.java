package tz.co.flex.flexsms.model;

/**
 * Represents the status of a payment
 */
public enum PaymentStatus {
    PENDING,      // Payment is being processed
    COMPLETED,    // Payment was successful
    FAILED,       // Payment failed
    REFUNDED,     // Payment was refunded
    CANCELLED     // Payment was cancelled
}
