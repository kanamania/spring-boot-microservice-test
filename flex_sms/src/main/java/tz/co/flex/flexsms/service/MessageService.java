package tz.co.flex.flexsms.service;

import tz.co.flex.flexsms.model.Message;
import tz.co.flex.flexsms.model.dto.PaymentRequest;

import java.util.List;

public interface MessageService {
    PaymentRequest.MessageResponse sendMessage(PaymentRequest.MessageRequest request, Long senderId);
    PaymentRequest.MessageResponse getMessageById(Long messageId);
    List<PaymentRequest.MessageResponse> getMessagesByGroup(Long groupId);
    List<PaymentRequest.MessageResponse> getMessagesByStatus(Message.MessageStatus status);
}
