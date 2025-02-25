package com.bside.mzoffice.chat.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ChatMessageSummaryResponse {
    @Schema(description = "채팅 ID")
    private String chatId;

    @Schema(description = "채팅 날짜")
    private LocalDate date;
}
