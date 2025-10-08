package tz.co.flex.flexsms.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.co.flex.flexsms.model.*;
import tz.co.flex.flexsms.model.dto.PaymentRequest;
import tz.co.flex.flexsms.repository.CustomerGroupRepository;
import tz.co.flex.flexsms.repository.MessageRepository;
import tz.co.flex.flexsms.repository.UserRepository;
import tz.co.flex.flexsms.service.MessageService;
import tz.co.flex.flexsms.service.NotificationService;
import tz.co.flex.flexsms.service.NotificationServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final CustomerGroupRepository groupRepository;
    private final NotificationServiceImpl notificationService;
    private final RabbitTemplate rabbitTemplate;

    @Override
    @Transactional
    public PaymentRequest.MessageResponse sendMessage(PaymentRequest.MessageRequest request, Long senderId) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        Message message = new Message();
        message.setContent(request.getContent());
        message.setSender(sender);
        message.setMessageType(request.getMessageType());
        message.setStatus(Message.MessageStatus.SENDING);

        if (request.getGroupId() != null) {
            CustomerGroup group = groupRepository.findById(request.getGroupId())
                    .orElseThrow(() -> new RuntimeException("Group not found"));
            message.setTargetGroup(group);
        }

        message = messageRepository.save(message);
        
        // Process message asynchronously
        processMessageAsync(message, request);
        
        return convertToResponse(message);
    }

    private void processMessageAsync(Message message, PaymentRequest.MessageRequest request) {
        try {
            if (message.getTargetGroup() != null) {
                // Send to specific group
                message.getTargetGroup().getCustomers().forEach(customer -> {
                    String personalizedContent = personalizeMessage(request, customer);
                    sendNotification(customer, personalizedContent, message.getMessageType());
                });
            } else {
                // Send to all customers (implement this method based on your needs)
                // This is a simplified example
                // customerRepository.findAll().forEach(customer -> {
                //     String personalizedContent = personalizeMessage(request, customer);
                //     sendNotification(customer, personalizedContent, message.getMessageType());
                // });
            }
            
            message.setStatus(Message.MessageStatus.SENT);
            message.setSentAt(LocalDateTime.now());
        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage(), e);
            message.setStatus(Message.MessageStatus.FAILED);
        }
        
        messageRepository.save(message);
    }

    private String personalizeMessage(PaymentRequest.MessageRequest request, Customer customer) {
        return String.format("%s %s,\n\n%s\n\n%s",
                request.getGreeting(),
                customer.getName(),
                request.getContent().replace("{name}", customer.getName()),
                request.getSignature());
    }

    private void sendNotification(Customer customer, String content, Message.MessageType messageType) {
        try {
            switch (messageType) {
                case EMAIL:
                    notificationService.sendEmail(customer, "FlexSMS Notification", content);
                    break;
                case SMS:
                    notificationService.sendSms(customer, content);
                    break;
                case BOTH:
                    notificationService.sendEmail(customer, "FlexSMS Notification", content);
                    notificationService.sendSms(customer, content);
                    break;
            }
        } catch (Exception e) {
            log.error("Error sending notification to {}: {}", customer.getEmail(), e.getMessage());
        }
    }

    @Override
    public PaymentRequest.MessageResponse getMessageById(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        return convertToResponse(message);
    }

    @Override
    public List<PaymentRequest.MessageResponse> getMessagesByGroup(Long groupId) {
        return messageRepository.findByTargetGroupId(groupId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentRequest.MessageResponse> getMessagesByStatus(Message.MessageStatus status) {
        return messageRepository.findByStatus(status).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private PaymentRequest.MessageResponse convertToResponse(Message message) {
        PaymentRequest.MessageResponse response = new PaymentRequest.MessageResponse();
        BeanUtils.copyProperties(message, response);
        
        if (message.getSender() != null) {
            response.setSenderId(message.getSender().getId());
            response.setSenderName(message.getSender().getUsername());
        }
        
        if (message.getTargetGroup() != null) {
            response.setGroupId(message.getTargetGroup().getId());
            response.setGroupName(message.getTargetGroup().getName());
        }
        
        return response;
    }
}
