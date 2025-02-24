package com.bside.mzoffice.chat.service;

import com.bside.mzoffice.chat.domain.*;
import com.bside.mzoffice.chat.dto.request.ChatMessageRequest;
import com.bside.mzoffice.chat.dto.response.ChatMessageResponse;
import com.bside.mzoffice.chat.enums.InquiryType;
import com.bside.mzoffice.chat.enums.MessageSenderType;
import com.bside.mzoffice.chat.repository.ChatMessageRepository;
import com.bside.mzoffice.clovaAi.service.AiService;
import com.bside.mzoffice.common.domain.ResponseCode;
import com.bside.mzoffice.common.exception.customException.ChatException;
import com.bside.mzoffice.common.exception.customException.NotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {
    private final ChatMessageRepository chatMessageRepository;
    private final AiService aiService;
    private static final Random RANDOM = new SecureRandom();

    @Transactional(readOnly = true)
    public ChatMessage get(String chatId) {
        return chatMessageRepository.findById(chatId).orElseThrow(() -> new NotFoundException(ResponseCode.NOT_FOUND_CHAT));
    }

    @Transactional(readOnly = true)
    public ChatSession getSession(ChatMessage chatMessage, String chatSessionId) {
        return chatMessage.getChatSessions().stream()
                .filter(session -> session.getChatSessionId().equals(chatSessionId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(ResponseCode.NOT_FOUND_CHAT_SESSION));
    }

    @Transactional
    public Message makeMessage(MessageSenderType type, InquiryType inquiryType, String content) {
        return Message.builder()
                .sender(type)
                .inquiryType(inquiryType)
                .content(content)
                .sentAt(LocalDateTime.now())
                .build();
    }

    public String generateChatSessionId(Long userId) {
        long timestamp = Instant.now().toEpochMilli(); // 현재 시간 (밀리초)
        int randomNum = RANDOM.nextInt(10000); // 0 ~ 9999 난수 생성
        return String.format("%d_%d_%04d", userId, timestamp, randomNum);
    }

    public ChatSession makeChatSession(Long userId, List<Message> messageList) {
        return ChatSession.builder()
                .chatSessionId(generateChatSessionId(userId))
                .createdAt(LocalDateTime.now())
                .messages(messageList)
                .build();
    }

    @Transactional
    public ChatMessageResponse saveChatMessage(MessageSenderType type, Long userId, ChatMessageRequest chatMessageRequest) {
        Message message = makeMessage(type, chatMessageRequest.getInquiryType(), chatMessageRequest.getContent());
        ChatMessage chatMessage;
        ChatSession chatSession;

        if (chatMessageRequest.getChatId() == null) {
            // 1️⃣ 채팅 아이디가 없으면 새로운 채팅과 세션 생성
            List<Message> messageList = new ArrayList<>();
            messageList.add(message);

            chatSession = makeChatSession(userId, messageList);
            List<ChatSession> chatSessionList = new ArrayList<>();
            chatSessionList.add(chatSession);

            chatMessage = ChatMessage.builder()
                    .customerId(userId)
                    .date(LocalDate.from(LocalDateTime.now()))
                    .chatSessions(chatSessionList)
                    .build();
        } else {
            // 2️⃣ 채팅 아이디가 있으면 기존 채팅 가져오기
            chatMessage = get(chatMessageRequest.getChatId());

            if (chatMessageRequest.getChatSessionId() == null) {
                // 2-1️⃣ 세션 아이디가 없으면 새로운 세션 생성
                List<Message> messageList = new ArrayList<>();
                messageList.add(message);
                chatSession = makeChatSession(userId, messageList);
                chatMessage.getChatSessions().add(chatSession);
            } else {
                // 2-2️⃣ 채팅 아이디와 세션 아이디가 있으면 메시지 추가
                chatSession = getSession(chatMessage, chatMessageRequest.getChatSessionId());
                chatSession.getMessages().add(message);
            }
        }

        chatMessageRepository.save(chatMessage);

        // 3️⃣ 생성된 chatId & chatSessionId 반환
        return ChatMessageResponse.builder()
                .chatId(chatMessage.getId()) // MongoDB의 @Id 자동 생성
                .chatSessionId(chatSession.getChatSessionId())
                .content(message.getContent()) // 유저 메시지 내용
                .build();
    }

    public String chat(Long customerId, TextMessage message) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            ChatMessageRequest chatMessageRequest = objectMapper.readValue(message.getPayload(), ChatMessageRequest.class);
            ChatMessageResponse userMessageResponse = saveChatMessage(MessageSenderType.USER, customerId, chatMessageRequest);

            String response = "";
            if (chatMessageRequest.getInquiryType().equals(InquiryType.AI_REQUEST)) {
                // 1️⃣ AI 응답 받기
                response = aiService.sendRequest(getSession(get(userMessageResponse.getChatId()), userMessageResponse.getChatSessionId()));

                // 2️⃣ AI 메시지 저장
                ChatMessageRequest aiChatMessageRequest = ChatMessageRequest.builder()
                        .chatId(userMessageResponse.getChatId())
                        .chatSessionId(userMessageResponse.getChatSessionId())
                        .content(response)
                        .build();
                saveChatMessage(MessageSenderType.AI, customerId, aiChatMessageRequest);
            }

            // 3️⃣ 최종 응답 반환
            ChatMessageResponse finalResponse = ChatMessageResponse.builder()
                    .chatId(userMessageResponse.getChatId())
                    .chatSessionId(userMessageResponse.getChatSessionId())
                    .content(response)
                    .build();

            return objectMapper.writeValueAsString(finalResponse);
        } catch (JsonProcessingException e) {
            log.error("chat json parse error : " + e.getMessage());
            throw new ChatException(ResponseCode.CHAT_JSON_PARSE_ERROR);
        }
    }
}
