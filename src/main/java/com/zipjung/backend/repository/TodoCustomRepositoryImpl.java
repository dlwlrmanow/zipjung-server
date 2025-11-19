package com.zipjung.backend.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zipjung.backend.dto.TodoResponseDto;
import com.zipjung.backend.entity.QPost;
import com.zipjung.backend.entity.QTodo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TodoCustomRepositoryImpl implements TodoCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public long countByNotDone(Long memberId) {
        LocalDateTime oneWeek = LocalDateTime.now().minusWeeks(1);

        QPost post = QPost.post;
        QTodo todo = QTodo.todo;

        long count = jpaQueryFactory
                .select(todo.task.count())
                .from(todo)
                .leftJoin(post).on(todo.postId.eq(post.id))
                .where(
                        post.memberId.eq(memberId)
                                .and(post.createdAt.eq(oneWeek))
                                .and(todo.isDone.eq(false))
                )
                .fetchCount();

        return count;
    }

    @Override
    public List<TodoResponseDto> getTodos(Long memberId) {
        QTodo todo = QTodo.todo;
        QPost post = QPost.post;

        List<TodoResponseDto> todos = jpaQueryFactory
                .select(
                        Projections.constructor(TodoResponseDto.class,
                        todo.id, todo.task, todo.createdAt
                        )
                )
                .from(todo)
                .leftJoin(post).on(todo.postId.eq(post.id))
                .where(
                        post.memberId.eq(memberId)
                                .and(todo.isDone.eq(false))
                )
                .fetch();
        return todos;
    }

}
