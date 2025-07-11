package com.zipjung.backend.service;

import com.zipjung.backend.model.ApiTest;
import com.zipjung.backend.repository.ApiTestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class ApiTestService {
    private final ApiTestRepository apiTestRepository;

    public ApiTest saveApiTest(ApiTest apiTest) {
        return apiTestRepository.save(apiTest);
    }
}
