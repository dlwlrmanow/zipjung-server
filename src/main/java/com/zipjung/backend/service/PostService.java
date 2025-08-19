package com.zipjung.backend.service;

import com.zipjung.backend.entity.Post;
import com.zipjung.backend.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found + postId: " + postId));
        post.setIsDeleted(true);
        postRepository.save(post);
    }
}
