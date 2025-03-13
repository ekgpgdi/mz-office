package com.bside.mzoffice.clovaAi.enums;

import com.bside.mzoffice.chat.enums.InputMethodType;
import com.bside.mzoffice.chat.enums.MessageType;

public enum InputTypeClovaPrompt {
    WITH_PREVIOUS_EMAIL_GENERATE("사용자가 받은 메일을 보고 사용자가 보낼 답장 메일을 작성해줘\n [사용자가 받은 메일] : \n"),
    WITHOUT_PREVIOUS_EMAIL_GENERATE("사용자의 상황을 보고 사용자가 보낼 메일을 작성해줘\n"),

    WITH_PREVIOUS_MESSAGE_GENERATE("사용자가 받은 문자를 보고 사용자가 보낼 답장 문자를 작성해줘\n [사용자가 받은 문자] : "),
    WITHOUT_PREVIOUS_MESSAGE_GENERATE("사용자의 상황을 보고 사용자가 답장 문자를 작성해줘\n");

    public final String prompt;

    InputTypeClovaPrompt(String prompt) {
        this.prompt = prompt;
    }

    public static String handleInputType(String content, MessageType messageType) {
        InputMethodType inputMethodType = InputMethodType.valueOf(content);

        return switch (inputMethodType) {
            case WITH_PREVIOUS -> (messageType == MessageType.MAIL)
                    ? InputTypeClovaPrompt.WITH_PREVIOUS_EMAIL_GENERATE.prompt
                    : InputTypeClovaPrompt.WITH_PREVIOUS_MESSAGE_GENERATE.prompt;
            case WITHOUT_PREVIOUS -> (messageType == MessageType.MAIL)
                    ? InputTypeClovaPrompt.WITHOUT_PREVIOUS_EMAIL_GENERATE.prompt
                    : InputTypeClovaPrompt.WITHOUT_PREVIOUS_MESSAGE_GENERATE.prompt;
        };
    }
}
