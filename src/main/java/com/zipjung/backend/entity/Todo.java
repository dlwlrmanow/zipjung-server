package com.zipjung.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "todo")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Todo extends BaseEntity{
    @Column(name = "task")
    private String task;

    @Column(name = "post_id")
    private Long postId;

    @Column(name = "is_done")
    private boolean isDone;

    @Column(name = "member_id")
    private Long memberId;

    public void markAsDone() {
        this.isDone = true;
    }
}
