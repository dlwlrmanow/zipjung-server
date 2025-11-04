package com.zipjung.backend.repository;

import com.zipjung.backend.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    // TODO: SSE notification은 여기서 보내지 말고 notification에 적재한 뒤 그 데이터 가져오기
    @Query("SELECT t.task FROM Todo t WHERE t.createdAt >= :onWeek AND t.memberId = :memberId AND t.isDone = false ")
    List<Todo> getRecentWeekTodo(@Param("OneWeek")LocalDateTime oneWeek, Long memberId);

}
