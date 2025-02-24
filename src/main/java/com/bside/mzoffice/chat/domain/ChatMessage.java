package com.bside.mzoffice.chat.domain;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@Document(collection = "chats")
public class ChatMessage {
    @Id
    private String id;
    private Long customerId; // 고객 ID
    private LocalDate date; // 채팅 날짜 (LocalDate로 변경)

    private List<ChatSession> chatSessions; // 같은 날 진행된 여러 채팅 세션
}
