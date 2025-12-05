package com.zipjung.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotificationDto {
    private Long memberId;
    private Long notificationId;
}
