package com.bside.mzoffice.user.naver;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.UriComponentsBuilder;

@Data
@Configuration
@ConfigurationProperties(prefix = "naver")
public class NaverProperties {
    private String requestTokenUri;
    private String userInfoUri;
    private String clientId;
    private String clientSecret;

    public String getRequestURL(String code) {
        return UriComponentsBuilder.fromUriString(requestTokenUri)
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("code", code)
                .toUriString();
    }

    public String getRequestURLByRefreshToken(String refreshToken) {
        return UriComponentsBuilder.fromUriString(requestTokenUri)
                .queryParam("grant_type", "refresh_token")
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("refresh_token", refreshToken)
                .toUriString();
    }

    public String getProfileRequestURL() {
        return UriComponentsBuilder.fromUriString(userInfoUri)
                .toUriString();
    }

    public String getUnlinkRequestURL(String accessToken) {
        return UriComponentsBuilder.fromUriString(requestTokenUri)
                .queryParam("grant_type", "delete")
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("access_token", accessToken)
                .toUriString();
    }
}
