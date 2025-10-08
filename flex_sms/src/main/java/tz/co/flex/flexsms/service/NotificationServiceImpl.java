package tz.co.flex.flexsms.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tz.co.flex.flexsms.config.NotificationConfig;
import tz.co.flex.flexsms.model.Customer;
import tz.co.flex.flexsms.model.PaymentStatus;

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationConfig notificationConfig;
    private final SmsService smsService;
    private final EmailService emailService;

    @Autowired
    public NotificationServiceImpl(NotificationConfig notificationConfig,
                                 SmsService smsService,
                                 EmailService emailService) {
        this.notificationConfig = notificationConfig;
        this.smsService = smsService;
        this.emailService = emailService;
    }

    @Override
    public void sendNotification(Customer customer, String subject, String message) throws Exception {
        switch (notificationConfig.getPreferredMethod()) {
            case EMAIL:
                sendEmail(customer, subject, message);
                break;
            case SMS:
                sendSms(customer, message);
                break;
            case BOTH:
                sendEmail(customer, subject, message);
                sendSms(customer, message);
                break;
            default:
                log.warn("No valid notification method configured for customer: {}", customer.getEmail());
        }
    }

    @Override
    public void sendPaymentStatusUpdateEmail(Customer customer, String transactionId, PaymentStatus status) throws Exception {
        if (customer.getEmail() == null || customer.getEmail().trim().isEmpty()) {
            log.warn("No email address provided for payment status update");
            return;
        }

        try {
            String emailContent = buildPaymentStatusEmail(customer, transactionId, status.toString());
            emailService.sendEmail(customer.getEmail(), "Payment Status Update", emailContent);
            log.info("Email sent to {} <{}>", customer.getEmail(), emailContent);
        } catch (Exception e) {
            throw e;
        }
    }

    public void sendEmail(Customer customer, String subject, String message) {
        if (customer.getEmail() == null || customer.getEmail().trim().isEmpty()) {
            log.warn("No email address provided for customer: {}", customer.getName());
            return;
        }

        try {
            String emailBody = buildMessage(customer, message);
                
            emailService.sendEmail(customer.getEmail(), subject, emailBody);
            log.info("Email sent to {} <{}>", customer.getName(), customer.getEmail());
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", customer.getEmail(), e.getMessage(), e);
            throw new RuntimeException("Failed to send welcome email", e);
        }
    }

    public void sendSms(Customer customer, String message) {
        // SMS sending implementation
        log.info("Sending SMS to {}: {}", customer.getPhoneNumber(), message);
        // smsService.send(customer.getPhone(), customer.getNotificationMessage());
    }

    private String buildPaymentStatusEmail(Customer customer, String transactionId, String status) {
        return String.format("%s %s,\n\n%s\n\n%s",
                "Dear ",
                customer.getName(),
                String.format("Your payment transaction (ID: %s) status has been updated to: %s\n\n",
                        transactionId, status),
                "Thank you for your business.\n" +
                "Best regards,\n" +
                "Flex SMS Team");
    }
    private String buildMessage(Customer customer, String message) {
        return String.format("%s %s,\n\n%s\n\n%s",
                "Dear ",
                customer.getName(),
                message.replace("{name}", customer.getName()),
                "Thank you for your business.\n" +
                "Best regards,\n" +
                "Flex SMS Team");
    }
}
