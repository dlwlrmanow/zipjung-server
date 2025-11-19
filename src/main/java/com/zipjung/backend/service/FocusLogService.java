package com.zipjung.backend.service;

import com.zipjung.backend.dto.FocusLogDto;
import com.zipjung.backend.dto.FocusLogForListDto;
import com.zipjung.backend.entity.FocusLog;
import com.zipjung.backend.entity.Post;
import com.zipjung.backend.repository.FocusLogRepository;
import com.zipjung.backend.repository.FocusTimeRepository;
import com.zipjung.backend.repository.MemberRepository;
import com.zipjung.backend.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FocusLogService {
    private final FocusLogRepository focusLogRepository;
    private final PostRepository postRepository;
    private final FocusTimeRepository focusTimeRepository;

    @Transactional // transactional로 순서 꼬이지 않고 비동기적으로 처리하도록
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
        System.out.println("postId: " + postId);

        // 3. focus_log.id 만들기
        FocusLog focusLog = new FocusLog(postId, focusLogDto.getRating());
        focusLogRepository.save(focusLog);

        // 4. 해당하는 focus_time에 focus_log.id update
        Long focusLogId = focusLog.getId();

        for(Long focusTimeId : focusLogDto.getFocusTimeId()) { // focus_time_id가 리스트 형태로 들어옴
            int count = focusTimeRepository.updateFocusLogId(focusLogId, focusTimeId);
            if (count != 0) {
                System.out.println("update success: " + count);
                return true;
            }

        }
        return false;
    }

    @Transactional(readOnly = true)
    public List<FocusLogForListDto> getFocusLogList(Long memberId) {
        // TODO: postId, focusLogId가 is_deleted true인거 제외하고 가져오기
        return focusLogRepository.getFocusLogList(memberId);
    }
}
