package com.bside.mzoffice.clovaAi.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

@JsonSerialize
@Getter
@Setter
public class ClovaRequestMessage {
    private ClovaMessage systemMessage;
    private ClovaMessage userMessage;
}
