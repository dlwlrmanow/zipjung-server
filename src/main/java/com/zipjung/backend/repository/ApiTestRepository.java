package com.zipjung.backend.repository;

import com.zipjung.backend.model.ApiTest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiTestRepository extends JpaRepository<ApiTest, Long> {
}
