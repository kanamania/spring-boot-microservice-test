package tz.co.flex.flexsms.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import tz.co.flex.flexsms.model.PaymentResponse;

@Slf4j
@Component
public class PaymentConsumer {

    @RabbitListener(queues = "${rabbitmq.payment.response-queue}")
    public void receivePaymentResponse(@Payload PaymentResponse paymentResponse) {
        try {
            log.info("Received payment response: {}", paymentResponse);
            
            // Process the payment response (e.g., update order status, send notifications)
            if ("SUCCESS".equalsIgnoreCase(paymentResponse.getStatus())) {
                handleSuccessfulPayment(paymentResponse);
            } else {
                handleFailedPayment(paymentResponse);
            }
            
        } catch (Exception e) {
            log.error("Error processing payment response: {}", e.getMessage(), e);
        }
    }
    
    private void handleSuccessfulPayment(PaymentResponse response) {
        // Update order status to PAID
        // Send confirmation email/SMS
        log.info("Payment successful for payment ID: {}", response.getPaymentId());
    }
    
    private void handleFailedPayment(PaymentResponse response) {
        // Update order status to PAYMENT_FAILED
        // Send failure notification
        log.warn("Payment failed for payment ID: {} - {}", 
                response.getPaymentId(), response.getMessage());
    }
}
