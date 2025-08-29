package com.zipjung.backend.service;

import com.zipjung.backend.entity.FocusTime;
import com.zipjung.backend.repository.FocusTimeRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FocusTimeService {
    private final FocusTimeRepository focusTimeRepository;
    // TODO: member_id 추가
    @Transactional
    public Long saveFocusTime(FocusTime focusTime) {
        focusTimeRepository.save(focusTime);

        // 저장된 focusTime_id return
        if (focusTime.getId() == null) {
            System.out.println("focus time id is null: 생성안됨!!");
        }

        return focusTime.getId();
    }

    @Transactional(readOnly = true)
    public List<FocusTime> fetchRecentFocusTime() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        List<FocusTime> focusTimes = focusTimeRepository.getRecentWeekFocusTimes(oneWeekAgo);

        if(focusTimes.size() == 0) {
            System.out.println("List<FocusTime> = null");
            return null;
        }

        return focusTimes;
    }

    @Transactional(readOnly = true) // 오늘의 집중 시간 가져오기
    public Long fetchTodayFocusTime() {
        LocalDate today = LocalDate.now();

        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay(); // 내일이 되는 00:00:00

        List<Long> timeList = focusTimeRepository.getTodayFocusTimes(startOfDay, endOfDay);

        long todayTimeSum = 0;
        for (Long time : timeList) {
            if(time != null) {
                todayTimeSum += time;
            }
        }
        return todayTimeSum;
    }

    @Transactional(readOnly = true)
    public FocusTime fetchFocusTimeById(Long id) {
        return focusTimeRepository.findById(id).orElse(null);
    }
}
