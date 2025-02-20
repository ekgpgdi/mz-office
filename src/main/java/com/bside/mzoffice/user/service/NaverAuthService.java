package com.bside.mzoffice.user.service;

import com.bside.mzoffice.common.domain.ResponseCode;
import com.bside.mzoffice.common.exception.customException.AuthLoginException;
import com.bside.mzoffice.user.domain.SnsType;
import com.bside.mzoffice.user.dto.response.AuthUserResponse;
import com.bside.mzoffice.user.naver.NaverProperties;
import com.bside.mzoffice.user.naver.NaverTokenResponse;
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

    private String toRequestAccessToken(String code) {
        ResponseEntity<NaverTokenResponse> response =
                restTemplate.exchange(naverProperties.getRequestURL(code), HttpMethod.GET, null, NaverTokenResponse.class);

        validateResponse(response);

        return response.getBody().getAccessToken();
    }

    public AuthUserResponse toRequestProfile(String code) {
        String accessToken = toRequestAccessToken(code);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

        ResponseEntity<NaverUserResponse> response =
                restTemplate.exchange("https://openapi.naver.com/v1/nid/me", HttpMethod.GET, request, NaverUserResponse.class);

        validateResponse(response);

        AuthUserResponse authUserResponse = response.getBody().getNaverUserDetail();
        authUserResponse.setSnsType(SnsType.NAVER);

        return authUserResponse;
    }
}
