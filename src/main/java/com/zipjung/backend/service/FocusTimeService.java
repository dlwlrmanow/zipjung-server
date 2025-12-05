package com.zipjung.backend.service;

import com.zipjung.backend.dto.*;
import com.zipjung.backend.entity.FocusTime;
import com.zipjung.backend.entity.Notification;
import com.zipjung.backend.entity.NotificationType;
import com.zipjung.backend.exception.FocusTimeException;
import com.zipjung.backend.repository.EmitterRepository;
import com.zipjung.backend.repository.FocusTimeRepository;

import com.zipjung.backend.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FocusTimeService {
    private final FocusTimeRepository focusTimeRepository;

    // SSE 관련
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;
    private final EmitterRepository emitterRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    // TODO: SSE 추가하기
    public Long saveFocusTime(FocusTimeRequestDto focusTimeRequestDto, Long memberId) {
        FocusTime focusTime = new FocusTime();
        focusTime.setFocusedTime(focusTimeRequestDto.getFocusedTime());
        focusTime.setStartFocusTime(focusTimeRequestDto.getStartFocusTime());
        focusTime.setMemberId(memberId);
        FocusTime saved = focusTimeRepository.save(focusTime);

        if(saved.getId() == null) {
            throw new FocusTimeException("FocusTime save failed");
        }

        return saved.getId();
    }

    @Transactional(readOnly = true)
    public List<FocusTime> fetchRecentFocusTime(Long memberId) {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        List<FocusTime> focusTimes = focusTimeRepository.getRecentWeekFocusTimes(oneWeekAgo, memberId);

        if(focusTimes.size() == 0) {
            System.out.println("List<FocusTime> = null");
            return null;
        }
        System.out.println("[FocusTimeService]");
        System.out.println(focusTimes.size());

        return focusTimes;
    }

    @Transactional(readOnly = true) // 오늘의 집중 시간 가져오기
    public FocusedTodayTotalResponse fetchTodayFocusTime() {
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

        // 00:00:00 형태로 파싱
        FocusedTodayTotalResponse totalToday = new FocusedTodayTotalResponse(todayTimeSum);
        System.out.println("[FocusTimeService] totalToday = " + totalToday.getTodayFocusTime() + "\n totalTodayStr: " + totalToday.getFocusedTimeStr());
        return totalToday;
    }

    @Transactional(readOnly = true)
    public FocusTime fetchFocusTimeById(Long id) {
        return focusTimeRepository.findById(id).orElse(null);
    }

    public Result<List<FocusTimeWithEndTimeResponse>> fetchTodayFocusTimesWithEndTime(Long memberId) {
        // 오늘의 날짜 데이터
        LocalDate today = LocalDate.now();

        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        List<FocusTimeWithEndTimeResponse> focusedTimeToday = focusTimeRepository.getTodayFocusTimesWithEndTime(startOfDay, endOfDay, memberId);

        return new Result<>(focusedTimeToday, focusedTimeToday.size());
    }

    @Transactional
    public void deleteFocusedItemAll(Long memberId) {
        LocalDate today = LocalDate.now();

        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        int result = focusTimeRepository.updateFocusedItemDeleteAll(startOfDay, endOfDay, memberId);
        System.out.println("[deleteFocusedItemAll] result count: " + result);

        if(result <= 0) {
            throw new FocusTimeException("FocusTime delete failed");
        }

        // Notification에 알림 적재
        Notification deleteFocusedTimeAllNotification = Notification.builder()
                .notificationType(NotificationType.DELETE_FOCUSED)
                .title("focused time 삭제")
                .message(result + " 개의 집중 시간이 삭제되었어요!")
                .fromId(memberId)
                .toId(memberId)
                .isRead(false)
                .build();
        notificationRepository.save(deleteFocusedTimeAllNotification);

        eventPublisher.publishEvent(new NotificationDto(memberId, deleteFocusedTimeAllNotification.getId()));
    }

    @Transactional
    public void deleteFocusTimeById(Long memberId, Long focusTimeId) {
        int result = focusTimeRepository.updateFocusedItemDelete(memberId, focusTimeId);
        System.out.println("[deleteFocusTimeById] result count: " + result);

        if(result < 0) {
            throw new FocusTimeException("FocusTime delete failed");
        }

        // 파싱된 데이터를 사용하기 위해 response 재사용
        FocusTimeWithEndTimeResponse focusTimeInfo = focusTimeRepository.getDeletedFocusTimeById(memberId, focusTimeId);
        // notification message
        String focusMessage = "focused time: " + focusTimeInfo.getStartTime() + " ~ " + focusTimeInfo.getEndTime() +
                "\n" + "total: " + focusTimeInfo.getFocusedTimeStr();
        System.out.println("*****[focusMessage]" + focusMessage);

        // 삭제된 내용 실시간 알림
        Notification deleteFocusedTimeOneNotification = Notification.builder()
                .notificationType(NotificationType.DELETE_FOCUSED)
                .title("focused time 삭제")
                .message(focusMessage)
                .fromId(memberId)
                .toId(memberId)
                .isRead(false)
                .build();
        notificationRepository.save(deleteFocusedTimeOneNotification);

        eventPublisher.publishEvent(new NotificationDto(memberId, deleteFocusedTimeOneNotification.getId()));
    }
}
