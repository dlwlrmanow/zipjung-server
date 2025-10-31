package com.zipjung.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationDto {
    private String title;
    private String message;
    private Long toId;
    private Boolean isRead;
}
