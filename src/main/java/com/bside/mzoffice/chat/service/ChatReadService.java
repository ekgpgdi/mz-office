package com.bside.mzoffice.chat.service;

import com.bside.mzoffice.chat.domain.ChatMessage;
import com.bside.mzoffice.chat.domain.ChatSession;
import com.bside.mzoffice.chat.dto.response.ChatMessageDetailResponse;
import com.bside.mzoffice.chat.dto.response.ChatMessageSummaryResponse;
import com.bside.mzoffice.chat.dto.response.ChatSessionResponse;
import com.bside.mzoffice.chat.dto.response.MessageResponse;
import com.bside.mzoffice.chat.repository.ChatMessageRepository;
import com.bside.mzoffice.common.domain.ResponseCode;
import com.bside.mzoffice.common.exception.customException.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatReadService {
    private final ChatMessageRepository chatMessageRepository;

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

    @Transactional(readOnly = true)
    public List<ChatMessageSummaryResponse> getRecentChats(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        log.info("userId : " + userId);
        List<ChatMessage> chatMessages = chatMessageRepository.findTop3ByUserIdAndDateBeforeTodayOrderByDateDesc(userId, LocalDate.now(), PageRequest.of(0, 3));

        return chatMessages.stream()
                .map(chat -> new ChatMessageSummaryResponse(chat.getId(), chat.getDate())).toList();
    }

    @Transactional(readOnly = true)
    public ChatMessageDetailResponse makeChatMessageDetailResponse(ChatMessage chatMessage) {
        List<ChatSessionResponse> chatSessionResponseList = chatMessage.getChatSessions().stream().map(chatSession -> ChatSessionResponse.builder()
                .chatSessionId(chatSession.getChatSessionId())
                .createdAt(chatSession.getCreatedAt())
                .messages(chatSession.getMessages().stream().map(message -> MessageResponse.builder()
                        .sender(message.getSender())
                        .inquiryType(message.getInquiryType())
                        .content(message.getContent())
                        .sentAt(message.getSentAt())
                        .build()).toList())
                .build()).toList();

        return new ChatMessageDetailResponse(chatMessage.getId(), chatMessage.getDate(), chatSessionResponseList);
    }

    @Transactional(readOnly = true)
    public ChatMessageDetailResponse getChatById(Authentication authentication, String chatId) throws AccessDeniedException {
        ChatMessage chatMessage = chatMessageRepository.findById(chatId).orElseThrow(() -> new NotFoundException(ResponseCode.NOT_FOUND_CHAT));
        Long userId = Long.parseLong(authentication.getName());

        if (!chatMessage.getUserId().equals(userId)) throw new AccessDeniedException("FORBIDDEN");

        return makeChatMessageDetailResponse(chatMessage);
    }

    @Transactional(readOnly = true)
    public ChatMessageDetailResponse getActiveChat(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        List<ChatMessage> activeChat = chatMessageRepository.findByUserIdAndDate(userId, LocalDate.now());

        if (activeChat.size() == 0) {
            return new ChatMessageDetailResponse(null, null, new ArrayList<>());
        }
        return makeChatMessageDetailResponse(activeChat.get(0));
    }
}
