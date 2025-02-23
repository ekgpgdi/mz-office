package com.bside.mzoffice.chat.service;

import com.bside.mzoffice.chat.domain.*;
import com.bside.mzoffice.chat.dto.request.ChatMessageRequest;
import com.bside.mzoffice.chat.repository.ChatRepository;
import com.bside.mzoffice.clovaAi.service.AiService;
import com.bside.mzoffice.common.domain.ResponseCode;
import com.bside.mzoffice.common.exception.customException.ChatException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final AiService aiService;

    @Transactional
    public Message makeMessage(MessageSenderType type, String content) {
        return Message.builder()
                .sender(type)
                .content(content)
                .sentAt(LocalDateTime.now())
                .build();
    }

    @Transactional
    public void saveChatMessage(MessageSenderType type, Long customerId, ChatMessageRequest chatMessageRequest) {
        Message message = makeMessage(type, chatMessageRequest.getContent());
        ChatMessage chatMessage = null;
        if (chatMessageRequest.getChatId() == null) {
            List<Message> messageList = new ArrayList<>();
            messageList.add(message);
            chatMessage = ChatMessage.builder()
                    .customerId(customerId)
                    .createdAt(LocalDateTime.now())
                    .chat(messageList)
                    .build();
        } else {
            chatMessage = chatRepository.findById(chatMessageRequest.getChatId())
                    .orElseThrow(() -> new ChatException(ResponseCode.NOT_FOUND_CHAT));

            chatMessage.getChat().add(message);
        }
        chatRepository.save(chatMessage);
    }

    @Transactional
    public void parsePayload(Long customerId, TextMessage message) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            ChatMessageRequest chatMessage = objectMapper.readValue(message.getPayload(), ChatMessageRequest.class);
            saveChatMessage(MessageSenderType.USER, customerId, chatMessage);

            if (chatMessage.getInquiryType().equals(InquiryType.AI_REQUEST)) {
                log.info("AI output : " + aiService.sendRequest(chatMessage.getContent()));
            }
        } catch (JsonProcessingException e) {
            log.error("chat json parse error : " + e.getMessage());
            throw new ChatException(ResponseCode.CHAT_JSON_PARSE_ERROR);
        }
    }
}
