package tz.co.flex.payment.model.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class BulkStatusUpdateRequest {
    private MultipartFile file;
    private String updatedBy;
}
