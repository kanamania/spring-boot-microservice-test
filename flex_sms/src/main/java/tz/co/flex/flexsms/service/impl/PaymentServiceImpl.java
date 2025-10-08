package tz.co.flex.flexsms.service.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import tz.co.flex.flexsms.client.PaymentServiceClient;
import tz.co.flex.flexsms.model.PaymentRequest;
import tz.co.flex.flexsms.model.PaymentResponse;
import tz.co.flex.flexsms.model.Payment;
import tz.co.flex.flexsms.model.Customer;
import tz.co.flex.flexsms.repository.PaymentRepository;
import tz.co.flex.flexsms.repository.CustomerRepository;
import tz.co.flex.flexsms.service.PaymentService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentServiceClient paymentServiceClient;
    private final RabbitTemplate rabbitTemplate;
    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    
    @Value("${rabbitmq.payment.queue}")
    private String paymentQueueName;

    @Getter
    @Value("${payment.service.api-key}")
    private String testApiKey;

    private Payment convertToPayment(PaymentRequest paymentRequest) {
        try {
            // Find customer by customerId
            Customer customer = customerRepository.findByCustomerId(paymentRequest.getCustomerId())
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + paymentRequest.getCustomerId()));

            Payment payment = new Payment();
            payment.setTransactionId(paymentRequest.getPaymentId());
            payment.setCustomer(customer);
            payment.setAmount(paymentRequest.getAmount());
            payment.setCurrency(paymentRequest.getCurrency());
            payment.setPaymentMethod(paymentRequest.getPaymentMethod());
            payment.setDescription("Payment for order: " + paymentRequest.getOrderId());

            return payment;
        } catch (Exception e) {
            log.error("Failed to convert PaymentRequest to Payment entity: {}", e.getMessage());
            throw new RuntimeException("Failed to process payment request: " + e.getMessage(), e);
        }
    }

    private void validatePaymentRequest(PaymentRequest paymentRequest) {
        if (paymentRequest == null) {
            throw new IllegalArgumentException("Payment request cannot be null");
        }
        if (paymentRequest.getCustomerId() == null || paymentRequest.getCustomerId().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID is required");
        }
        if (paymentRequest.getAmount() == null || paymentRequest.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        if (paymentRequest.getCurrency() == null || paymentRequest.getCurrency().trim().isEmpty()) {
            throw new IllegalArgumentException("Currency is required");
        }
    }

    @Override
    @Retryable(
        retryFor = Exception.class,
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public PaymentResponse processPayment(PaymentRequest paymentRequest) {
        log.info("Initiating payment for customer: {}", paymentRequest.getCustomerId());
        validatePaymentRequest(paymentRequest);

        try {
            log.info("Converting payment request to payment entity and saving to database");

            // Convert PaymentRequest to Payment entity and save to database
            Payment payment = convertToPayment(paymentRequest);
            payment = paymentRepository.save(payment);

            log.info("Payment saved to database with ID: {}", payment.getId());

            log.info("Sending payment request to queue: {}", paymentRequest.getPaymentId());

            // Set a unique payment ID if not set
            if (paymentRequest.getPaymentId() == null) {
                paymentRequest.setPaymentId("PAY-" + UUID.randomUUID().toString());
            }

            // Send to payment processing queue
            rabbitTemplate.convertAndSend(paymentQueueName, paymentRequest);

            // Return a pending response
            return PaymentResponse.builder()
                .paymentId(paymentRequest.getPaymentId())
                .status("PENDING")
                .message("Payment request received and is being processed")
                .amount(paymentRequest.getAmount())
                .currency(paymentRequest.getCurrency())
                .processedAt(LocalDateTime.now())
                .build();

        } catch (IllegalArgumentException e) {
            log.error("Invalid payment request: {}", e.getMessage());
            return PaymentResponse.builder()
                .paymentId(paymentRequest.getPaymentId())
                .status("FAILED")
                .message("Invalid payment request: " + e.getMessage())
                .amount(paymentRequest.getAmount())
                .currency(paymentRequest.getCurrency())
                .processedAt(LocalDateTime.now())
                .build();
        } catch (Exception e) {
            log.error("Error processing payment: {}", e.getMessage(), e);
            return PaymentResponse.builder()
                .paymentId(paymentRequest.getPaymentId())
                .status("FAILED")
                .message("Failed to process payment: " + e.getMessage())
                .amount(paymentRequest.getAmount())
                .currency(paymentRequest.getCurrency())
                .processedAt(LocalDateTime.now())
                .build();
        }
    }
}
