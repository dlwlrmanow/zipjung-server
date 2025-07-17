package com.zipjung.backend.service;

import com.zipjung.backend.dto.FocusLogDto;
import com.zipjung.backend.entity.FocusLog;
import com.zipjung.backend.entity.Post;
import com.zipjung.backend.repository.FocusLogRepository;
import com.zipjung.backend.repository.FocusTimeRepository;
import com.zipjung.backend.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FocusLogService {
    // TODO: 여기에서 하나의 @Transactional 로 묶어서 처리해야 데이터 꼬이지 않음
    private final FocusLogRepository focusLogRepository;
    private final PostRepository postRepository;
    private final FocusTimeRepository focusTimeRepository;

    @Transactional
    public void saveFocusLog(FocusLogDto focusLogDto) {
        // 1. post.id 만들기
        Post post = new Post(focusLogDto.getTitle(), focusLogDto.getContent(), focusLogDto.getServiceId());
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
}
