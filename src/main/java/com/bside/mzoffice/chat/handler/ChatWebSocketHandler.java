package com.bside.mzoffice.chat.handler;

import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("클라이언트 연결됨: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        System.out.println("받은 메시지: " + message.getPayload());

        // 받은 메시지를 다시 클라이언트(A)에게 전송
        session.sendMessage(new TextMessage("서버 응답: TEST "));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        System.out.println("클라이언트 연결 종료: " + session.getId());
    }
}
