package com.bside.mzoffice.chat.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class Message {
    private MessageSenderType sender;
    private String content;
    private LocalDateTime sentAt;
}
