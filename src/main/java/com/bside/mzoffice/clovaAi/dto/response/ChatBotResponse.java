package com.bside.mzoffice.clovaAi.dto.response;

import com.bside.mzoffice.clovaAi.dto.request.ClovaMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatBotResponse {

    private Status status;
    private Result result;

    @Getter
    @Setter
    public static class Status {
        private String code;
        private String message;
    }

    @Getter
    @Setter
    public static class Result {
        private ClovaMessage message;
        private int inputLength;
        private int outputLength;
        private String stopReason;
        private String seed;
    }
}