package com.bside.mzoffice.user.service;

import com.bside.mzoffice.user.domain.SnsType;
import com.bside.mzoffice.user.domain.User;
import com.bside.mzoffice.user.dto.response.AuthUserResponse;
import com.bside.mzoffice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Optional<User> getBySnsTypeAndSnsId(SnsType snsType, String snsId) {
        return userRepository.findBySnsTypeAndSnsId(snsType, snsId);
    }

    @Transactional
    public User sign(AuthUserResponse authUserResponse) {
        User user = User.builder()
                .username(authUserResponse.getName())
                .email(authUserResponse.getEmail())
                .snsId(authUserResponse.getId())
                .snsType(authUserResponse.getSnsType())
                .build();

        userRepository.save(user);
        return user;
    }
}
