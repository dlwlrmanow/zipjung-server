package com.zipjung.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user")
@Getter
@Setter
public class User extends BaseEntity {
    @Column
    private String username;

    @Column(length = 24, nullable = false)
    @Size(min = 12, max = 24)
    private String password;

    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "is_withdrawn")
    private Boolean isWithdrawn = false;
}
