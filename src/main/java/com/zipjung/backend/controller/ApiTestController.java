package com.zipjung.backend.controller;

import com.zipjung.backend.entity.ApiTest;
import com.zipjung.backend.service.ApiTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api-test")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class ApiTestController {
    private final ApiTestService apiTestService;

    @PostMapping(produces = "application/json")
    public ResponseEntity<ApiTest> saveApiTest(@RequestBody ApiTest apiTest) {
        ApiTest savedTest = apiTestService.saveApiTest(apiTest);
        System.out.println(savedTest);
        return new ResponseEntity<>(savedTest, HttpStatus.CREATED);
    }

}
