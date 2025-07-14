package com.zipjung.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@Table(name = "post")
public class Post extends BaseEntity {
    @Column
    private String title;

    @Column
    private String content;

    @Column(name = "service_id")
    private Long serviceId;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    public Post(String title, String content, Long serviceId) {
        this.title = title;
        this.content = content;
        this.serviceId = serviceId;
//        this.isDeleted = false;
    }
    public Post() {}
}
