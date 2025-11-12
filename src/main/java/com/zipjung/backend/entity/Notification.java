package com.zipjung.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notification")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
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

    // setter 없애고 읽음으로 처리하는 메서드 생성
    public void markAsRead() {
        this.isRead = true;
    }
}
