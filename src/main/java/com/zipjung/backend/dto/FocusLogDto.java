package com.zipjung.backend.dto;

import java.util.List;

public record FocusLogDto(String username, List<Long> focusTimeId, Long memberId, String title, String content, int rating, Long serviceId) {
}
