package com.zipjung.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "focus_log")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FocusLog extends BaseEntity {
    @Column(name = "post_id")
    private Long postId;

    @Column(name = "rating")
    private int rating;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;
}
