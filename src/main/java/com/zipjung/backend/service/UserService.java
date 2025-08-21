package com.zipjung.backend.service;

import com.zipjung.backend.dto.RegisterDto;
import com.zipjung.backend.entity.Profile;
import com.zipjung.backend.entity.User;
import com.zipjung.backend.repository.UserCustomRepository;
import com.zipjung.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserCustomRepository userCustomRepository;
    private final UserRepository userRepository;
    private final DataSourceTransactionManager dataSourceTransactionManager;

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
        // 2: 가입한 적 있음
        if(state == 2L) {
            System.out.println("존재하는 email : " + registerDto.getEmail());
        }
        // 3: 중복된 username
        if(state == 3L) {
            System.out.println("중복 username : " + registerDto.getUsername());
        }
    }

    // TODO: email로 가입한 계정 찾기
    // TODO: 예외처리 적용해보기
    @Transactional
    public String findUsernameByEmail(String email) {
        Optional<String> usernameOrNot = userRepository.findByEmail(email);
        String username = usernameOrNot.get();
        System.out.println(username);
        return username;
    }


}
