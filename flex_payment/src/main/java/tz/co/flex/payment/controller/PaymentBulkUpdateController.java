package tz.co.flex.payment.controller;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.Refill;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tz.co.flex.payment.model.dto.BulkUpdateResult;
import tz.co.flex.payment.security.CurrentUser;
import tz.co.flex.payment.security.UserPrincipal;
import tz.co.flex.payment.service.PaymentBulkUpdateService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/payments/bulk-update")
@Tag(name = "Bulk Payment Update", description = "APIs for bulk updating payment statuses via Excel upload")
@RequiredArgsConstructor
public class PaymentBulkUpdateController {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final String[] ALLOWED_CONTENT_TYPES = {
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "application/vnd.ms-excel"
    };

    private final PaymentBulkUpdateService bulkUpdateService;
    private final Map<String, Bucket> rateLimitBuckets = new ConcurrentHashMap<>();

    // Rate limiting: 10 requests per minute per IP
    private Bucket resolveBucket(String ip) {
        return rateLimitBuckets.computeIfAbsent(ip, key -> {
            Bandwidth limit = Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1)));
            return Bucket.builder().addLimit(limit).build();
        });
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Upload Excel file to update payment statuses in bulk",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<?> bulkUpdateStatus(
            @CurrentUser UserPrincipal currentUser,
            @RequestParam("file") @NotNull(message = "File is required") MultipartFile file,
            @RequestParam(value = "comment", required = false) 
            @Size(max = 500, message = "Comment must not exceed 500 characters") String comment,
            HttpServletRequest request) {
        
        // Rate limiting check
        String clientIp = getClientIp(request);
        Bucket bucket = resolveBucket(clientIp);
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        
        if (!probe.isConsumed()) {
            long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .header("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefill))
                    .body(Map.of(
                        "error", "Too many requests",
                        "message", "Rate limit exceeded. Try again in " + waitForRefill + " seconds"
                    ));
        }

        // File validation
        validateFile(file);

        try {
            log.info("Bulk update request received from user: {} (IP: {})", 
                    currentUser.getUsername(), clientIp);
            
            BulkUpdateResult result = bulkUpdateService.processBulkStatusUpdate(
                    file, 
                    currentUser.getUsername(),
                    comment);
            
            log.info("Bulk update completed. Success: {}, Failed: {}", 
                    result.getSuccessfulUpdates(), result.getFailedUpdates());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("Bulk update failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "Failed to process bulk update",
                        "message", e.getMessage()
                    ));
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds the maximum limit of 10MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !isAllowedContentType(contentType)) {
            throw new IllegalArgumentException("Invalid file type. Only Excel files (.xlsx, .xls) are allowed");
        }
    }

    private boolean isAllowedContentType(String contentType) {
        for (String allowedType : ALLOWED_CONTENT_TYPES) {
            if (allowedType.equalsIgnoreCase(contentType)) {
                return true;
            }
        }
        return false;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
