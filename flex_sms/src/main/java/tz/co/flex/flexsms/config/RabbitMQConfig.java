package tz.co.flex.flexsms.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.messages.queue}")
    private String messageQueueName;

    @Value("${rabbitmq.payment.queue}")
    private String paymentQueueName;

    @Value("${rabbitmq.customer.queue}")
    private String customerQueueName;

    @Value("${rabbitmq.payment.response-queue}")
    private String paymentResponseQueueName;

    // Message queue beans (flex.messages)
    @Bean
    public Queue messageQueue() {
        return new Queue(messageQueueName, true);
    }
    
    // Customer queue beans
    @Bean
    public Queue customerQueue() {
        return new Queue(customerQueueName, true);
    }
    
    // Payment queue beans (using configured names)
    @Bean
    public Queue paymentQueue() {
        return new Queue(paymentQueueName, true);
    }
    
    @Bean
    public Queue paymentResponseQueue() {
        return new Queue(paymentResponseQueueName, true);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
