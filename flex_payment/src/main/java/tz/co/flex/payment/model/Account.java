package tz.co.flex.payment.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(name = "api_token", nullable = false, unique = true)
    private String apiToken;
    
    @Column(nullable = false)
    private boolean active = true;
    
    @Column(name = "webhook_url")
    private String webhookUrl;
    
    @Column(name = "allowed_ips")
    private String allowedIps; // Comma-separated list of allowed IPs
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getApiToken() { return apiToken; }
    public void setApiToken(String apiToken) { this.apiToken = apiToken; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    public String getWebhookUrl() { return webhookUrl; }
    public void setWebhookUrl(String webhookUrl) { this.webhookUrl = webhookUrl; }
    
    public String getAllowedIps() { return allowedIps; }
    public void setAllowedIps(String allowedIps) { this.allowedIps = allowedIps; }
    
    @PrePersist
    public void generateApiToken() {
        if (this.apiToken == null) {
            this.apiToken = "flx_" + UUID.randomUUID().toString().replace("-", "");
        }
    }
}
