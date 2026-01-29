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
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Slf4j
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
    public void saveFocusTime(FocusTimeRequest focusTimeRequest, Long memberId) {
        // SSE 추가하기
        // 오늘 쓴 데이터(오늘 집중 시간 데이터)가 있는지 확인
        Long totalFocusedTimeToday = focusTimeRepository.getLastTotalFocusedTimeToday(memberId);

        FocusTime focusTime = FocusTime.builder()
                .focusedTime(focusTimeRequest.focusedTime())
                .startFocusTime(focusTimeRequest.startFocusTime())
                .endFocusTime(focusTimeRequest.endFocusTime())
                .memberId(memberId)
                .totalToday(focusTimeRequest.focusedTime() + totalFocusedTimeToday) // totalFocusedTimeToday이 없는 경우 어차피 0
                .build();
        focusTimeRepository.save(focusTime);
    }

    @Transactional(readOnly = true)
    public List<FocusTime> fetchRecentFocusTime(Long memberId) {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        List<FocusTime> focusTimes = focusTimeRepository.getRecentWeekFocusTimes(oneWeekAgo, memberId);

        if(focusTimes.isEmpty()) {
            System.out.println("List<FocusTime> = null");
            return null;
        }
        System.out.println("[FocusTimeService]");
        System.out.println(focusTimes.size());

        return focusTimes;
    }

    // 오늘의 집중 시간 가져오기
    @Transactional(readOnly = true)
    public FocusedTodayTotalResponse fetchTodayFocusTime(Long memberId) {
        // 오늘 누적 시간 맨 마지막 데이터만 가져와서 파싱
        Long totalFocusedTimeToday = focusTimeRepository.getLastTotalFocusedTimeToday(memberId);

        // 00:00:00 형태로 파싱
        return new FocusedTodayTotalResponse(totalFocusedTimeToday);
    }

    @Transactional(readOnly = true)
    public FocusTime fetchFocusTimeById(Long id) {
        return focusTimeRepository.findById(id).orElse(null);
    }

    public TodayFocusTimeListResponse fetchTodayFocusTimesWithEndTime(Long memberId) {
        // 오늘의 날짜 데이터
        LocalDate todayKst = LocalDate.now(ZoneId.of("Asia/Seoul"));

        LocalDateTime startOfDay = todayKst.minusDays(1).atStartOfDay(); // 2025-12-30 00:00:00
        LocalDateTime endOfDay = todayKst.plusDays(1).atStartOfDay();

        log.info("조회 범위 (KST 기준): {} ~ {}", startOfDay, endOfDay);

        // TODO: locationIsDeleted가 null로 나오면 삭제되었거나 아직 데이터 추가되지 않은 것 -> 그냥 '+'추가하면 된다.
        TodayFocusTimeListResponse todayFocusTimeListResponse = TodayFocusTimeListResponse.builder()
                .focusTimeWithLocationDtoList(focusTimeRepository.getFocusTimeWithLocationDtoList(startOfDay, endOfDay, memberId))
                .build();

        return todayFocusTimeListResponse;
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
