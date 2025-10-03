package com.zipjung.backend.repository;

import com.zipjung.backend.entity.FocusTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface FocusTimeRepository extends JpaRepository<FocusTime, Long> {
//    @Modifying
//    @Query(value = "INSERT INTO FocusTime(focusedTime, startFocusTime, memberId) VALUES (:focusedTime, :startFocusTime, :memberId)")
//    void saveWithMemberID(@Param("focusedTime") LocalDateTime focusedTime, @Param("startFocusTime") String startFocusTime, @Param("memberId") Long memberId);

    @Modifying
    @Query("UPDATE FocusTime f SET f.focusLogId = :focusLogId WHERE f.id = :focusTimeId")
    // TODO: update한 postId를 return하도록 수정
    int updateFocusLogId(@Param("focusLogId") Long focusLogId, @Param("focusTimeId")Long focusTimeId);

    // 사용자 집중 시간 리스트에서 최근 일주일 집중시간 + 시간에 대한 기록 가져오기
    @Query("SELECT f FROM FocusTime f WHERE f.createdAt >= :oneWeekAgo AND f.isDeleted = false AND f.focusLogId IS NULL") // 최근 일주일내 기록만 가져오기
    List<FocusTime> getRecentWeekFocusTimes(@Param("oneWeekAgo") LocalDateTime oneWeekAgo);

//    // totalFocusedTime 가져오기
//    // DONE: focusLogId에 해당하는 총 집중 시간
//    @Query("SELECT f.focusedTime FROM FocusTime f WHERE f.id = :focusLogId")
//    List<Long> getTotalFocusTime(@Param("focusLogId") Long focusLogId);

    @Query("SELECT f.focusedTime FROM FocusTime f WHERE f.createdAt BETWEEN :startOfDay AND :endOfDay AND f.isDeleted = false")
    List<Long> getTodayFocusTimes(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);
}
