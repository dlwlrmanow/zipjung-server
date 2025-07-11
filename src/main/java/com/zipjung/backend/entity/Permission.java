package com.zipjung.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "permission")
public class Permission extends BaseEntity {
    @Column(name = "service_id")
    private Long serviceId;

    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

}
