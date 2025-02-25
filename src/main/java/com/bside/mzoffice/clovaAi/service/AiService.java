package com.bside.mzoffice.clovaAi.service;

import com.bside.mzoffice.chat.domain.ChatSession;
import com.bside.mzoffice.chat.domain.Message;
import com.bside.mzoffice.chat.enums.InquiryType;
import com.bside.mzoffice.chat.enums.MailSentenceGenerationType;
import com.bside.mzoffice.chat.enums.MessageSentenceGenerationType;
import com.bside.mzoffice.chat.enums.MessageType;
import com.bside.mzoffice.clovaAi.domain.ClovaPrompt;
import com.bside.mzoffice.clovaAi.dto.request.ChatBotRequest;
import com.bside.mzoffice.clovaAi.dto.request.ClovaMessage;
import com.bside.mzoffice.clovaAi.dto.request.ClovaRequestMessage;
import com.bside.mzoffice.clovaAi.dto.response.ChatBotResponse;
import com.bside.mzoffice.common.domain.ResponseCode;
import com.bside.mzoffice.common.exception.customException.ClovaAiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional(readOnly = true)
    public ClovaRequestMessage makeAiPrompt(List<Message> messageList) {
        StringBuilder aiPrompt = new StringBuilder();
        MessageType messageType = null;

        ClovaRequestMessage clovaRequestMessage = new ClovaRequestMessage();
        ClovaMessage systemMessage = null;

        // 메시지 타입에 따른 처리 분기
        for (Message message : messageList) {
            if (message.getContent() == null) continue;

            InquiryType inquiryType = message.getInquiryType();
            String content = message.getContent();

            // REQUEST_TYPE 처리
            if (inquiryType.equals(InquiryType.REQUEST_TYPE)) {
                systemMessage = handleRequestType(content);
            }

            // MESSAGE_TYPE 처리
            if (inquiryType.equals(InquiryType.MESSAGE_TYPE)) {
                messageType = handleMessageType(content, aiPrompt);
            }

            // INPUT_METHOD 처리
            if (inquiryType.equals(InquiryType.INPUT_METHOD)) {
                handleInputMethod(content, aiPrompt);
            }

            // SENTENCE_GENERATION_TYPE 처리
            if (messageType != null && inquiryType.equals(InquiryType.SENTENCE_GENERATION_TYPE)) {
                handleSentenceGenerationType(content, aiPrompt, messageType);
            }

            if(inquiryType.equals(InquiryType.AI_REQUEST)) {
                aiPrompt.append(content);
            }
        }

        clovaRequestMessage.setSystemMessage(systemMessage);
        clovaRequestMessage.setUserMessage(ClovaMessage.creatUserOf(aiPrompt.toString()));
        return clovaRequestMessage;
    }

    // REQUEST_TYPE 처리 메서드
    private ClovaMessage handleRequestType(String content) {
        switch (content) {
            case "PARSE":
                return ClovaMessage.createDesignPersonaSystemOf(ClovaPrompt.PARSE.prompt);
            case "GENERATE":
                return ClovaMessage.createDesignPersonaSystemOf(ClovaPrompt.GENERATE.prompt);
            default:
                // 추가 처리 로직
                break;
        }

        return ClovaMessage.createDesignPersonaSystemOf(ClovaPrompt.GENERATE.prompt);
    }

    // MESSAGE_TYPE 처리 메서드
    private MessageType handleMessageType(String content, StringBuilder aiPrompt) {
        switch (content) {
            case "MESSAGE":
                aiPrompt.append(ClovaPrompt.MESSAGE_GENERATE.prompt);
                return MessageType.MESSAGE;
            case "MAIL":
                return MessageType.MAIL;
            default:
                return null;
        }
    }

    // INPUT_METHOD 처리 메서드
    private void handleInputMethod(String content, StringBuilder aiPrompt) {
        switch (content) {
            case "WITH_PREVIOUS_EMAIL":
                aiPrompt.append(ClovaPrompt.WITH_PREVIOUS_EMAIL_MAIL_GENERATE.prompt);
                break;
            case "WITHOUT_PREVIOUS_EMAIL":
                aiPrompt.append(ClovaPrompt.WITHOUT_PREVIOUS_EMAIL_MAIL_GENERATE.prompt);
                break;
            default:
                // 추가 처리 로직
                break;
        }
    }

    // SENTENCE_GENERATION_TYPE 처리 메서드
    private void handleSentenceGenerationType(String content, StringBuilder aiPrompt, MessageType messageType) {
        if (messageType == MessageType.MESSAGE) {
            handleMessageSentenceGeneration(content, aiPrompt);
        } else if (messageType == MessageType.MAIL) {
            handleMailSentenceGeneration(content, aiPrompt);
        }
    }

    // MESSAGE 관련 SENTENCE_GENERATION_TYPE 처리 메서드
    private void handleMessageSentenceGeneration(String content, StringBuilder aiPrompt) {
        switch (content) {
            case "CONGRATULATION":
                aiPrompt.append(ClovaPrompt.MESSAGE_CONGRATULATION_TEXT.prompt);
                aiPrompt.append(MessageSentenceGenerationType.CONGRATULATION.example);
                break;
            case "INQUIRY":
                aiPrompt.append(ClovaPrompt.MESSAGE_INQUIRY_TEXT.prompt);
                aiPrompt.append(MessageSentenceGenerationType.INQUIRY.example);
                break;
            case "APPRECIATION":
                aiPrompt.append(ClovaPrompt.MESSAGE_APPRECIATION_TEXT.prompt);
                aiPrompt.append(MessageSentenceGenerationType.APPRECIATION.example);
                break;
            case "APOLOGY":
                aiPrompt.append(ClovaPrompt.MESSAGE_APOLOGY_TEXT.prompt);
                aiPrompt.append(MessageSentenceGenerationType.APOLOGY.example);
                break;
            case "SCHEDULE_CONFIRMATION":
                aiPrompt.append(ClovaPrompt.MESSAGE_SCHEDULE_CONFIRMATION_TEXT.prompt);
                aiPrompt.append(MessageSentenceGenerationType.SCHEDULE_CONFIRMATION.example);
                break;
            case "ANNOUNCEMENT":
                aiPrompt.append(ClovaPrompt.MESSAGE_ANNOUNCEMENT_TEXT.prompt);
                aiPrompt.append(MessageSentenceGenerationType.ANNOUNCEMENT.example);
                break;
            case "WORK_REQUEST":
                aiPrompt.append(ClovaPrompt.MESSAGE_WORK_REQUEST_TEXT.prompt);
                aiPrompt.append(MessageSentenceGenerationType.WORK_REQUEST.example);
                break;
            case "FOLLOW_UP":
                aiPrompt.append(ClovaPrompt.MESSAGE_FOLLOW_UP_TEXT.prompt);
                aiPrompt.append(MessageSentenceGenerationType.FOLLOW_UP.example);
                break;
            default:
                // 추가 처리 로직
                break;
        }
    }

    // MAIL 관련 SENTENCE_GENERATION_TYPE 처리 메서드
    private void handleMailSentenceGeneration(String content, StringBuilder aiPrompt) {
        switch (content) {
            case "FEEDBACK_REQUEST":
                aiPrompt.append(ClovaPrompt.MAIL_FEEDBACK_REQUEST_TEXT.prompt);
                aiPrompt.append(MailSentenceGenerationType.FEEDBACK_REQUEST.example);
                break;
            case "REMINDER":
                aiPrompt.append(ClovaPrompt.MAIL_REMINDER_TEXT.prompt);
                aiPrompt.append(MailSentenceGenerationType.REMINDER.example);
                break;
            case "THANK_YOU":
                aiPrompt.append(ClovaPrompt.MAIL_THANK_YOU_TEXT.prompt);
                aiPrompt.append(MailSentenceGenerationType.THANK_YOU.example);
                break;
            case "APOLOGY":
                aiPrompt.append(ClovaPrompt.MAIL_APOLOGY_TEXT.prompt);
                aiPrompt.append(MailSentenceGenerationType.APOLOGY.example);
                break;
            case "GREETING":
                aiPrompt.append(ClovaPrompt.MAIL_GREETING_TEXT.prompt);
                aiPrompt.append(MailSentenceGenerationType.GREETING.example);
                break;
            case "SUGGESTION":
                aiPrompt.append(ClovaPrompt.MAIL_SUGGESTION_TEXT.prompt);
                aiPrompt.append(MailSentenceGenerationType.SUGGESTION.example);
                break;
            case "FOLLOW_UP":
                aiPrompt.append(ClovaPrompt.MAIL_FOLLOW_UP_TEXT.prompt);
                aiPrompt.append(MailSentenceGenerationType.FOLLOW_UP.example);
                break;
            default:
                // 추가 처리 로직
                break;
        }
    }

    private String makeResponseBody(ChatSession chatSession) {
        ClovaRequestMessage clovaRequestMessage = makeAiPrompt(chatSession.getMessages());
        System.out.println("system prompt : " + clovaRequestMessage.getSystemMessage().getContent());
        System.out.println("user prompt : " + clovaRequestMessage.getUserMessage().getContent());

        ChatBotRequest chatBotRequestDto = ChatBotRequest.builder()
                .messages(new ArrayList<>(List.of(clovaRequestMessage.getSystemMessage(), clovaRequestMessage.getUserMessage()))) // 메시지 리스트
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

    public String sendRequest(ChatSession chatSession) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", apiKey);
        headers.set("X-NCP-CLOVASTUDIO-REQUEST-ID", generateRequestId());

        String requestBody = makeResponseBody(chatSession);

        log.info("AI INPUT : " + requestBody);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        String url = UriComponentsBuilder.fromUriString(host).toUriString();
        ResponseEntity<ChatBotResponse> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, ChatBotResponse.class);

        log.info("getStatusCode : " + responseEntity.getStatusCode());

        return responseEntity.getBody().getResult().getMessage().getContent();
    }
}
