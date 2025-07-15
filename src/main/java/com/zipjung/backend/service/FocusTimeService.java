package com.zipjung.backend.service;

import com.zipjung.backend.entity.FocusTime;
import com.zipjung.backend.repository.FocusTimeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FocusTimeService {
    private final FocusTimeRepository focusTimeRepository;

    @Transactional
    public void saveFocusTime(FocusTime focusTime) {
        focusTimeRepository.save(focusTime);
        if (focusTime.getId() == null) {
            System.out.println("focus time id is null: 생성안됨!!");
        }
    }
}
