package com.bside.mzoffice.application.controller;

import com.bside.mzoffice.chat.dto.response.ChatMessageDetailResponse;
import com.bside.mzoffice.chat.dto.response.ChatMessageSummaryResponse;
import com.bside.mzoffice.chat.service.ChatReadService;
import com.bside.mzoffice.chat.service.ChatWriteService;
import com.bside.mzoffice.common.domain.ResponseCode;
import com.bside.mzoffice.common.response.ServerResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatReadService chatReadService;
    private final ChatWriteService chatWriteService;

    @Operation(
            summary = "최근 채팅 내역 조회",
            description = "사용자의 최근 채팅 내역 최대 3개를 조회합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "0개 ~ 3개의 채팅 내역 응답",
            content = @Content(schema = @Schema(implementation = ChatMessageSummaryResponse.class))
    )
    @GetMapping("/recent")
    public ServerResponse<List<ChatMessageSummaryResponse>> getRecentChats(Authentication authentication) {
        return ServerResponse.successResponse(chatReadService.getRecentChats(authentication));
    }

    @Operation(
            summary = "채팅방 대화 조회",
            description = "채팅방 내에서 이루어진 전체 대화 목록을 조회합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "채팅방 내 상세 대화 목록 응답",
            content = @Content(schema = @Schema(implementation = ChatMessageDetailResponse.class))
    )
    @ApiResponse(
            responseCode = "403",
            description = "사용자의 ID와 채팅방 소유자의 ID가 일치하지 않을 경우 접근 불가",
            content = @Content(schema = @Schema(implementation = String.class))
    )
    @GetMapping("/{chatId}")
    public ServerResponse<ChatMessageDetailResponse> getChatById(Authentication authentication, @PathVariable String chatId) throws AccessDeniedException {
        return ServerResponse.successResponse(chatReadService.getChatById(authentication, chatId));
    }

    @Operation(
            summary = "오늘 진행된 채팅 내역 조회",
            description = "오늘 날짜에 진행된 채팅이 있는지 확인하고, 있다면 해당 채팅 내역을 반환합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "오늘 진행된 채팅 내역 응답, 없다면 chatId null",
            content = @Content(schema = @Schema(implementation = ChatMessageDetailResponse.class))
    )
    @GetMapping("/active")
    public ServerResponse<ChatMessageDetailResponse> getActiveChat(Authentication authentication) {
        return ServerResponse.successResponse(chatReadService.getActiveChat(authentication));
    }

    @Operation(
            summary = "채팅방 대화 삭제",
            description = "채팅방 내에서 이루어진 전체 대화 목록을 삭제합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "채팅방 내 전체 대화 삭제 및 채팅방 삭제",
            content = @Content(schema = @Schema(implementation = ChatMessageDetailResponse.class))
    )
    @ApiResponse(
            responseCode = "403",
            description = "사용자의 ID와 채팅방 소유자의 ID가 일치하지 않을 경우 접근 불가",
            content = @Content(schema = @Schema(implementation = String.class))
    )
    @DeleteMapping("/{chatId}")
    public ServerResponse<ResponseCode> deleteChatById(Authentication authentication, @PathVariable String chatId) throws AccessDeniedException {
        return ServerResponse.successResponse(chatWriteService.deleteChatById(authentication, chatId));
    }
}
