package com.bside.mzoffice.chat.domain;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@Document(collection = "chats")
public class ChatMessage {
    @Id
    private String id;

    private Long customerId;

    private LocalDateTime createdAt;

    private List<Message> chat;
}
