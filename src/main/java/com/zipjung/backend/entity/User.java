package com.zipjung.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Entity
@Table(name = "user")
@Getter
public class User extends BaseEntity {
    @Column
    private String username;

    @Column(length = 24, nullable = false)
    @Size(min = 12, max = 24)
    private String password;

    @Column(name = "role_id")
    private Long roleId;

    @Column
    private Boolean isWithdrawn;
}
