package com.zipjung.backend.service;

import com.zipjung.backend.entity.Post;
import com.zipjung.backend.entity.Todo;
import com.zipjung.backend.repository.PostRepository;
import com.zipjung.backend.repository.TodoRepository;
import com.zipjung.backend.security.JwtTokenProvider;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
//@AutoConfigureMockMvc(addFilters = false)
public class TodoServiceTest {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private TodoRepository todoRepository;
    @Autowired
    private EntityManager em;

    private final Long TEST_MEMBER_ID = 999L; // 충돌날 일 없는 걸로

    @Test
    @Transactional
    @Rollback(false)
    void setUp() {
        System.out.println("=== 가짜 데이터 5,000건 생성 ===");

        for (int i = 0; i < 3000; i++) {
            // post id 먼 저생성
            Post post = Post.builder()
                    .title("Test Dummy Data")
                    .serviceId(2L)
                    .isDeleted(false)
                    .memberId(TEST_MEMBER_ID)
                    .build();
            postRepository.save(post);

            Long postId = post.getId();

            // todos 생성
            Todo todos = Todo.builder()
                    .task("가짜 데이터 todo 입니다." + "[" + i + "]")
                    .postId(postId)
                    .isDone(false)
                    .memberId(TEST_MEMBER_ID)
                    .build();

            todoRepository.save(todos);

            if (i % 100 == 0) {
                em.flush();
                em.clear();
                System.out.println(i + "개 돌파... (계속 진행 중)");
            }
        }

        // 중요: DB에 실제 쿼리가 날아가게 하고, JPA 1차 캐시를 비움
        // 이걸 안 하면 캐시 테스트가 아니라 JPA 1차 캐시 성능이 측정될 수 있어
        em.flush();
        em.clear();
        System.out.println("=== 데이터 생성 완료 ===");
    }

}
