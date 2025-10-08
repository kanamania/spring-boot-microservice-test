package tz.co.flex.flexsms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tz.co.flex.flexsms.model.Customer;
import tz.co.flex.flexsms.service.ExcelService;
import tz.co.flex.flexsms.service.MessageProducer;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customer Management", description = "APIs for managing customer data and bulk uploads")
public class CustomerController {

    private final ExcelService excelService;
    private final MessageProducer messageProducer;

    @Autowired
    public CustomerController(ExcelService excelService, MessageProducer messageProducer) {
        this.excelService = excelService;
        this.messageProducer = messageProducer;
    }

    @Operation(
        summary = "Upload customer data from Excel file",
        description = "Upload an Excel file containing customer information. The file will be processed and customers will be sent to the messaging queue.",
        responses = {
            @ApiResponse(responseCode = "200", description = "File processed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file or processing error")
        }
    )
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(
            @Parameter(
                description = "Excel file containing customer data",
                required = true,
                content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            )
            @RequestParam("file") MultipartFile file) {
        try {
            // Read customers from Excel
            List<Customer> customers = excelService.readCustomersFromExcel(file);
            
            // Send customers to RabbitMQ
            messageProducer.sendCustomers(customers);
            
            return ResponseEntity.ok("Successfully processed and sent " + customers.size() + " customers for messaging");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error processing file: " + e.getMessage());
        }
    }
}
