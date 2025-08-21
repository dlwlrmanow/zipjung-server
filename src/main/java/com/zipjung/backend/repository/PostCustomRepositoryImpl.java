package com.zipjung.backend.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zipjung.backend.entity.QFocusLog;
import com.zipjung.backend.entity.QFocusTime;
import com.zipjung.backend.entity.QPost;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostCustomRepositoryImpl implements PostCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    // DONE: post_id로 해당되는 데이터를 찾아서 is_deleted = true
    // DONE: focusTime에서 focus_log_id -> null로 변경해주기
    @Override
    public void deletePost(Long postId) {
        QPost post = QPost.post;
        QFocusTime focusTime = QFocusTime.focusTime;
        QFocusLog focusLog = QFocusLog.focusLog;

        jpaQueryFactory
                .update(post)
                .set(post.isDeleted, true)
                .where(post.id.eq(postId))
                .execute();
        jpaQueryFactory
                .update(focusLog)
                .set(focusLog.isDeleted, true)
                .where(focusLog.postId.eq(postId))
                .execute();

        long focusLogId = jpaQueryFactory.select(focusLog.id).from(focusLog).where(focusLog.postId.eq(postId)).fetchOne();

        jpaQueryFactory
                .update(focusTime)
                .set(focusTime.focusLogId, (Long) null)
                .where(focusTime.focusLogId.eq(focusLogId))
                .execute();
    }
}
