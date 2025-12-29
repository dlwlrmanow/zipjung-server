package com.zipjung.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "focus_time")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FocusTime extends BaseEntity {
    @Column(name = "focused_time")
    private Long focusedTime;

    @Column(name = "start_focus_time")
    private String startFocusTime;

    @Column(name = "end_focus_time")
    private String endFocusTime;

    @Column(name = "focus_log_id")
    private Long focusLogId;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    // TODO: 나중에 제거
    @Column(name = "member_id")
    private Long memberId;

    public void markAsDeleted() {
        this.isDeleted = true;
    }

    public void updateFocusLogId(Long focusLogId) {
        this.focusLogId = focusLogId;
    }
}
