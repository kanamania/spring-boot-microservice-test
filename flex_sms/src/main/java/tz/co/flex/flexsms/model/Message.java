package tz.co.flex.flexsms.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 500)
    private String content;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private CustomerGroup targetGroup;
    
    @Column(nullable = false)
    private MessageType messageType; // EMAIL, SMS, or BOTH
    
    @Column(nullable = false)
    private MessageStatus status; // DRAFT, SENDING, SENT, FAILED
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime sentAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = MessageStatus.DRAFT;
        }
    }
    
    public enum MessageType {
        EMAIL, SMS, BOTH
    }
    
    public enum MessageStatus {
        DRAFT, SENDING, SENT, FAILED
    }
}
