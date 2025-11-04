package com.zipjung.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationDto {
    private String title;
    private String message;
    // 보내는 사람은 authentication으로 가져옴
    private Long toId;
    private Boolean isRead;
}
