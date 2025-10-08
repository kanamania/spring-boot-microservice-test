package tz.co.flex.flexsms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import tz.co.flex.flexsms.model.Customer;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Sends an email to the specified recipient with the given subject and message
     * @param to Recipient's email address
     * @param subject Email subject
     * @param message Email message content
     */
    public void sendEmail(String to, String subject, String message) {
        if (to == null || to.trim().isEmpty()) {
            throw new IllegalArgumentException("Recipient email cannot be null or empty");
        }
        
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        
        mailSender.send(mailMessage);
    }
    
    /**
     * Sends a welcome email to a customer
     * @param customer The customer to send the email to
     */
    public void sendEmail(Customer customer) {
        String subject = "Welcome to FlexSMS!";
        String message = String.format("""
            Dear %s,
            
            Thank you for your interest in our services. We have received your information and will contact you shortly.
            
            Your contact number: %s
            
            Best regards,
            The FlexSMS Team
            """, customer.getName(), customer.getPhoneNumber());
            
        sendEmail(customer.getEmail(), subject, message);
    }
}
