package com.bside.mzoffice.user.service;

import com.bside.mzoffice.common.domain.ResponseCode;
import com.bside.mzoffice.common.exception.customException.NotFoundException;
import com.bside.mzoffice.user.domain.SnsType;
import com.bside.mzoffice.user.domain.User;
import com.bside.mzoffice.user.dto.response.AuthUserResponse;
import com.bside.mzoffice.user.dto.response.TokenResponse;
import com.bside.mzoffice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
                .accessToken(authUserResponse.getAccessToken())
                .refreshToken(authUserResponse.getRefreshToken())
                .tokenExpiry(LocalDateTime.now().plusSeconds(authUserResponse.getExpiresIn()))
                .build();

        userRepository.save(user);
        return user;
    }

    @Transactional(readOnly = true)
    public User get(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException(ResponseCode.NOT_FOUND_USER));
    }

    @Transactional
    public void updateToken(User user, TokenResponse tokenResponse) {
        user.setAccessToken(tokenResponse.getAccessToken());
        user.setRefreshToken(tokenResponse.getRefreshToken());
        user.setTokenExpiry(LocalDateTime.now().plusSeconds(tokenResponse.getExpiresIn()));
    }

    @Transactional
    public void deleteUser(User user) {
        userRepository.delete(user);
    }
}
