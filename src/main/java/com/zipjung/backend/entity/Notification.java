package com.zipjung.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "notification")
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
}
