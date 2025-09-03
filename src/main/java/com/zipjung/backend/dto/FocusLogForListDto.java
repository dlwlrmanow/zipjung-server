package com.zipjung.backend.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class FocusLogForListDto {
    // TODO: username 추가
    private String username;
    private Long postId;
    // 리스트에 표면적으로 보일 내용
    private Long focusLogId;
    private String title;
    private int rating;
    private LocalDateTime postCreatedAt;
    private Long totalFocusedTime;

    @QueryProjection
    public FocusLogForListDto(Long postId, Long focusLogId, String title, int rating, LocalDateTime postCreatedAt, Long totalFocusedTime) {
        this.postId = postId;
        this.focusLogId = focusLogId;
        this.title = title;
        this.rating = rating;
        this.postCreatedAt = postCreatedAt;
        this.totalFocusedTime = totalFocusedTime;
    }
}
