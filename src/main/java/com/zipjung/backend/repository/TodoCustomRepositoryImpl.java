package com.zipjung.backend.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zipjung.backend.entity.QPost;
import com.zipjung.backend.entity.QTodo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

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

}
