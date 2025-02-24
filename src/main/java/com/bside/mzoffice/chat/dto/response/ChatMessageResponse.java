package com.bside.mzoffice.chat.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageResponse {

    @Schema(description = "채팅 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String chatId;

    @Schema(description = "메일 다시 문의할때마다 null 로 이후 채팅 세션 ID 필수", requiredMode = Schema.RequiredMode.AUTO)
    private String chatSessionId;

    @Schema(description = "응답 콘텐츠 (AI PROMPT 요청 일 때만) ", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;
}
