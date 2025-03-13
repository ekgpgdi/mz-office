package com.bside.mzoffice.clovaAi.service;

import com.bside.mzoffice.chat.domain.ChatSession;
import com.bside.mzoffice.chat.domain.Message;
import com.bside.mzoffice.chat.enums.*;
import com.bside.mzoffice.clovaAi.enums.ClovaPrompt;
import com.bside.mzoffice.clovaAi.dto.request.ChatBotRequest;
import com.bside.mzoffice.clovaAi.dto.request.ClovaMessage;
import com.bside.mzoffice.clovaAi.dto.request.ClovaRequestMessage;
import com.bside.mzoffice.clovaAi.dto.response.ChatBotResponse;
import com.bside.mzoffice.clovaAi.enums.InputTypeClovaPrompt;
import com.bside.mzoffice.clovaAi.enums.RequestTypeClovaPrompt;
import com.bside.mzoffice.clovaAi.enums.SentenceGenerationTypeClovaPrompt;
import com.bside.mzoffice.common.enums.ResponseCode;
import com.bside.mzoffice.common.exception.customException.ClovaAiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

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
                messageType = MessageType.valueOf(content);
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

        if (messageType != null) {
            String typeText = messageType.equals(MessageType.MAIL) ? "메일" : "문자";
            String systemPrompt = sentenceGenerationType.isEmpty() ?
                    String.format("작성된 %s이 내가 받은 %s에 맞게 불필요한 내용이 있다면 없애주고 정중하게 표현을 바꿀 수 있는 부분을 개선해줘\n ", typeText, typeText) :
                    String.format("작성된 %s이 나의 상황에 알맞게 불필요한 내용이 있다면 없애주고 정중하게 표현을 바꿀 수 있는 부분을 개선해줘\n ", typeText);

            String userPrompt = sentenceGenerationType.isEmpty() ?
                    String.format("내가 받은 %s은 아래와 같아 \n [내가 받은 %s] : %s \n [작성된 답장 %s] : ", typeText, typeText, aiRequest, typeText) :
                    String.format("나의 상황은 아래와 같아\n %s %s \n [작성한 %s] : ", getSituation(sentenceGenerationType), aiRequest, typeText);

            String aiOutFormat = messageType.equals(MessageType.MAIL) ? "응답은 아래 형태로 줘 \n 제목 : [제목란]\n\n [메일 본문란] \n "
                    : "응답은 아래 형태로 줘 \n [문자 본문란] \n";

            clovaRequestMessage.setVerificationSystemMessage(ClovaMessage.createDesignPersonaSystemOf(systemPrompt + aiOutFormat));
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
        return ClovaMessage.createDesignPersonaSystemOf(RequestTypeClovaPrompt.handleRequestType(content));
    }

    // INPUT_METHOD 처리 메서드
    private String handleInputMethod(String content, MessageType messageType) {
        return InputTypeClovaPrompt.handleInputType(content, messageType);
    }

    // SENTENCE_GENERATION_TYPE 처리 메서드
    private String handleSentenceGenerationType(String content, MessageType messageType) {
        if (messageType == MessageType.MESSAGE) {
            return SentenceGenerationTypeClovaPrompt.handleSentenceGenerationType(MessageSentenceGenerationType.valueOf(content));
        }
        return SentenceGenerationTypeClovaPrompt.handleSentenceGenerationType(MailSentenceGenerationType.valueOf(content));
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

        String message = Optional.ofNullable(responseEntity.getBody())
                .map(ChatBotResponse::getResult)
                .map(ChatBotResponse.Result::getMessage)
                .map(ClovaMessage::getContent)
                .map(s -> s.replaceAll("-\\s*\n", "-")) // "-" 뒤 개행 제거
                .orElse("응답 없음");

        if (url.equals(verificationHost)) {
            message += "\n\n 위와 같이 답장을 작성할 수 있습니다.\n더 필요하신 도움이 있으시면 말씀해 주세요.";
        }

        return message;
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
