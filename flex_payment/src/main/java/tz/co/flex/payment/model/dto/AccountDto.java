package tz.co.flex.payment.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AccountDto {
    private String id;
    
    @NotBlank(message = "Account name is required")
    private String name;
    
    private String webhookUrl;
    private String allowedIps; // Comma-separated list of allowed IPs
    private boolean active = true;
    
    // For responses only
    private String apiToken;
}
