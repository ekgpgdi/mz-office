package com.bside.mzoffice.chat.dto.response;

import com.bside.mzoffice.chat.enums.InquiryType;
import com.bside.mzoffice.chat.enums.MessageSenderType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MessageResponse {

    @Schema(description = "메세지 발송자")
    private MessageSenderType sender;

    @Schema(description = "메세지 타입")
    private InquiryType inquiryType;

    @Schema(description = "메세지 타입 별 값들 enum / ai 요청 문구")
    private String content;

    @Schema(description = "발송 시각")
    private LocalDateTime sentAt;
}
