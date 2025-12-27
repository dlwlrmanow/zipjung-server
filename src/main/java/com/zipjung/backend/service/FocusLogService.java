package com.zipjung.backend.service;

import com.zipjung.backend.dto.FocusLogDto;
import com.zipjung.backend.dto.FocusLogForListDto;
import com.zipjung.backend.dto.LocationRequest;
import com.zipjung.backend.dto.NotificationDto;
import com.zipjung.backend.entity.*;
import com.zipjung.backend.exception.AlreadyExistDataException;
import com.zipjung.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FocusLogService {
    private final FocusLogRepository focusLogRepository;
    private final PostRepository postRepository;
    private final FocusTimeRepository focusTimeRepository;
    private final LocationRepository locationRepository;

    // SSE
    private final NotificationRepository notificationRepository;
    private final ApplicationEventPublisher eventPublisher;

    // Redis
    private final RedisRankService redisRankService;

    // TODO: Flutter 수정! 위치 추가하는 부분 추가됨
    @Transactional // transactional로 순서 꼬이지 않고 비동기적으로 처리
    public boolean saveFocusLog(FocusLogDto focusLogDto, Long memberId) {
        // 1. post.id 만들기
        Post post = Post.builder()
                .title(focusLogDto.getTitle())
                .content(focusLogDto.getContent())
                .serviceId(1L) // DONE: service_id 생성 후 변경
                .memberId(memberId)
                .build();
        postRepository.save(post);

        // 2. post.id 가져오기
        Long postId = post.getId();

        // 3. focus_log.id 만들기
        FocusLog focusLog = FocusLog.builder()
                        .postId(postId)
                                .rating(focusLogDto.getRating())
                                        .build();
        focusLogRepository.save(focusLog);

        // 4. 해당하는 focus_time에 focus_log.id update
        Long focusLogId = focusLog.getId();

        for(Long focusTimeId : focusLogDto.getFocusTimeId()) { // focus_time_id가 리스트 형태로 들어옴
            int count = focusTimeRepository.updateFocusLogId(focusLogId, focusTimeId);
            if (count != 0) {
                log.info("update success: {}", count);
                return true;
            }
        }

        return false;
    }

    @Transactional
    public void addLocation(Long memberId, LocationRequest locationRequest) {

        // 이미 데이터가 존재하는 경우 예외 던지기
        if(focusTimeRepository.isLocationExist(locationRequest.getFocusTimeId())) {
            throw new AlreadyExistDataException("location already exist");
        }

        // 위치 데이터 추가
        Post post = Post.builder()
                .title("location")
                .serviceId(1L) // DONE: service_id 생성 후 변경
                .memberId(memberId)
                .isDeleted(false)
                .build();
        postRepository.save(post);
        Long postId = post.getId();

        // 위치테이블에 데이터 추가
        Location location = Location.builder()
                .postId(postId)
                .latitude(locationRequest.getLongitude())
                .longitude(locationRequest.getLongitude())
                .placeUrl(locationRequest.getPlaceUrl())
                .placeId(locationRequest.getPlaceId())
                .build();
        locationRepository.save(location);

        FocusLog focusLog = FocusLog.builder()
                .postId(postId)
                .isDeleted(false)
                .build();
        focusLogRepository.save(focusLog);
        Long focusLogId = focusLog.getId();

        // request에 담아온 focusTimeId로 찾아서 focusLogId 추가
        Long focusTimeId = locationRequest.getFocusTimeId();

        FocusTime focusTime = focusTimeRepository.findById(focusTimeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 기록이 존재하지 않습니다. focus_time_id=" + focusTimeId));

        focusTime.updateFocusLogId(focusLogId);

        // sse 알림 보내기 위해 notification 알림 적재
        Notification addLocationNotification = Notification.builder()
                .notificationType(NotificationType.ADD_LOCATION)
                .title("위치 추가")
                .message("위치가 성공적으로 추가되었어요!")
                .fromId(memberId)
                .toId(memberId)
                .isRead(false)
                .build();
        notificationRepository.save(addLocationNotification);

        // 이벤트 발송
        eventPublisher.publishEvent(new NotificationDto(memberId, addLocationNotification.getId()));

        // redis 인기 검색어
        redisRankService.increasePopularSpotScore(locationRequest.getSpotName(), locationRequest.getPlaceId());
    }


    @Transactional(readOnly = true)
    public List<FocusLogForListDto> getFocusLogList(Long memberId) {
        // TODO: postId, focusLogId가 is_deleted true인거 제외하고 가져오기
        return focusLogRepository.getFocusLogList(memberId);
    }
}