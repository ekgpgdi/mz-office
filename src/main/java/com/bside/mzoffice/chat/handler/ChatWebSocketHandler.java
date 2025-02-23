package com.bside.mzoffice.chat.handler;

import com.bside.mzoffice.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.stereotype.Component;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {
    private final ChatService chatService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws AuthenticationException {
        log.info("클라이언트 연결됨: " + session.getId());

        Long customerId = (Long) session.getAttributes().get("customerId");

        if (customerId != null) {
            System.out.println("Authenticated user: " + customerId);
        } else {
            try {
                session.close(CloseStatus.NOT_ACCEPTABLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
            throw new AuthenticationException();
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        log.info("받은 메시지: " + message.getPayload());
        Long customerId = (Long) session.getAttributes().get("customerId");

        if (customerId != null) {
            log.info(customerId + " sent a message: " + message.getPayload());
            chatService.parsePayload(customerId, message);
        } else {
            throw new AuthenticationException();
        }


    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("클라이언트 연결 종료: " + session.getId());
    }
}
