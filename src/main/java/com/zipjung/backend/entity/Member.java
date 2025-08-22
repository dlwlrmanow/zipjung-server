package com.zipjung.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "member")
@Getter
@Setter
public class Member extends BaseEntity {
    @Column
    private String username;

    @Column(length = 24, nullable = false)
    @Size(min = 12, max = 24)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "is_withdrawn")
    private Boolean isWithdrawn = false;
}
