package com.bside.mzoffice.chat.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ChatSessionResponse {

    @Schema(description = "채팅창 내 세션 ID")
    private String chatSessionId;

    @Schema(description = "채팅창 내 세션 생성 시각")
    private LocalDateTime createdAt;

    @Schema(description = "세션 내 주고 받은 메세지들")
    private List<MessageResponse> messages;
}
