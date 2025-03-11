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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
@Slf4j
public class AiService {
    @Value("${ai.host}")
    private String host;

    @Value("${ai.verification-host}")
    private String verificationHost;

    @Value("${ai.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    private static String generateRequestId() {
        return UUID.randomUUID().toString();
    }

    @Transactional(readOnly = true)
    public ClovaRequestMessage makeAiPrompt(List<Message> messageList) {
        MessageType messageType = null;

        ClovaRequestMessage clovaRequestMessage = new ClovaRequestMessage();
        ClovaMessage systemMessage = null;

        String inputMethodTxt = "";
        String sentenceGenerationType = "";
        String subText = "";
        String aiRequest = "";
        boolean verifyAi = false;

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
                verifyAi = true;
                messageType = handleMessageType(content);
                if (systemMessage == null)
                    systemMessage = ClovaMessage.createDesignPersonaSystemOf(ClovaPrompt.GENERATE.prompt);
            }

            // INPUT_METHOD 처리
            if (messageType != null && inquiryType.equals(InquiryType.INPUT_METHOD)) {
                inputMethodTxt = handleInputMethod(content, messageType);
                sentenceGenerationType = "";
                subText = "";
            }

            // SENTENCE_GENERATION_TYPE 처리
            if (messageType != null && inquiryType.equals(InquiryType.SENTENCE_GENERATION_TYPE)) {
                sentenceGenerationType = handleSentenceGenerationType(content, messageType);
                subText = "나의 상황은 이거야 : ";
            }

            if (inquiryType.equals(InquiryType.AI_REQUEST)) {
                if (messageType == null) aiRequest = "해석 원하는 문구 : " + content;
                else aiRequest = content;
            }
        }

        if (systemMessage == null) systemMessage = ClovaMessage.createDesignPersonaSystemOf(ClovaPrompt.PARSE.prompt);

        clovaRequestMessage.setSystemMessage(systemMessage);
        clovaRequestMessage.setUserMessage(ClovaMessage.creatUserOf(inputMethodTxt + sentenceGenerationType + subText + aiRequest));

        if (messageType != null && verifyAi) {
            String typeText = messageType.equals(MessageType.MAIL) ? "메일" : "문자";
            String systemPrompt = sentenceGenerationType.isEmpty() ?
                    String.format("아래 업무용 답장 %s을 내가 받은 %s에 알맞게 개선해줘\n", typeText, typeText) :
                    String.format("아래 업무용 답장 %s을 나의 상황에 알맞게 개선해줘\n", typeText);

            String userPrompt = sentenceGenerationType.isEmpty() ?
                    String.format("내가 받은 %s은 아래와 같아 \n [내가 받은 %s] : %s \n [작성된 답장 %s] : ", typeText, typeText, aiRequest, typeText) :
                    String.format("나의 상황은 아래와 같아\n %s %s \n [작성한 %s] : ", getSituation(sentenceGenerationType), aiRequest, typeText);

            clovaRequestMessage.setVerificationSystemMessage(ClovaMessage.createDesignPersonaSystemOf(systemPrompt));
            clovaRequestMessage.setVerificationUserMessage(ClovaMessage.creatUserOf(userPrompt));
        }

        return clovaRequestMessage;
    }

    private String getSituation(String sentenceGenerationType) {
        Pattern pattern = Pattern.compile("\\[(.*?)]");
        Matcher matcher = pattern.matcher(sentenceGenerationType);

        return matcher.find() ? matcher.group(1) : "";
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
    private MessageType handleMessageType(String content) {
        switch (content) {
            case "MESSAGE":
                return MessageType.MESSAGE;
            case "MAIL":
                return MessageType.MAIL;
            default:
                return null;
        }
    }

    // INPUT_METHOD 처리 메서드
    private String handleInputMethod(String content, MessageType messageType) {
        ClovaPrompt prompt = switch (content) {
            case "WITH_PREVIOUS" -> (messageType == MessageType.MAIL) ? ClovaPrompt.WITH_PREVIOUS_EMAIL_GENERATE : ClovaPrompt.WITH_PREVIOUS_MESSAGE_GENERATE;
            case "WITHOUT_PREVIOUS" -> (messageType == MessageType.MAIL) ? ClovaPrompt.WITHOUT_PREVIOUS_EMAIL_GENERATE : ClovaPrompt.WITHOUT_PREVIOUS_MESSAGE_GENERATE;
            default -> ClovaPrompt.WITHOUT_PREVIOUS_EMAIL_GENERATE;
        };
        return prompt.prompt;
    }

    // SENTENCE_GENERATION_TYPE 처리 메서드
    private String handleSentenceGenerationType(String content, MessageType messageType) {
        if (messageType == MessageType.MESSAGE) {
            return handleMessageSentenceGeneration(content);
        }

        return handleMailSentenceGeneration(content);
    }

    // MESSAGE 관련 SENTENCE_GENERATION_TYPE 처리 메서드
    private String handleMessageSentenceGeneration(String content) {
        StringBuilder aiPrompt = new StringBuilder();
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
        return aiPrompt.toString();
    }

    // MAIL 관련 SENTENCE_GENERATION_TYPE 처리 메서드
    private String handleMailSentenceGeneration(String content) {
        StringBuilder aiPrompt = new StringBuilder();
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
        return aiPrompt.toString();
    }

    private String makeResponseBody(ClovaMessage systemMessage, ClovaMessage userMessage) {

        ChatBotRequest chatBotRequestDto = ChatBotRequest.builder()
                .messages(new ArrayList<>(List.of(systemMessage, userMessage))) // 메시지 리스트
                .topP(0.8)
                .temperature(0.5)
                .maxTokens(500)
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

    private HttpHeaders makeHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", apiKey);
        headers.set("X-NCP-CLOVASTUDIO-REQUEST-ID", generateRequestId());
        return headers;
    }

    private String sendPostRequest(String url, String requestBody, HttpHeaders headers) {
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<ChatBotResponse> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, ChatBotResponse.class);

        return Optional.of(responseEntity.getBody())
                .map(ChatBotResponse::getResult)
                .map(ChatBotResponse.Result::getMessage)
                .map(ClovaMessage::getContent)
                .map(s -> s.replaceAll("-\\s*\n", "-"))
                .orElse("응답 없음");
    }


    public String sendRequest(ChatSession chatSession) {
        HttpHeaders headers = makeHeaders();

        ClovaRequestMessage clovaRequestMessage = makeAiPrompt(chatSession.getMessages());
        log.info("system prompt : " + clovaRequestMessage.getSystemMessage().getContent());
        log.info("user prompt : " + clovaRequestMessage.getUserMessage().getContent());

        String requestBody = makeResponseBody(clovaRequestMessage.getSystemMessage(), clovaRequestMessage.getUserMessage());
        log.info("AI INPUT : " + requestBody);

        String aiOutput = sendPostRequest(host, requestBody, headers);
        log.info("AI OUTPUT : " + aiOutput);

        if (clovaRequestMessage.getVerificationSystemMessage() == null || clovaRequestMessage.getVerificationSystemMessage().getContent() == null
                || clovaRequestMessage.getVerificationSystemMessage().getContent().length() == 0) {
            return aiOutput;
        }

        clovaRequestMessage.getVerificationUserMessage().addContent(aiOutput);
        String verificationRequestBody = makeResponseBody(clovaRequestMessage.getVerificationSystemMessage(), clovaRequestMessage.getVerificationUserMessage());
        log.info("VERIFICATION AI INPUT : " + verificationRequestBody);

        return sendPostRequest(verificationHost, verificationRequestBody, headers);
    }

}
