package tz.co.flex.flexsms.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tz.co.flex.flexsms.model.Customer;

@Slf4j
@Service
public class SmsServiceImpl implements SmsService {

    @Override
    public void sendSms(Customer customer) throws Exception {
        // In a real application, this would call an SMS gateway API
        String message = String.format("Hello %s, thank you for your interest! We'll contact you soon at %s.",
                customer.getName(), customer.getPhoneNumber());
        
        // Log the message for demonstration purposes
        log.info("Sending SMS to {} ({}): {}", 
                customer.getName(), customer.getPhoneNumber(), message);
        
        // Simulate some processing time
        Thread.sleep(100);
    }
}
