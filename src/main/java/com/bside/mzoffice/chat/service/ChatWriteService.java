package com.bside.mzoffice.chat.service;

import com.bside.mzoffice.chat.domain.ChatMessage;
import com.bside.mzoffice.chat.domain.ChatSession;
import com.bside.mzoffice.chat.domain.Message;
import com.bside.mzoffice.chat.dto.request.ChatMessageRequest;
import com.bside.mzoffice.chat.dto.response.ChatMessageResponse;
import com.bside.mzoffice.chat.enums.InquiryType;
import com.bside.mzoffice.chat.enums.MessageSenderType;
import com.bside.mzoffice.chat.repository.ChatMessageRepository;
import com.bside.mzoffice.common.enums.ResponseCode;
import com.bside.mzoffice.common.exception.customException.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatWriteService {
    private final ChatMessageRepository chatMessageRepository;
    private static final Random RANDOM = new SecureRandom();

    @Transactional
    public ResponseCode deleteChatById(Authentication authentication, String chatId) throws AccessDeniedException {
        ChatMessage chatMessage = chatMessageRepository.findById(chatId).orElseThrow(() -> new NotFoundException(ResponseCode.NOT_FOUND_CHAT));
        Long userId = Long.parseLong(authentication.getName());

        if (!chatMessage.getUserId().equals(userId)) throw new AccessDeniedException("FORBIDDEN");
        chatMessageRepository.delete(chatMessage);
        return ResponseCode.SUCCESS;
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
    public ChatMessageResponse saveChatMessageNew(MessageSenderType type, Long userId, ChatMessageRequest chatMessageRequest) {
        Message message = makeMessage(type, chatMessageRequest.getInquiryType(), chatMessageRequest.getContent());

        List<Message> messageList = new ArrayList<>();
        messageList.add(message);

        ChatSession chatSession = makeChatSession(userId, messageList);
        List<ChatSession> chatSessionList = new ArrayList<>();
        chatSessionList.add(chatSession);

        ChatMessage chatMessage = ChatMessage.builder()
                .userId(userId)
                .date(LocalDate.from(LocalDateTime.now()))
                .chatSessions(chatSessionList)
                .build();

        chatMessageRepository.save(chatMessage);

        return ChatMessageResponse.builder()
                .chatId(chatMessage.getId()) // MongoDB의 @Id 자동 생성
                .chatSessionId(chatSession.getChatSessionId())
                .content(message.getContent()) // 유저 메시지 내용
                .build();
    }

    @Transactional
    public ChatMessageResponse updateChatMessage(MessageSenderType type,
                                                 Long userId,
                                                 ChatMessageRequest chatMessageRequest,
                                                 ChatMessage chatMessage,
                                                 Optional<ChatSession> chatSessionOptional) {
        Message message = makeMessage(type, chatMessageRequest.getInquiryType(), chatMessageRequest.getContent());

        chatMessage.setDate(LocalDate.from(LocalDateTime.now()));

        ChatSession chatSession;
        if (chatSessionOptional.isEmpty()) {
            List<Message> messageList = new ArrayList<>();
            messageList.add(message);
            chatSession = makeChatSession(userId, messageList);
            chatMessage.getChatSessions().add(chatSession);
        } else {
            chatSession = chatSessionOptional.get();
            chatSession.getMessages().add(message);
        }

        chatMessageRepository.save(chatMessage);

        return ChatMessageResponse.builder()
                .chatId(chatMessage.getId()) // MongoDB의 @Id 자동 생성
                .chatSessionId(chatSession.getChatSessionId())
                .content(message.getContent()) // 유저 메시지 내용
                .build();
    }
}
