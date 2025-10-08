package tz.co.flex.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.co.flex.payment.model.PaymentMessage;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentMessage, String> {
    Optional<PaymentMessage> findByPaymentId(String paymentId);
    boolean existsByPaymentId(String paymentId);
}
