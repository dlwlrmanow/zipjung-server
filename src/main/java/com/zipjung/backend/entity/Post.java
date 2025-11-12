package com.zipjung.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "post")
@Builder
public class Post extends BaseEntity {
    @Column
    private String title;

    @Column
    private String content;

    @Column(name = "service_id")
    private Long serviceId;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "member_id")
    private Long memberId;

    public void markAsDelete() {
        this.isDeleted = true;
    }
}
