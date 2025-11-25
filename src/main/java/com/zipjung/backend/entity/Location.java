package com.zipjung.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "location")
public class Location extends BaseEntity {
    private Long id;
    private Long postId;

    // 위도
    private Long latitude;
    // 경도
    private Long longitude;

    private Boolean isDeleted;

}
