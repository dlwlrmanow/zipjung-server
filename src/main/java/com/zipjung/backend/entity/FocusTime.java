package com.zipjung.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "focus_time")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FocusTime extends BaseEntity {
    @Column(name = "focused_time")
    private Long focusedTime;

    @Column(name = "start_focus_time")
    private Long startFocusTime;

    @Column(name = "focus_log_id")
    private Long focusLogId;

    @Column(name = "is_deleted")
    private Boolean isDeleted;
}
