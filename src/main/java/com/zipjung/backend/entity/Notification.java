package com.zipjung.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notification")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
public class Notification extends BaseEntity{
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType notificationType;

    @Column(name = "title")
    private String title;

    @Column(name = "message")
    private String message;

    @Column(name = "from_id")
    private Long fromId;

    @Column(name = "to_id")
    private Long toId;

    @Column(name = "url")
    private String url;

    @Column(name = "is_read")
    private Boolean isRead;

    @Builder
    public Notification(NotificationType notificationType, String title, String message, Long fromId, Long toId, boolean isRead) {
        this.notificationType = notificationType;
        this.title = title;
        this.message = message;
        this.fromId = fromId;
        this.toId = toId;
        this.isRead = isRead; // 사용자한테 발송되었는지 여부
    }
}
