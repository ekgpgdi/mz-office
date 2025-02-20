package com.bside.mzoffice.user.naver;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NaverUnlinkResponse {
    @JsonProperty("result")
    private String result;

    @JsonProperty("access_token")
    private String accessToken;
}
