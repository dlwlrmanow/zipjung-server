package com.zipjung.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "focus_log")
public class FocusLog extends BaseEntity {
    @Column(name = "post_id")
    private Long postId;

    @Column(name = "rating")
    private int rating;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

}
