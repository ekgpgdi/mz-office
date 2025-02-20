package com.bside.mzoffice.user.controller;

import com.bside.mzoffice.common.response.ServerResponse;
import com.bside.mzoffice.user.domain.SnsType;
import com.bside.mzoffice.user.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(
            summary = "네이버 OAuth 로그인",
            description = "사용자가 네이버 OAuth 인증을 통해 로그인합니다. 성공 시 JWT 토큰을 반환합니다. " +
                    "만약 로그인 정보가 없으면, 자동으로 회원가입을 진행한 후 JWT 토큰을 반환합니다. " +
                    "'code' 파라미터는 네이버 OAuth 인증 후 받은 인증 코드입니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "성공적으로 로그인되거나 회원가입이 완료되어 JWT 토큰을 반환",
            content = @Content(schema = @Schema(implementation = String.class))
    )
    @ApiResponse(
            responseCode = "401",
            description = "인증 실패: 잘못된 코드나 인증 과정에 문제가 있을 경우"
    )
    @GetMapping("/naver-callback")
    public ServerResponse<String> loginByNaver(@RequestParam(name = "code") String code) {
        return ServerResponse.successResponse(authService.loginByOAuth(code, SnsType.NAVER));
    }
}
