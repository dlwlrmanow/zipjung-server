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
    public Long countByNotDone(Long memberId) {
        LocalDateTime oneWeek = LocalDateTime.now().minusWeeks(1);

        QPost post = QPost.post;
        QTodo todo = QTodo.todo;

        Long count = jpaQueryFactory
                .select(todo.task.count()) // count()함수는 집계결과가 null인 경우 0을 반환
                .from(todo)
                .leftJoin(post).on(todo.postId.eq(post.id))
                .where(
                        post.memberId.eq(memberId)
                                .and(post.createdAt.eq(oneWeek))
                                .and(todo.isDone.eq(false))
                )
                .fetchOne(); // 단일 결과 조회시

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
                                .and(post.isDeleted.eq(false)) // soft delete되지 않은 post만
                )
                .fetch();
        return todos;
    }

    @Override
    public Long countListTodo(Long memberId) {
        QPost post = QPost.post;
        QTodo todo = QTodo.todo;

        Long count = jpaQueryFactory
                .select(todo.count()) // count는 무조건 0 -> null X
                .from(todo)
                .leftJoin(post).on(todo.postId.eq(post.id))
                .where(
                        post.memberId.eq(memberId)
                                .and(todo.isDone.eq(false))
                )
                .fetchOne();

        return count;
    }

    @Override
    public boolean deleteTodo (Long memberId, Long todoId) {
        QTodo todo = QTodo.todo;
        QPost post = QPost.post;

        // todo_id에 해당하는 postId 찾아오기
        Long postId = jpaQueryFactory
                .select(todo.postId)
                .from(todo)
                .where(todo.id.eq(todoId))
                .fetchOne();

        if(postId == null) {
            System.out.println("[TodoCustomRepositoryImpl] postId == null");
            return false;
        }

        long deleteCount = jpaQueryFactory
                .update(post)
                .set(post.isDeleted, true)
                .where(
                        post.id.eq(postId)
                                .and(post.memberId.eq(memberId))
                )
                .execute(); // 항상 원시 타입(long): null X

        if(deleteCount == 0) { // 오류
            System.out.println("[TodoCustomRepositoryImpl] 삭제된 post가 없음!!");
            return false;
        }

        return true;
    }

}
