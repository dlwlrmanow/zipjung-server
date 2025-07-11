package com.zipjung.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "api_test")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiTest extends BaseEntity {
    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;
}
