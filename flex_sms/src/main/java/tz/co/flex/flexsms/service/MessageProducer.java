package tz.co.flex.flexsms.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tz.co.flex.flexsms.model.Customer;

import java.util.List;

@Service
public class MessageProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.customer.queue}")
    private String customerQueueName;

    public void sendCustomers(List<Customer> customers) {
        // Send customers in bulk directly to queue
        rabbitTemplate.convertAndSend(customerQueueName, customers);
    }
}
