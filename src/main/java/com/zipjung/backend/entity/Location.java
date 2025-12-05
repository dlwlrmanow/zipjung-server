package com.zipjung.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "location")
public class Location extends BaseEntity {
    private Long id;
    private Long postId;

    // 위도
    // Long 사용시 00.0000 소수점 뒤에 잘림
    private Double latitude;
    // 경도
    private Double longitude;

    private Boolean isDeleted;
}
