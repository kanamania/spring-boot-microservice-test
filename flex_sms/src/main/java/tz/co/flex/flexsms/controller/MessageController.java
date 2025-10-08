package tz.co.flex.flexsms.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.co.flex.flexsms.model.Message;
import tz.co.flex.flexsms.model.dto.PaymentRequest;
import tz.co.flex.flexsms.service.MessageService;

import java.util.List;

//@RestController
//@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public ResponseEntity<PaymentRequest.MessageResponse> sendMessage(
            @RequestBody PaymentRequest.MessageRequest request,
            @RequestHeader("X-User-Id") Long senderId) {
        PaymentRequest.MessageResponse response = messageService.sendMessage(request, senderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentRequest.MessageResponse> getMessage(@PathVariable Long id) {
        PaymentRequest.MessageResponse response = messageService.getMessageById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<PaymentRequest.MessageResponse>> getMessagesByGroup(@PathVariable Long groupId) {
        List<PaymentRequest.MessageResponse> messages = messageService.getMessagesByGroup(groupId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PaymentRequest.MessageResponse>> getMessagesByStatus(
            @PathVariable("status") Message.MessageStatus status) {
        List<PaymentRequest.MessageResponse> messages = messageService.getMessagesByStatus(status);
        return ResponseEntity.ok(messages);
    }
}
