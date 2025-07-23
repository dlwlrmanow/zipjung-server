package com.zipjung.backend.service;

import com.zipjung.backend.entity.FocusTime;
import com.zipjung.backend.repository.FocusTimeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    @Transactional // 오늘의 집중 시간 가져오기
    public Long fetchTodayFocusTime() {
        LocalDate today = LocalDate.now();

        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay(); // 내일이 되는 00:00:00

        List<FocusTime> timeList = focusTimeRepository.getTodayFocusTimes(startOfDay, endOfDay);

        return timeList.stream() // 1. List<FocusTime>을 Stream으로 변환
                .filter(f -> f.getFocusedTime() != null) // 2. 필터링으로 조건 걸기 null인 데이터는 제외
                .mapToLong(FocusTime::getFocusedTime) // 3. (= f -> f.getFocusedTime())필드를 꺼내고 long타입으로 형변환 List -> Long값들의 stream으로
                .sum(); // 4. 최종적으로 long들을 다 더해주는 역할
    }
}
