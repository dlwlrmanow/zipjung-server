package com.zipjung.backend.service;

import com.zipjung.backend.entity.Post;
import com.zipjung.backend.repository.FocusLogRepository;
import com.zipjung.backend.repository.PostCustomRepository;
import com.zipjung.backend.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.PrivateKey;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostCustomRepository postCustomRepository;
    private final FocusLogRepository focusLogRepository;

    @Transactional
    public void deletePost(Long memberId, Long postId) {
        // TODO: memberId로 먼저 본인인지 확인 한 뒤 post id로 삭제
//        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found + postId: " + postId));
        // TODO: 해당 postId의 focusTime.focus_log_id -> null
//        post.setIsDeleted(true);
        postCustomRepository.deletePost(memberId, postId);
    }
}
