package com.zipjung.backend.service;

import com.zipjung.backend.entity.ApiTest;
import com.zipjung.backend.repository.ApiTestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApiTestService {
    private final ApiTestRepository apiTestRepository;

    public ApiTest saveApiTest(ApiTest apiTest) {
        return apiTestRepository.save(apiTest);
    }
}
