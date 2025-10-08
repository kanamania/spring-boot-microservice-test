package tz.co.flex.flexsms.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import tz.co.flex.flexsms.model.Customer;

import java.util.List;

@Slf4j
@Service
public class MessageConsumer {

    private final NotificationService notificationService;

    @Autowired
    public MessageConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = "${rabbitmq.messages.queue}")
    public void receiveMessage(@Payload List<Customer> customers) {
        log.info("Processing {} customer notifications", customers.size());
        
        for (Customer customer : customers) {
            try {
                notificationService.sendNotification(customer, "FlexSMS Notification", "This is a test notification");
                log.debug("Successfully processed notification for customer: {}", customer.getEmail());
            } catch (Exception e) {
                log.error("Failed to process notification for customer {}: {}", 
                        customer.getEmail(), e.getMessage(), e);
            }
        }
    }
}
