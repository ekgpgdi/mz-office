package com.bside.mzoffice.chat.dto.request;


import com.bside.mzoffice.chat.enums.InquiryType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageRequest {

    @Schema(description = "첫 채팅에는 없고 이후 채팅 ID 필수", requiredMode = Schema.RequiredMode.AUTO)
    private String chatId;

    @Schema(description = "메일 다시 문의할때마다 null 로 이후 채팅 세션 ID 필수", requiredMode = Schema.RequiredMode.AUTO)
    private String chatSessionId;

    @Schema(description = "사용자 선택 타입 > requestType = PARSE 인 경우 null", requiredMode = Schema.RequiredMode.AUTO)
    private InquiryType inquiryType;

    @Schema(description = "사용자 선택 타입에 따른 enum 혹은 사용자 입력 Text", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;
}
