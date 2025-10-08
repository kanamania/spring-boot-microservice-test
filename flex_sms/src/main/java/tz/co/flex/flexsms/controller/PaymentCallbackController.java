package tz.co.flex.flexsms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.co.flex.flexsms.model.dto.PaymentStatusUpdate;
import tz.co.flex.flexsms.service.PaymentCallbackService;

/**
 * Controller for receiving payment status updates from external payment services
 */
@Slf4j
@RestController
@RequestMapping("/api/payments/status")
@RequiredArgsConstructor
@Tag(name = "Payment Status Callback", description = "APIs for payment status updates")
public class PaymentCallbackController {

    private final PaymentCallbackService paymentCallbackService;

    /**
     * Endpoint for receiving payment status updates from external services
     * @param statusUpdate The payment status update from the external service
     * @return 200 OK if the update was processed successfully
     */
    @PostMapping
    @Operation(summary = "Receive payment status updates from external services")
    public ResponseEntity<Void> handlePaymentStatusUpdate(
            @RequestHeader(value = "X-Callback-Signature", required = false) String signature,
            @RequestBody PaymentStatusUpdate statusUpdate) {
        
        log.info("Received payment status update: {}", statusUpdate);
        paymentCallbackService.processStatusUpdate(statusUpdate, signature);
        return ResponseEntity.ok().build();
    }
}
