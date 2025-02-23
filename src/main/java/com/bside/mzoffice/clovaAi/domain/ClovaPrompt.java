package com.bside.mzoffice.clovaAi.domain;

import lombok.Getter;

@Getter
public enum ClovaPrompt {
    MAIL_GENERATE("너는 업무용 메일과 문자에 맞는 스타일로 답변을 작성하는 AI야. \n" +
            "사용자가 받은 메일을 보고 사용자가 보낼 답변을 작성해줘\n");

    public final String prompt;

    ClovaPrompt(String prompt) {
        this.prompt = prompt;
    }
}
