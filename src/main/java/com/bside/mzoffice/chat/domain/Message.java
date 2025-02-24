package com.bside.mzoffice.chat.domain;

import com.bside.mzoffice.chat.enums.InquiryType;
import com.bside.mzoffice.chat.enums.MessageSenderType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class Message {
    private MessageSenderType sender;
    private InquiryType inquiryType;
    private String content;
    private LocalDateTime sentAt;
}
