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
    // DONE: 여기에서 하나의 @Transactional 로 묶어서 처리해야 데이터 꼬이지 않음
    private final FocusLogRepository focusLogRepository;
    private final PostRepository postRepository;
    private final FocusTimeRepository focusTimeRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void saveFocusLog(FocusLogDto focusLogDto) {
        // 0. username -> user_id 변환 필요
        Long userId = memberRepository.findByUsername(focusLogDto.getUsername());
        System.out.println("[FocusLogService] userId: " + userId);
        // 1. post.id 만들기
        // DONE: post에 member_id 추가
        Post post = Post.builder()
                .title(focusLogDto.getTitle())
                .content(focusLogDto.getContent())
                .serviceId(focusLogDto.getServiceId())
                .memberId(userId)
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
            if (count != 0) System.out.println("update success: " + count);
        }
    }

    @Transactional(readOnly = true)
    public List<FocusLogForListDto> getFocusLogs() {
        // DONE: is_deleted = 0인 리스트만 뽑기
        // TODO: 해당하는 userId만 뽑기
        return focusLogRepository.getFocusLogList();
    }
}
