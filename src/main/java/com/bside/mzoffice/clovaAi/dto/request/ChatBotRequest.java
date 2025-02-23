package com.bside.mzoffice.clovaAi.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;

@Builder
@Getter
public class ChatBotRequest {
    private ArrayList<ClovaMessage> messages;
    private double topP;
    private double temperature;
    private int maxTokens;
    private double repeatPenalty;
}
