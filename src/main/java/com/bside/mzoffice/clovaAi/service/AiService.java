package com.bside.mzoffice.clovaAi.service;

import com.bside.mzoffice.chat.domain.Message;
import com.bside.mzoffice.clovaAi.dto.request.ChatBotRequest;
import com.bside.mzoffice.clovaAi.dto.request.ClovaMessage;
import com.bside.mzoffice.clovaAi.dto.response.ChatBotResponse;
import com.bside.mzoffice.common.domain.ResponseCode;
import com.bside.mzoffice.common.exception.customException.ClovaAiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class AiService {
    @Value("${ai.host}")
    private String host;

    @Value("${ai.key}")
    private String apiKey;

    private static String generateRequestId() {
        return UUID.randomUUID().toString();
    }

    private String makeResponseBody(String completionRequest) {
        ClovaMessage systemMessage = ClovaMessage.createDesignPersonaSystemOf();
        ClovaMessage userMessage = ClovaMessage.creatUserOf(completionRequest);

        ChatBotRequest chatBotRequestDto = ChatBotRequest.builder()
                .messages(new ArrayList<>(List.of(systemMessage, userMessage))) // 메시지 리스트
                .topP(0.8)
                .temperature(0.5)
                .maxTokens(100)
                .repeatPenalty(5.0)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = "";
        try {
            requestBody = objectMapper.writeValueAsString(chatBotRequestDto);
        } catch (Exception e) {
            log.error("Error serializing the request body: " + e.getMessage());
            throw new ClovaAiException(ResponseCode.AI_REQUEST_JSON_SERIALIZATION_ERROR); // 변경된 이넘
        }
        return requestBody;
    }

    public String sendRequest(String completionRequest) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", apiKey);
        headers.set("X-NCP-CLOVASTUDIO-REQUEST-ID", generateRequestId());

        String requestBody = makeResponseBody(completionRequest);

        log.info("AI INPUT : " + requestBody);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        String url = UriComponentsBuilder.fromUriString(host).toUriString();
        ResponseEntity<ChatBotResponse> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, ChatBotResponse.class);

        return responseEntity.getBody().getResult().getMessage().getContent();
    }
}
