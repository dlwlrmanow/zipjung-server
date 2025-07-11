package com.zipjung.backend.model;

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
    @Column(name = "user_id")
    private Long userId;
}
