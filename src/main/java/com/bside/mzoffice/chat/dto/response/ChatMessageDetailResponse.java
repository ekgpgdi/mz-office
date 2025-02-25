package com.bside.mzoffice.chat.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ChatMessageDetailResponse extends ChatMessageSummaryResponse {

    @Schema(description = "채팅창 내 세션 리스트")
    private List<ChatSessionResponse> chatSessionList = new ArrayList<>();

    public ChatMessageDetailResponse(String chatId, LocalDate date, List<ChatSessionResponse> chatSessionList) {
        super(chatId, date);
        this.chatSessionList = chatSessionList;
    }
}
