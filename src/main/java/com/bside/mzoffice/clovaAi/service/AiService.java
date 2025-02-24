package com.bside.mzoffice.clovaAi.service;

import com.bside.mzoffice.chat.domain.ChatSession;
import com.bside.mzoffice.chat.domain.Message;
import com.bside.mzoffice.chat.enums.InquiryType;
import com.bside.mzoffice.chat.enums.MailSentenceGenerationType;
import com.bside.mzoffice.chat.enums.MessageSentenceGenerationType;
import com.bside.mzoffice.chat.enums.MessageType;
import com.bside.mzoffice.clovaAi.domain.ClovaPrompt;
import com.bside.mzoffice.clovaAi.dto.response.ChatBotResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

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
    public String makeAiPrompt(List<Message> messageList) {
        StringBuilder aiPrompt = new StringBuilder();
        MessageType messageType = null;

        // 메시지 타입에 따른 처리 분기
        for (Message message : messageList) {
            if (message.getContent() == null) continue;

            InquiryType inquiryType = message.getInquiryType();
            String content = message.getContent();

            // REQUEST_TYPE 처리
            if (inquiryType.equals(InquiryType.REQUEST_TYPE)) {
                handleRequestType(content, aiPrompt);
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
        }

        return aiPrompt.toString();
    }

    // REQUEST_TYPE 처리 메서드
    private void handleRequestType(String content, StringBuilder aiPrompt) {
        switch (content) {
            case "PARSE":
                aiPrompt.append(ClovaPrompt.PARSE.prompt);
                break;
            case "GENERATE":
                aiPrompt.append(ClovaPrompt.GENERATE.prompt);
                break;
            default:
                // 추가 처리 로직
                break;
        }
    }

    // MESSAGE_TYPE 처리 메서드
    private MessageType handleMessageType(String content, StringBuilder aiPrompt) {
        switch (content) {
            case "MESSAGE":
                aiPrompt.append(ClovaPrompt.MESSAGE_GENERATE);
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
                aiPrompt.append(ClovaPrompt.WITH_PREVIOUS_EMAIL_MAIL_GENERATE);
                break;
            case "WITHOUT_PREVIOUS_EMAIL":
                aiPrompt.append(ClovaPrompt.WITHOUT_PREVIOUS_EMAIL_MAIL_GENERATE);
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
                aiPrompt.append(ClovaPrompt.MESSAGE_CONGRATULATION_TEXT);
                aiPrompt.append(MessageSentenceGenerationType.CONGRATULATION);
                break;
            case "INQUIRY":
                aiPrompt.append(ClovaPrompt.MESSAGE_INQUIRY_TEXT);
                aiPrompt.append(MessageSentenceGenerationType.INQUIRY);
                break;
            case "APPRECIATION":
                aiPrompt.append(ClovaPrompt.MESSAGE_APPRECIATION_TEXT);
                aiPrompt.append(MessageSentenceGenerationType.APPRECIATION);
                break;
            case "APOLOGY":
                aiPrompt.append(ClovaPrompt.MESSAGE_APOLOGY_TEXT);
                aiPrompt.append(MessageSentenceGenerationType.APOLOGY);
                break;
            case "SCHEDULE_CONFIRMATION":
                aiPrompt.append(ClovaPrompt.MESSAGE_SCHEDULE_CONFIRMATION_TEXT);
                aiPrompt.append(MessageSentenceGenerationType.SCHEDULE_CONFIRMATION);
                break;
            case "ANNOUNCEMENT":
                aiPrompt.append(ClovaPrompt.MESSAGE_ANNOUNCEMENT_TEXT);
                aiPrompt.append(MessageSentenceGenerationType.ANNOUNCEMENT);
                break;
            case "WORK_REQUEST":
                aiPrompt.append(ClovaPrompt.MESSAGE_WORK_REQUEST_TEXT);
                aiPrompt.append(MessageSentenceGenerationType.WORK_REQUEST);
                break;
            case "FOLLOW_UP":
                aiPrompt.append(ClovaPrompt.MESSAGE_FOLLOW_UP_TEXT);
                aiPrompt.append(MessageSentenceGenerationType.FOLLOW_UP);
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
                aiPrompt.append(ClovaPrompt.MAIL_FEEDBACK_REQUEST_TEXT);
                aiPrompt.append(MailSentenceGenerationType.FEEDBACK_REQUEST);
                break;
            case "REMINDER":
                aiPrompt.append(ClovaPrompt.MAIL_REMINDER_TEXT);
                aiPrompt.append(MailSentenceGenerationType.REMINDER);
                break;
            case "THANK_YOU":
                aiPrompt.append(ClovaPrompt.MAIL_THANK_YOU_TEXT);
                aiPrompt.append(MailSentenceGenerationType.THANK_YOU);
                break;
            case "APOLOGY":
                aiPrompt.append(ClovaPrompt.MAIL_APOLOGY_TEXT);
                aiPrompt.append(MailSentenceGenerationType.APOLOGY);
                break;
            case "GREETING":
                aiPrompt.append(ClovaPrompt.MAIL_GREETING_TEXT);
                aiPrompt.append(MailSentenceGenerationType.GREETING);
                break;
            case "SUGGESTION":
                aiPrompt.append(ClovaPrompt.MAIL_SUGGESTION_TEXT);
                aiPrompt.append(MailSentenceGenerationType.SUGGESTION);
                break;
            case "FOLLOW_UP":
                aiPrompt.append(ClovaPrompt.MAIL_FOLLOW_UP_TEXT);
                aiPrompt.append(MailSentenceGenerationType.FOLLOW_UP);
                break;
            default:
                // 추가 처리 로직
                break;
        }
    }

    private String makeResponseBody(ChatSession chatSession) {
        String prompt = makeAiPrompt(chatSession.getMessages());
        System.out.println("prompt : " + prompt);
        return "";
//        ClovaMessage systemMessage = ClovaMessage.createDesignPersonaSystemOf();
//        ClovaMessage userMessage = ClovaMessage.creatUserOf(completionRequest);
//
//        ChatBotRequest chatBotRequestDto = ChatBotRequest.builder()
//                .messages(new ArrayList<>(List.of(systemMessage, userMessage))) // 메시지 리스트
//                .topP(0.8)
//                .temperature(0.5)
//                .maxTokens(100)
//                .repeatPenalty(5.0)
//                .build();
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        String requestBody = "";
//        try {
//            requestBody = objectMapper.writeValueAsString(chatBotRequestDto);
//        } catch (Exception e) {
//            log.error("Error serializing the request body: " + e.getMessage());
//            throw new ClovaAiException(ResponseCode.AI_REQUEST_JSON_SERIALIZATION_ERROR); // 변경된 이넘
//        }
//        return requestBody;
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

        return responseEntity.getBody().getResult().getMessage().getContent();
    }
}
