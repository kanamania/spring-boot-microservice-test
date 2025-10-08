package tz.co.flex.flexsms.service;

import tz.co.flex.flexsms.model.PaymentRequest;
import tz.co.flex.flexsms.model.PaymentResponse;

public interface PaymentService {
    /**
     * Process a payment request
     * @param paymentRequest The payment request details, including callback URL for status updates
     * @return PaymentResponse containing the result of the payment processing
     */
    PaymentResponse processPayment(PaymentRequest paymentRequest);

    String getTestApiKey();
}
