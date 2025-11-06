package com.zipjung.backend.repository;

public interface TodoCustomRepository {
    long countByNotDone(Long memberId);
}
