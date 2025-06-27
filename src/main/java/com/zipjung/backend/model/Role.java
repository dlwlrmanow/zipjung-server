package com.zipjung.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "role")
public class Role extends BaseEntity {
    @Column
    private String name;

    @Column(name = "service_id")
    private Long serviceId;
}
