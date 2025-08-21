package com.zipjung.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "profile")
public class Profile extends BaseEntity{
    @Column
    private Long userId;

    @Column(length = 10)
    @Size(max = 10)
    private String nickname;

    @Column
    private String pic; // 프로필 사진

    @Column
    private String bio;

    @Column
    private String email;

    @Column
    private Boolean isWithdrawn;
}
