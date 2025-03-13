package com.bside.mzoffice.user.service;

import com.bside.mzoffice.common.enums.ResponseCode;
import com.bside.mzoffice.common.exception.customException.AuthLoginException;
import com.bside.mzoffice.common.response.ServerResponse;
import com.bside.mzoffice.common.service.JwtService;
import com.bside.mzoffice.user.enums.SnsType;
import com.bside.mzoffice.user.enums.User;
import com.bside.mzoffice.user.dto.response.AuthUserResponse;
import com.bside.mzoffice.user.dto.response.TokenResponse;
import com.bside.mzoffice.user.naver.NaverTokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
            userService.updateToken(user, (TokenResponse) authUserResponse);
        } else {
            user = userService.sign(authUserResponse);
        }

        return jwtService.generateJwt(user.getId());
    }

    @Transactional
    public ServerResponse<ResponseCode> revokeAccount(Authentication authentication, SnsType snsType) {
        Long userId = Long.parseLong(authentication.getName());
        User user = userService.get(userId);

        LocalDateTime tokenExpiryTime = user.getTokenExpiry();
        LocalDateTime currentTime = LocalDateTime.now();

        ResponseCode responseCode = null;
        if (snsType.equals(SnsType.NAVER)) {
            if (tokenExpiryTime.isBefore(currentTime)) {
                NaverTokenResponse naverTokenResponse = naverAuthService.refreshAccessToken(user.getRefreshToken());
                userService.updateToken(user, naverTokenResponse);
            }

            responseCode = naverAuthService.revokeNaverAccount(user.getAccessToken());
        }

        if (responseCode == null) {
            throw new AuthLoginException(ResponseCode.SERVER_ERROR);
        }
        if (!responseCode.equals(ResponseCode.SUCCESS)) {
            throw new AuthLoginException(responseCode);
        }

        userService.deleteUser(user);
        return ServerResponse.successResponse();
    }
}
