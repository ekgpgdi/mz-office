package com.bside.mzoffice.chat.controller;

import com.bside.mzoffice.chat.dto.response.ChatMessageDetailResponse;
import com.bside.mzoffice.chat.dto.response.ChatMessageSummaryResponse;
import com.bside.mzoffice.chat.service.ChatService;
import com.bside.mzoffice.common.response.ServerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @GetMapping("/recent")
    public ServerResponse<List<ChatMessageSummaryResponse>> getRecentChats(Authentication authentication) {
        return ServerResponse.successResponse(chatService.getRecentChats(authentication));
    }

    @GetMapping("/{chatId}")
    public ServerResponse<ChatMessageDetailResponse> getChatById(@PathVariable String chatId) {
        return ServerResponse.successResponse(chatService.getChatById(chatId));
    }
}
