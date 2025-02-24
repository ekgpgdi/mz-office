package com.bside.mzoffice.chat.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class ChatSession {
    private String chatSessionId;
    private LocalDateTime createdAt; // 채팅 시작 시간
    private List<Message> messages; // 이 채팅에서 오간 메시지들
}

