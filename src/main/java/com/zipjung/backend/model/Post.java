package com.zipjung.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "post")
public class Post extends BaseEntity {
    @Column(name = "user_id")
    private Long userId;

    @Column
    private String title;

    @Column
    private String content;

    @Column(name = "service_id")
    private Long serviceId;
}
