package com.zipjung.backend.service;

import com.zipjung.backend.dto.FocusLogForListDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(addFilters = false)
public class FocusLogServiceTest {
    @Autowired
    private FocusLogService focusLogService;

    @Test
    void fetchFocusLogList() {
        Long memberId = 1L;

        List<FocusLogForListDto> logs = focusLogService.getFocusLogList(memberId);
        assertFalse(logs.isEmpty());

        logs.forEach(log -> {
            System.out.println("postId: " + log.getPostId());
            System.out.println("focusLogId: " + log.getFocusLogId());
            System.out.println("title: " + log.getTitle());
            System.out.println("rating: " + log.getRating());
            System.out.println("createdAt: " + log.getPostCreatedAt());
            System.out.println("focusedTimeSum: " + log.getTotalFocusedTime());
            System.out.println("-----------");
        });
    }
}
