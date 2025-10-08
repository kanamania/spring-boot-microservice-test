package tz.co.flex.flexsms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class PaymentServiceConfig {
    
    @Value("${payment.service.base-url}")
    private String paymentServiceBaseUrl;
    
    @Value("${payment.service.api-key}")
    private String apiKey;
    
    @Bean
    public RestTemplate paymentServiceRestTemplate() {
        return new RestTemplate();
    }
    
    public String getPaymentServiceBaseUrl() {
        return paymentServiceBaseUrl;
    }
    
    public String getApiKey() {
        return apiKey;
    }
}
