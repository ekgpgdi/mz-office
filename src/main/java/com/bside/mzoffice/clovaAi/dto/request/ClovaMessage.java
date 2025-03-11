package com.bside.mzoffice.clovaAi.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Getter;

@JsonSerialize
@Getter
@Builder
public class ClovaMessage {
    private ROLE role;
    private String content;

    public enum ROLE {
        system, user, assistant
    }

    public static ClovaMessage creatUserOf(String content) {
        return ClovaMessage.builder()
                .role(ROLE.user)
                .content(content)
                .build();
    }

    public static ClovaMessage createDesignPersonaSystemOf(String content) {
        return ClovaMessage.builder()
                .role(ClovaMessage.ROLE.system)
                .content(content)
                .build();
    }

    public void addContent(String text) {
        this.content = this.content + text;
    }
}
