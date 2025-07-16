package com.zipjung.backend.service;

import com.zipjung.backend.entity.FocusTime;
import com.zipjung.backend.repository.FocusTimeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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

    @Transactional
    public List<FocusTime> fetchRecentFocusTime() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        List<FocusTime> focusTimes = focusTimeRepository.getRecentWeekFocusTimes(oneWeekAgo);

        if(focusTimes.size() == 0) {
            System.out.println("List<FocusTime> = null");
            return null;
        }

        return focusTimes;
    }
}
