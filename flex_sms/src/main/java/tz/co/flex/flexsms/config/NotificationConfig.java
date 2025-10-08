package tz.co.flex.flexsms.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "notification")
public class NotificationConfig {
    private String preferredMethod;

    public enum NotificationMethod {
        EMAIL,
        SMS,
        BOTH
    }

    public NotificationMethod getPreferredMethod() {
        try {
            return NotificationMethod.valueOf(preferredMethod.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return NotificationMethod.EMAIL; // Default to EMAIL
        }
    }

    public void setPreferredMethod(String preferredMethod) {
        this.preferredMethod = preferredMethod;
    }
}
