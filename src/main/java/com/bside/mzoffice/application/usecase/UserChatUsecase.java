package com.bside.mzoffice.application.usecase;

import com.bside.mzoffice.chat.domain.ChatMessage;
import com.bside.mzoffice.chat.domain.ChatSession;
import com.bside.mzoffice.chat.dto.request.ChatMessageRequest;
import com.bside.mzoffice.chat.dto.response.ChatMessageResponse;
import com.bside.mzoffice.chat.enums.InquiryType;
import com.bside.mzoffice.chat.enums.MessageSenderType;
import com.bside.mzoffice.chat.service.ChatReadService;
import com.bside.mzoffice.chat.service.ChatWriteService;
import com.bside.mzoffice.clovaAi.service.AiService;
import com.bside.mzoffice.common.enums.ResponseCode;
import com.bside.mzoffice.common.exception.customException.ChatException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserChatUsecase {
    private final AiService aiService;
    private final ChatWriteService chatWriteService;
    private final ChatReadService chatReadService;

    public String execute(Long userId, TextMessage message) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // 채팅 메시지 요청 파싱
            ChatMessageRequest chatMessageRequest = objectMapper.readValue(message.getPayload(), ChatMessageRequest.class);

            // 채팅 ID가 없으면 새 채팅 생성, 있으면 기존 채팅 처리
            ChatMessageResponse response = (chatMessageRequest.getChatId() == null)
                    ? notExistsChatId(userId, chatMessageRequest)
                    : existsChatId(userId, chatMessageRequest);

            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            log.error("chat json parse error : " + e.getMessage());
            throw new ChatException(ResponseCode.CHAT_JSON_PARSE_ERROR);
        }
    }

    // 채팅 ID가 없는 경우: 새 채팅 메시지 생성
    public ChatMessageResponse notExistsChatId(Long userId, ChatMessageRequest chatMessageRequest) {
        return chatWriteService.saveChatMessageNew(MessageSenderType.USER, userId, chatMessageRequest);
    }

    // 채팅 ID가 있는 경우: 기존 채팅 메시지 업데이트 및 AI 응답 처리
    public ChatMessageResponse existsChatId(Long userId, ChatMessageRequest chatMessageRequest) {
        ChatMessage chatMessage = chatReadService.get(chatMessageRequest.getChatId());
        String response = "";

        // ChatSession이 존재하는 경우에만 session을 가져옴
        Optional<ChatSession> chatSession = Optional.ofNullable(
                chatMessageRequest.getChatSessionId() != null
                        ? chatReadService.getSession(chatMessage, chatMessageRequest.getChatSessionId())
                        : null
        );

        // 메시지 업데이트
        ChatMessageResponse userMessageResponse = chatWriteService.updateChatMessage(
                MessageSenderType.USER, userId, chatMessageRequest, chatMessage, chatSession
        );

        // AI 응답이 필요한 경우 처리
        if (chatSession.isPresent() && chatMessageRequest.getInquiryType().equals(InquiryType.AI_REQUEST)) {
            response = handleAiResponse(userId, userMessageResponse, chatMessage, chatSession.get());
        }

        // 응답 객체 반환
        return buildChatMessageResponse(userMessageResponse, response);
    }

    // AI 응답 처리
    private String handleAiResponse(Long userId, ChatMessageResponse userMessageResponse, ChatMessage chatMessage, ChatSession chatSession) {
        String response = aiService.sendRequest(chatSession);

        log.info("AI Output for chatId {} and sessionId {}: {}", userMessageResponse.getChatId(), userMessageResponse.getChatSessionId(), response);

        // AI 메시지 생성 후 업데이트
        ChatMessageRequest aiChatMessageRequest = ChatMessageRequest.builder()
                .chatId(userMessageResponse.getChatId())
                .chatSessionId(userMessageResponse.getChatSessionId())
                .inquiryType(InquiryType.AI_RESPONSE)
                .content(response)
                .build();

        chatWriteService.updateChatMessage(MessageSenderType.AI, userId, aiChatMessageRequest, chatMessage, Optional.of(chatSession));

        return response;
    }

    // ChatMessageResponse 객체 빌드
    private ChatMessageResponse buildChatMessageResponse(ChatMessageResponse userMessageResponse, String response) {
        return ChatMessageResponse.builder()
                .chatId(userMessageResponse.getChatId())
                .chatSessionId(userMessageResponse.getChatSessionId())
                .content(response)
                .build();
    }
}