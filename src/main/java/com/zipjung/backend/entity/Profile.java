package com.zipjung.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Setter;

@Entity
@Table(name = "profile")
@Setter
public class Profile extends BaseEntity{
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(length = 10)
    @Size(max = 10)
    private String nickname;

    @Column
    private String pic; // 프로필 사진

    @Column
    private String bio;

    @Column(nullable = false)
    private String email;

    @Column(name = "is_withdrawn")
    private Boolean isWithdrawn = false;
}
