package com.zipjung.backend.service;

import com.zipjung.backend.dto.RegisterDto;
import com.zipjung.backend.entity.Profile;
import com.zipjung.backend.entity.User;
import com.zipjung.backend.repository.UserCustomRepository;
import com.zipjung.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserCustomRepository userCustomRepository;
    private final UserRepository userRepository;

    @Transactional
    public void SignUp(RegisterDto registerDto) {
        Long state = userCustomRepository.areYouNew(registerDto);

        // 0: 가입 가능
        if(state == 0L) {
            User user = new User();
            user.setUsername(registerDto.getUsername());
            user.setPassword(registerDto.getPassword());
            userRepository.save(user);

            Profile profile = new Profile();
            profile.setEmail(registerDto.getEmail());
            profile.setUserId(user.getId());

        }
        // TODO: Error msg 던지기
        // 2: 가입한 적 있음
        if(state == 2L) {}
        // 3: 중복된 username
        if(state == 3L) {}
    }


}
