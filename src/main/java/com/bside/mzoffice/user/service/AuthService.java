package com.bside.mzoffice.user.service;

import com.bside.mzoffice.common.service.JwtService;
import com.bside.mzoffice.user.domain.SnsType;
import com.bside.mzoffice.user.domain.User;
import com.bside.mzoffice.user.dto.response.AuthUserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class AuthService {
    private final JwtService jwtService;
    private final NaverAuthService naverAuthService;
    private final UserService userService;

    @Transactional
    public String loginByOAuth(String code, SnsType snsType) {
        AuthUserResponse authUserResponse = null;

        if (snsType.equals(SnsType.NAVER)) {
            authUserResponse = naverAuthService.toRequestProfile(code);
        }

        Optional<User> userOptional = userService.getBySnsTypeAndSnsId(authUserResponse.getSnsType(), authUserResponse.getId());
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else {
            user = userService.sign(authUserResponse);
        }

        return jwtService.generateJwt(user.getId());
    }
}
