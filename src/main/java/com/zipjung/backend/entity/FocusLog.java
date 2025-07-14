package com.zipjung.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "focus_log")
public class FocusLog extends BaseEntity {
    @Column(name = "post_id")
    private Long postId;

    @Column(name = "rating")
    private int rating;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    public FocusLog(Long postId, int rating) {
        this.postId = postId;
        this.rating = rating;
    }

    public FocusLog() {}

}
