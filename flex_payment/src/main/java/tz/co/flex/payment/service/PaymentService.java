package tz.co.flex.payment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.co.flex.payment.model.PaymentMessage;
import tz.co.flex.payment.repository.PaymentRepository;

import java.util.Optional;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public PaymentMessage savePayment(PaymentMessage payment) {
        // Check if payment with this ID already exists
        if (payment.getPaymentId() != null && paymentRepository.existsByPaymentId(payment.getPaymentId())) {
            throw new IllegalArgumentException("Payment with ID " + payment.getPaymentId() + " already exists");
        }
        
        // If paymentId is not set, generate a new one
        if (payment.getPaymentId() == null || payment.getPaymentId().trim().isEmpty()) {
            payment.setPaymentId("PAY-" + System.currentTimeMillis());
        }
        
        return paymentRepository.save(payment);
    }

    public Optional<PaymentMessage> getPayment(String paymentId) {
        return paymentRepository.findByPaymentId(paymentId);
    }
}
