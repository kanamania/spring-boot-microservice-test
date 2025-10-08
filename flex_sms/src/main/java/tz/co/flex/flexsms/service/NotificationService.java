package tz.co.flex.flexsms.service;

import tz.co.flex.flexsms.model.Customer;
import tz.co.flex.flexsms.model.PaymentStatus;

public interface NotificationService {
    void sendNotification(Customer customer, String subject, String message) throws Exception;
    
    /**
     * Sends a payment status update email to the customer
     * @param customer Customer's email address
     * @param transactionId The transaction ID
     * @param status Payment status
     */
    void sendPaymentStatusUpdateEmail(Customer customer, String transactionId, PaymentStatus status) throws Exception;
}
