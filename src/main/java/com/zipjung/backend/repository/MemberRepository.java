package com.zipjung.backend.repository;

import com.zipjung.backend.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    @Query("""
            SELECT u.username FROM Member u 
            LEFT JOIN Profile p 
            ON p.userId = u.id
            WHERE p.email = :email
            """)
    Optional<String> findByEmail(@Param("email") String email);

    Member findMemberByUsername(String username);

    boolean existsByUsername(String username);
}
