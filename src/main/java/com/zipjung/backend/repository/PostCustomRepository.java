package com.zipjung.backend.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface PostCustomRepository {
    void deletePost(Long postId);
}
