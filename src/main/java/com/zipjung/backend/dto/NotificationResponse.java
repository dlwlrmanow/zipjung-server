package com.zipjung.backend.dto;

import com.zipjung.backend.entity.NotificationType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class NotificationResponse {
    private NotificationType notificationType;
    private String title;
    private String message;
    // 보내는 사람은 authentication으로 가져옴
    private Long toId; // TODO: 나중에 다른 사람에게 보내는 알림인 경우에는 username으로 수정할 필요!
    private Long memberId; // 보내는 사람

    @Builder
    public NotificationResponse(NotificationType notificationType, String title, String message, Long toId, Long memberId) {
        this.notificationType = notificationType;
        this.title = title;
        this.message = message;
        this.toId = toId;
        this.memberId = memberId;
    }
}
