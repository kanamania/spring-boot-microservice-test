package tz.co.flex.flexsms.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PaymentResponse {
    private String status;  // SUCCESS, PENDING, FAILED
    private String message;
    private String transactionId;
    private String referenceNumber;
    private LocalDateTime timestamp;
    private String paymentProvider;  // TIGOPESA, MPESA, VISA, etc.
    private String receiptNumber;
    private String errorCode;
    private String errorDescription;
    private String callbackUrl;  // URL to receive payment status updates
    
    public static PaymentResponse success(String transactionId, String referenceNumber, String paymentProvider) {
        PaymentResponse response = new PaymentResponse();
        response.setStatus("SUCCESS");
        response.setMessage("Payment processed successfully");
        response.setTransactionId(transactionId);
        response.setReferenceNumber(referenceNumber);
        response.setPaymentProvider(paymentProvider);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }
    
    public static PaymentResponse error(String errorCode, String errorDescription) {
        PaymentResponse response = new PaymentResponse();
        response.setStatus("FAILED");
        response.setErrorCode(errorCode);
        response.setErrorDescription(errorDescription);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }
}
