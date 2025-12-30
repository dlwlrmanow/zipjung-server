package com.zipjung.backend.repository;

import com.zipjung.backend.dto.FocusTimeWithEndTimeResponse;
import com.zipjung.backend.entity.FocusTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface FocusTimeRepository extends JpaRepository<FocusTime, Long>, FocusTimeCustomRepository {
    @Modifying(clearAutomatically = true)
    @Query("UPDATE FocusTime f SET f.focusLogId = :focusLogId WHERE f.id = :focusTimeId")
        // TODO: update한 postId를 return하도록 수정
    int updateFocusLogId(@Param("focusLogId") Long focusLogId, @Param("focusTimeId") Long focusTimeId);

    // 사용자 집중 시간 리스트에서 최근 일주일 집중시간 + 시간에 대한 기록 가져오기
    @Query("SELECT f FROM FocusTime f " +
            "WHERE f.createdAt >= :oneWeekAgo " +
            "AND f.isDeleted = false " +
            "AND f.focusLogId IS NULL " +
            "AND f.memberId = :memberId")
    // 최근 일주일내 기록만 가져오기
    List<FocusTime> getRecentWeekFocusTimes(@Param("oneWeekAgo") LocalDateTime oneWeekAgo, Long memberId);

//    // totalFocusedTime 가져오기
//    // DONE: focusLogId에 해당하는 총 집중 시간
//    @Query("SELECT f.focusedTime FROM FocusTime f WHERE f.id = :focusLogId")
//    List<Long> getTotalFocusTime(@Param("focusLogId") Long focusLogId);

    @Query("SELECT f.focusedTime FROM FocusTime f " +
            "WHERE f.createdAt BETWEEN :startOfDay AND :endOfDay " +
                "AND f.isDeleted = false")
    List<Long> getTodayFocusTimes(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);


    // TODO: location 있는 것고 없는 걸로 나눠서 보내기
//    @Query("SELECT new com.zipjung.backend.dto.FocusTimeWithEndTimeResponse(f.id, f.focusedTime, f.startFocusTime, f.endFocusTime) " +
//            "FROM FocusTime f " +
//            "WHERE f.createdAt BETWEEN :startOfDay AND :endOfDay " +
//                "AND f.isDeleted = false " +
//                "AND f.memberId = :memberId")
//    List<FocusTimeWithEndTimeResponse> getTodayFocusTimesWithEndTime(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay, Long memberId);


    // 오늘 집중 시간 전체 삭제
    @Modifying(clearAutomatically = true)
    @Query("UPDATE FocusTime f " +
            "SET f.isDeleted = true " +
            "WHERE f.createdAt BETWEEN :startOfDay AND :endOfDay " +
                "AND f.memberId = :memberId")
    int updateFocusedItemDeleteAll(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay, @Param("memberId") Long memberId);


    // 해당 item 하나만 삭제
    @Modifying(clearAutomatically = true)
    @Query("UPDATE FocusTime f " +
            "SET f.isDeleted = true " +
            "WHERE f.memberId = :memberId " +
                "AND f.id = :focusTimeId")
    int updateFocusedItemDelete(@Param("memberId") Long memberId, @Param("focusTimeId") Long focusTimeId);


    // notification 보내기 위해 삭제한 item의 내용 가져오기
    @Query("SELECT new com.zipjung.backend.dto.FocusTimeWithEndTimeResponse(f.id, f.focusedTime, f.startFocusTime, f.endFocusTime) " +
            "FROM FocusTime f " +
            "WHERE f.isDeleted = true " +
            "AND f.memberId = :memberId " +
            "AND f.id = :focusTimeId")
    FocusTimeWithEndTimeResponse getDeletedFocusTimeById(@Param("memberId") Long memberId, @Param("focusTimeId") Long focusTimeId);
}
