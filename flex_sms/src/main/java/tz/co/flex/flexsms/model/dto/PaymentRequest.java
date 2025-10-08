package tz.co.flex.flexsms.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tz.co.flex.flexsms.model.Message;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentRequest {
    private String customerId;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String accountNumber;
    private BigDecimal amount;
    private String currency;  // TZS, USD, etc.
    private String paymentMethod;  // MOBILE_MONEY, CREDIT_CARD, BANK_TRANSFER
    private String paymentReference;
    private String description;

    @Data
    public static class MessageRequest {
        @NotBlank(message = "Message content is required")
        private String content;

        private Long groupId; // If null, message goes to all customers

        @NotNull(message = "Message type is required")
        private Message.MessageType messageType;

        // Optional fields that can be replaced in the message content
        private String greeting = "Hello"; // Default greeting
        private String signature = "Best regards,\nThe Team"; // Default signature
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageResponse {
        private Long id;
        private String content;
        private Long senderId;
        private String senderName;
        private Long groupId;
        private String groupName;
        private Message.MessageType messageType;
        private Message.MessageStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime sentAt;
        private int totalRecipients;
        private int successfulDeliveries;
        private int failedDeliveries;
    }
}
