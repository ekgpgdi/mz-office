package com.bside.mzoffice.user.service;

import com.bside.mzoffice.common.enums.ResponseCode;
import com.bside.mzoffice.common.exception.customException.AuthLoginException;
import com.bside.mzoffice.user.enums.SnsType;
import com.bside.mzoffice.user.dto.response.AuthUserResponse;
import com.bside.mzoffice.user.naver.NaverProperties;
import com.bside.mzoffice.user.naver.NaverTokenResponse;
import com.bside.mzoffice.user.naver.NaverUnlinkResponse;
import com.bside.mzoffice.user.naver.NaverUserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Slf4j
@Service
public class NaverAuthService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final NaverProperties naverProperties;

    private void validateResponse(ResponseEntity<?> response) {
        if (response == null) {
            throw new AuthLoginException(ResponseCode.API_REQUEST_FAILED);
        }

        // 인증 오류 처리
        if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            throw new AuthLoginException(ResponseCode.UNAUTHORIZED);
        }

        if (response.getStatusCode() == HttpStatus.FORBIDDEN) {
            throw new AuthLoginException(ResponseCode.FORBIDDEN);
        }
    }

    private NaverTokenResponse toRequestAccessToken(String code) {
        ResponseEntity<NaverTokenResponse> response =
                restTemplate.exchange(naverProperties.getRequestURL(code), HttpMethod.GET, null, NaverTokenResponse.class);

        validateResponse(response);

        return response.getBody();
    }

    public AuthUserResponse toRequestProfile(String code) {
        NaverTokenResponse tokenResponse = toRequestAccessToken(code);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(tokenResponse.getAccessToken());
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

        ResponseEntity<NaverUserResponse> response =
                restTemplate.exchange(naverProperties.getProfileRequestURL(), HttpMethod.GET, request, NaverUserResponse.class);

        validateResponse(response);

        AuthUserResponse authUserResponse = response.getBody().getNaverUserDetail();
        authUserResponse.setSnsType(SnsType.NAVER);
        authUserResponse.setAccessToken(tokenResponse.getAccessToken());
        authUserResponse.setRefreshToken(tokenResponse.getRefreshToken());
        authUserResponse.setExpiresIn(tokenResponse.getExpiresIn());

        return authUserResponse;
    }

    public ResponseCode revokeNaverAccount(String accessToken) {
        try {
            ResponseEntity<NaverUnlinkResponse> response = restTemplate.exchange(naverProperties.getUnlinkRequestURL(accessToken), HttpMethod.GET, null, NaverUnlinkResponse.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody().getResult().equals("success")) {
                return ResponseCode.SUCCESS;
            } else {
                log.error("Fail{\n" +
                        "                // 연동 해제 실패 처리 : " +
                        "                " + response.getBody());
                return ResponseCode.FAILED_UNLINK_ACCOUNT;
            }
        } catch (Exception e) {
            // 예외 처리
            e.printStackTrace();
        }

        return ResponseCode.FAILED_UNLINK_ACCOUNT;
    }

    public NaverTokenResponse refreshAccessToken(String refreshToken) {
        ResponseEntity<NaverTokenResponse> response = restTemplate.exchange(naverProperties.getRequestURLByRefreshToken(refreshToken), HttpMethod.GET, null, NaverTokenResponse.class);

        if (response.getBody().getAccessToken() == null) {
            throw new AuthLoginException(ResponseCode.UNAUTHORIZED);
        }
        return response.getBody();
    }
}
