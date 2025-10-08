package tz.co.flex.flexsms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.co.flex.flexsms.model.Customer;
import tz.co.flex.flexsms.model.PaymentRequest;
import tz.co.flex.flexsms.model.PaymentResponse;
import tz.co.flex.flexsms.repository.CustomerRepository;
import tz.co.flex.flexsms.service.PaymentService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payment", description = "Payment processing API")
public class PaymentController {

    private final PaymentService paymentService;
    private final CustomerRepository customerRepository;

    public PaymentController(PaymentService paymentService, CustomerRepository customerRepository) {
        this.paymentService = paymentService;
        this.customerRepository = customerRepository;
    }
    private void ensureCustomerExists(String customerId) {
        if (customerRepository.findByCustomerId(customerId).isEmpty()) {
            Customer customer = new Customer();
            customer.setCustomerId(customerId);
            customer.setName("Customer " + customerId);
            customer.setEmail(customerId.replace("CUST-", "customer") + "@example.com");
            customer.setPhoneNumber("+255" + (100000000 + new Random().nextInt(900000000)));
            customerRepository.save(customer);
            log.info("Created new customer: {}", customerId);
        }
    }

    @Operation(
        summary = "Process a payment",
        description = "Process a single payment request and send it to the payment queue for processing"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Payment request accepted and queued for processing",
            content = @Content(schema = @Schema(implementation = PaymentResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid payment request data"
        )
    })
    @PostMapping("/process")
    public ResponseEntity<PaymentResponse> processPayment(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Payment request details",
                required = true,
                content = @Content(schema = @Schema(implementation = PaymentRequest.class))
            )
            @RequestBody PaymentRequest paymentRequest) {
        PaymentResponse response = paymentService.processPayment(paymentRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Mock process 100 payment requests",
        description = "Generate and process 100 dummy payment requests for testing purposes. " +
                     "This endpoint automatically generates random payment data and dispatches " +
                     "all requests to the payment queue. No input required."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully generated and processed 100 payment requests",
            content = @Content(schema = @Schema(implementation = MockPaymentResponse.class))
        )
    })
    @PostMapping("/mock/process-batch")
    public ResponseEntity<MockPaymentResponse> mockProcessPayments() {
        log.info("Starting mock payment batch processing - generating 100 payment requests");
        
        List<PaymentResponse> responses = new ArrayList<>();
        Random random = new Random();
        String[] currencies = {"TZS", "USD", "EUR", "KES", "UGX"};
        String[] paymentMethods = {"MOBILE_MONEY", "CREDIT_CARD", "BANK_TRANSFER"};
        String[] customerIds = new String[50];
        
        // Generate customer IDs
        for (int i = 0; i < 50; i++) {
            customerIds[i] = "CUST-" + String.format("%05d", i + 1);
        }
        // Generate and process 100 payment requests
        for (int i = 0; i < 100; i++) {
            PaymentRequest request = PaymentRequest.builder()
                .paymentId("PAY-MOCK-" + UUID.randomUUID().toString().substring(0, 8))
                .customerId(customerIds[random.nextInt(customerIds.length)])
                .orderId("ORD-2025-" + String.format("%05d", i + 1))
                .amount(BigDecimal.valueOf(1000 + random.nextInt(99000))) // 1,000 to 100,000
                .currency(currencies[random.nextInt(currencies.length)])
                .paymentMethod(paymentMethods[random.nextInt(paymentMethods.length)])
                .apiToken(paymentService.getTestApiKey())
                .build();

            try {
                // Ensure customer exists before processing payment
                ensureCustomerExists(request.getCustomerId());

                PaymentResponse response = paymentService.processPayment(request);
                responses.add(response);
                log.debug("Processed mock payment {}/100: {}", i + 1, request.getPaymentId());
            } catch (Exception e) {
                log.error("Failed to process mock payment {}/100: {} - {}", i + 1, request.getPaymentId(), e.getMessage());
                // Create a failed response for tracking purposes
                PaymentResponse failedResponse = PaymentResponse.builder()
                    .paymentId(request.getPaymentId())
                    .status("FAILED")
                    .message("Failed to process payment: " + e.getMessage())
                    .amount(request.getAmount())
                    .currency(request.getCurrency())
                    .processedAt(LocalDateTime.now())
                    .build();
                responses.add(failedResponse);
            }
        }
        
        log.info("Completed mock payment batch processing - {} requests processed successfully", responses.size());
        
        MockPaymentResponse mockResponse = new MockPaymentResponse(
            100,
            responses.size(),
            100 - responses.size(),
            responses
        );
        
        return ResponseEntity.ok(mockResponse);
    }
    
    /**
     * Response wrapper for mock payment batch processing
     */
    @Schema(description = "Mock payment batch processing response")
    public record MockPaymentResponse(
        @Schema(description = "Total number of payment requests generated", example = "100")
        int totalRequests,
        
        @Schema(description = "Number of successfully processed requests", example = "100")
        int successfulRequests,
        
        @Schema(description = "Number of failed requests", example = "0")
        int failedRequests,
        
        @Schema(description = "List of all payment responses")
        List<PaymentResponse> responses
    ) {}
}
