package com.bside.mzoffice.chat.dto.request;


import com.bside.mzoffice.chat.domain.InquiryType;
import com.bside.mzoffice.chat.domain.RequestType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequest {

    @Schema(description = "첫 채팅에는 없고 이후 채팅 ID 필수", requiredMode = Schema.RequiredMode.AUTO)
    private String chatId;

    @Schema(description = "요청 타입", requiredMode = Schema.RequiredMode.REQUIRED)
    private RequestType requestType;

    @Schema(description = "사용자 선택 타입 > requestType = PARSE 인 경우 null", requiredMode = Schema.RequiredMode.AUTO)
    private InquiryType inquiryType;

    @Schema(description = "사용자 선택 타입에 따른 enum 혹은 사용자 입력 Text", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;
}
