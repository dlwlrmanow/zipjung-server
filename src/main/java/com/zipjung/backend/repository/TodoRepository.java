package com.zipjung.backend.repository;

import com.zipjung.backend.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    @Query("SELECT t.task FROM Todo t WHERE t.createdAt >= :onWeek AND t.memberId = :memberId AND t.isDone = false ")
    List<Todo> getRecentWeekTodo(@Param("OneWeek")LocalDateTime oneWeek, Long memberId);

}
