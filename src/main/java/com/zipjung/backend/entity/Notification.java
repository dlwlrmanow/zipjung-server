package com.zipjung.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "notification")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class Notification extends BaseEntity{
    @Column(name = "title")
    private String title;

    @Column(name = "message")
    private String message;

    @Column(name = "from_id")
    private Long fromId;

    @Column(name = "to_id")
    private Long toId;

    @Column(name = "is_read")
    private Boolean isRead;

    @Builder
    public Notification(String title, String message, Long fromId, Long toId, boolean isRead) {
        this.title = title;
        this.message = message;
        this.fromId = fromId;
        this.toId = toId;
        this.isRead = isRead; // 사용자한테 발송되었는지 여부
    }
}
