package com.zipjung.backend.repository;

import com.zipjung.backend.dto.FocusLogForListDto;

import java.util.List;

public interface FocusLogCustomRepository {
    List<FocusLogForListDto> getFocusLogList(Long memberId);
}
