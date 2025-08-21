package com.zipjung.backend.dto;

import lombok.*;

import java.util.List;

@Data
public class FocusLogDto {
    private List<Long> focusTimeId;
    private String title;
    private String content;
    private int rating;
    private Long serviceId;
}
