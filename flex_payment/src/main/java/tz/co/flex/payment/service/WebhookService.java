package tz.co.flex.payment.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
public class WebhookService {
    private static final Logger log = LoggerFactory.getLogger(WebhookService.class);
    
    private final RestTemplate restTemplate;
    private final long timeout;
    private final int maxRetries;
    private final long retryDelay;

    public WebhookService(
            RestTemplate restTemplate,
            @Value("${webhook.timeout:5000}") long timeout,
            @Value("${webhook.max-retries:3}") int maxRetries,
            @Value("${webhook.retry-delay:1000}") long retryDelay) {
        this.restTemplate = restTemplate;
        this.timeout = timeout;
        this.maxRetries = maxRetries;
        this.retryDelay = retryDelay;
    }

    @Retryable(
        value = {HttpClientErrorException.class, HttpServerErrorException.class, ResourceAccessException.class},
        maxAttemptsExpression = "${webhook.max-retries:3}",
        backoff = @Backoff(delayExpression = "${webhook.retry-delay:1000}")
    )
    public void triggerWebhook(String callbackUrl, Object payload) {
        try {
            log.info("Sending webhook to: {}", callbackUrl);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            
            HttpEntity<Object> requestEntity = new HttpEntity<>(payload, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                callbackUrl,
                HttpMethod.POST,
                requestEntity,
                String.class
            );
            
            log.info("Webhook sent successfully to {}. Status: {}", callbackUrl, response.getStatusCode());
            
        } catch (Exception e) {
            log.error("Failed to send webhook to {}: {}", callbackUrl, e.getMessage());
            throw e; // This will trigger the retry mechanism
        }
    }
}
