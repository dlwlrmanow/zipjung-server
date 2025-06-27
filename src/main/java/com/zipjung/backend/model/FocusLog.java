package com.zipjung.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "focus_log")
public class FocusLog extends BaseEntity {
    @Column(name = "location_id")
    private Long locationId;

    @Column(name = "post_id")
    private Long postId;

    @Column(name = "focus_time_id")
    private Long focusTimeId;
}
