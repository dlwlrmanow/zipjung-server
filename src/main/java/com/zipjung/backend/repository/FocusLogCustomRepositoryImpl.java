package com.zipjung.backend.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zipjung.backend.dto.FocusLogForListDto;
import com.zipjung.backend.dto.QFocusLogForListDto;
import com.zipjung.backend.entity.QFocusLog;
import com.zipjung.backend.entity.QFocusTime;
import com.zipjung.backend.entity.QPost;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FocusLogCustomRepositoryImpl implements FocusLogCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<FocusLogForListDto> getFocusLogList(Long memberId) {
        QPost post = QPost.post;
        QFocusLog focusLog = QFocusLog.focusLog;
        QFocusTime focusTime = QFocusTime.focusTime;

        List<FocusLogForListDto> focusLogList = jpaQueryFactory
                .select(new QFocusLogForListDto(post.id, focusLog.id, post.title, focusLog.rating, post.createdAt, focusTime.focusedTime.sum()))
                .from(focusLog)
                .leftJoin(post).on(focusLog.postId.eq(post.id))
                .leftJoin(focusTime).on(focusLog.id.eq(focusTime.focusLogId))
                .where(post.isDeleted.eq(false).and(post.memberId.eq(memberId)))
                .groupBy(focusLog.id, post.id, post.title, focusLog.rating, post.createdAt)
                .fetch();

        return focusLogList;
    }
}
