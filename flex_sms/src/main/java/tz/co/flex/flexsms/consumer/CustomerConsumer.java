package tz.co.flex.flexsms.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import tz.co.flex.flexsms.model.Customer;
import tz.co.flex.flexsms.repository.CustomerRepository;
import tz.co.flex.flexsms.service.NotificationService;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerConsumer {

    private final NotificationService notificationService;
    private final CustomerRepository customerRepository;

    @RabbitListener(queues = "${rabbitmq.customer.queue}")
    public void receiveCustomers(@Payload List<Customer> customers) {
        try {
            log.info("Received {} customers from queue", customers.size());
            
            for (Customer customer : customers) {
                try {
                    // Save customer to database
                    Customer savedCustomer = customerRepository.save(customer);
                    log.info("Saved customer to database: {} <{}>", 
                            savedCustomer.getName(), savedCustomer.getEmail());
                    
                    // Send welcome notification
                    sendWelcomeNotification(savedCustomer);
                } catch (Exception e) {
                    log.error("Failed to process customer {}: {}", 
                            customer.getEmail(), e.getMessage(), e);
                    // Continue processing other customers even if one fails
                }
            }
            
            log.info("Finished processing {} customers", customers.size());
            
        } catch (Exception e) {
            log.error("Error processing customers from queue: {}", e.getMessage(), e);
        }
    }
    
    private void sendWelcomeNotification(Customer customer) throws Exception {
        String subject = "Welcome to Flex SMS!";
        String message = String.format(
            "We're excited to have you join Flex SMS!\n\n" +
            "Your account has been successfully created and you can now start using our services.\n\n" +
            "If you have any questions, please don't hesitate to contact our support team."
        );
        
        notificationService.sendNotification(customer, subject, message);
        log.info("Welcome notification sent to customer: {} <{}>", 
                customer.getName(), customer.getEmail());
    }
}
