package com.zipjung.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "todo")
@Getter
@Setter
public class Todo extends BaseEntity{
    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "task")
    private String task;

    @Column(name = "is_done")
    private boolean isDone;
}
