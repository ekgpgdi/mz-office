package com.bside.mzoffice.chat.handler;

import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.stereotype.Component;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws AuthenticationException {
        System.out.println("클라이언트 연결됨: " + session.getId());

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
        System.out.println("받은 메시지: " + message.getPayload());

        Long customerId = (Long) session.getAttributes().get("customerId");

        if (customerId != null) {
            System.out.println(customerId + " sent a message: " + message.getPayload());

            // 응답 메시지 전송
            session.sendMessage(new TextMessage("Hello " + customerId + ", your message: " + message.getPayload()));
        } else {
            throw new AuthenticationException();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        System.out.println("클라이언트 연결 종료: " + session.getId());
    }
}
