package tz.co.flex.flexsms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.co.flex.flexsms.model.Payment;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    /**
     * Find a payment by its transaction ID
     */
    Optional<Payment> findByTransactionId(String transactionId);
    
    /**
     * Check if a payment exists with the given transaction ID
     */
    boolean existsByTransactionId(String transactionId);
    
    /**
     * Find a payment by its provider reference
     */
    Optional<Payment> findByProviderReference(String providerReference);
}
