package tz.co.flex.flexsms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.co.flex.flexsms.model.Message;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByTargetGroupId(Long groupId);
    List<Message> findByStatus(Message.MessageStatus status);
}
