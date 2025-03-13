package com.bside.mzoffice.application.controller;

import com.bside.mzoffice.common.enums.ResponseCode;
import com.bside.mzoffice.common.response.ServerResponse;
import com.bside.mzoffice.user.enums.SnsType;
import com.bside.mzoffice.user.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    /**
     * 네이버 로그인 / 회원가입
     * @param code 사용자 액세스 토큰
     * @return ServerResponse - content : jwt
     */
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
    @GetMapping("/login/naver-callback")
    public ServerResponse<String> loginByNaver(@RequestParam(name = "code") String code) {
        return ServerResponse.successResponse(authService.loginByOAuth(code, SnsType.NAVER));
    }

    /**
     * 네이버 계정 연동 해제 및 탈퇴
     * @return ServerResponse - responseCode
     */
    @Operation(
            summary = "네이버 계정 연동 해제 및 탈퇴",
            description = "사용자 네이버 계정과의 OAuth 연동을 해제하고 회원 정보를 삭제합니다."
    )
    @ApiResponse(responseCode = "200", description = "성공적으로 연동 해제됨",
            content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "401", description = "연동 해제 실패")
    @PostMapping("/unlink-naver")
    public ServerResponse<ResponseCode> unlinkNaverAccount(Authentication authentication) {
        return authService.revokeAccount(authentication, SnsType.NAVER);
    }
}
