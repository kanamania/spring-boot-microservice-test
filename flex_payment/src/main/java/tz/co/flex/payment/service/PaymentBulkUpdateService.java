package tz.co.flex.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tz.co.flex.payment.model.PaymentMessage;
import tz.co.flex.payment.model.PaymentStatus;
import tz.co.flex.payment.model.dto.BulkUpdateResult;
import tz.co.flex.payment.repository.PaymentRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentBulkUpdateService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public BulkUpdateResult processBulkStatusUpdate(MultipartFile file, String updatedBy, String comment) throws IOException {
        BulkUpdateResult result = new BulkUpdateResult();
        
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            
            // Skip header row
            if (rows.hasNext()) {
                rows.next();
            }
            
            while (rows.hasNext()) {
                Row currentRow = rows.next();
                try {
                    String paymentId = getStringValue(currentRow.getCell(0));
                    String statusStr = getStringValue(currentRow.getCell(1));
                    
                    if (paymentId == null || paymentId.trim().isEmpty()) {
                        result.addError("Row " + (currentRow.getRowNum() + 1) + ": Payment ID is required");
                        continue;
                    }
                    
                    PaymentStatus status;
                    try {
                        status = PaymentStatus.valueOf(statusStr.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        result.addError("Row " + (currentRow.getRowNum() + 1) + ": Invalid status '" + statusStr + "'");
                        continue;
                    }
                    
                    paymentRepository.findByPaymentId(paymentId).ifPresentOrElse(
                        payment -> {
                            payment.setStatus(status);
                            payment.setUpdatedBy(updatedBy);
                            if (comment != null && !comment.isBlank()) {
                                payment.setLastUpdateComment(comment);
                            }
                            payment = paymentRepository.save(payment);
                            result.incrementSuccessful();
                            log.info("Updated payment {} to status {} by user: {}", 
                                paymentId, status, updatedBy);
                        },
                        () -> result.addError("Row " + (currentRow.getRowNum() + 1) + ": Payment not found with ID " + paymentId)
                    );
                    
                } catch (Exception e) {
                    log.error("Error processing row " + (currentRow.getRowNum() + 1), e);
                    result.addError("Row " + (currentRow.getRowNum() + 1) + ": " + e.getMessage());
                }
            }
            
        } catch (Exception e) {
            log.error("Error processing bulk update file", e);
            throw new RuntimeException("Failed to process Excel file: " + e.getMessage(), e);
        }
        
        return result;
    }
    
    private String getStringValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }
}
