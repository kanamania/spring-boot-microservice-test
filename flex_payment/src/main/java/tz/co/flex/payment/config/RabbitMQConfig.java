package tz.co.flex.payment.config;

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

    @Value("${rabbitmq.queue.name}")
    private String paymentsQueue;

    @Bean
    public Queue paymentsQueue() {
        return new Queue(paymentsQueue, true);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange("flex.direct");
    }

    @Bean
    public Binding binding(Queue paymentsQueue, DirectExchange exchange) {
        return BindingBuilder.bind(paymentsQueue)
                .to(exchange)
                .with(String.valueOf(paymentsQueue));
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
