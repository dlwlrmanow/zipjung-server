package com.zipjung.backend.service;

import com.zipjung.backend.dto.JoinRequestDto;
import com.zipjung.backend.entity.Member;
import com.zipjung.backend.entity.Profile;
import com.zipjung.backend.entity.Role;
import com.zipjung.backend.exception.DuplicateEmailException;
import com.zipjung.backend.exception.DuplicateUsernameException;
import com.zipjung.backend.repository.MemberCustomRepository;
import com.zipjung.backend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberCustomRepository memberCustomRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void registerMember(JoinRequestDto joinRequestDto) {
        // 존재하는 사용자 (중복 이메일 체크)
        // TODO: 굳이 dto전체 비교X -> getEmail로 확인
        if(memberCustomRepository.areYouNew(joinRequestDto)) {
            throw new DuplicateEmailException("존재하는 사용자: " + joinRequestDto.getEmail());
        }

        // 중복 username
        if(memberRepository.existsByUsername(joinRequestDto.getUsername())) {
            throw new DuplicateUsernameException("중복된 username: " + joinRequestDto.getUsername());
        }

        // 정상 등록이 가능
        Member member = new Member();
        member.setUsername(joinRequestDto.getUsername());
        member.setPassword(passwordEncoder.encode(joinRequestDto.getPassword())); // pw 암호화해서 우리 DB에 저장
        member.setRole(Role.ROLE_USER);
        memberRepository.save(member); // memberId 필요하기 때문에 먼저 save

        Profile profile = new Profile();
        profile.setEmail(joinRequestDto.getEmail());
        profile.setUserId(member.getId());
    }

    // TODO: email로 가입한 계정 찾기
    // TODO: 예외처리 적용해보기
    @Transactional
    public String findUsernameByEmail(String email) {
        Optional<String> usernameOrNot = memberRepository.findByEmail(email);
        String username = usernameOrNot.get();
        System.out.println(username);
        return username;
    }


}
