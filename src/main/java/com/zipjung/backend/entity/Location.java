package com.zipjung.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "location")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Location extends BaseEntity {
    @Column(name = "post_id")
    private Long postId;

    // 위도
    // Long 사용시 00.0000 소수점 뒤에 잘림
    @Column(name = "latitude")
    private Double latitude;
    // 경도
    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "place_url")
    private String placeUrl;

    @Column(name = "place_id")
    private Long placeId;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    public void markAsDelete() {
        this.isDeleted = true;
    }
}
