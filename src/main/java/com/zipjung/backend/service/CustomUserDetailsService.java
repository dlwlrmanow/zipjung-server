package com.zipjung.backend.service;

import com.zipjung.backend.entity.Member;
import com.zipjung.backend.repository.MemberRepository;
import com.zipjung.backend.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findMemberByUsername(username);
        if(member == null) {
            throw new UsernameNotFoundException("no user: " + username);
        }

        Long memberId = memberRepository.findIdByUsername(username); // username으로 memberId 찾아서 넣기
//        Role role = member.getRole();
//        if(role == null) {
//            throw new IllegalStateException(username + "/" + memberId + "의 user에 대해서 role이 null");
//        }
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(member.getRole().name()));
        return new CustomUserDetails(memberId, member.getUsername(), member.getPassword(), authorities); // security에서 사용하는 userr객체에 담아서 반환
    }

}
