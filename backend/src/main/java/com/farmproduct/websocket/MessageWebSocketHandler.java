package com.farmproduct.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MessageWebSocketHandler extends TextWebSocketHandler {

    private static final Map<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = getUserIdFromSession(session);
        if (userId != null) {
            userSessions.put(userId, session);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        Map<String, Object> data = objectMapper.readValue(payload, Map.class);
        String type = (String) data.get("type");

        if ("chat".equals(type)) {
            Long targetUserId = ((Number) data.get("targetUserId")).longValue();
            String content = (String) data.get("content");
            Long sessionId = ((Number) data.get("sessionId")).longValue();

            WebSocketSession targetSession = userSessions.get(targetUserId);
            if (targetSession != null && targetSession.isOpen()) {
                Map<String, Object> msgData = new ConcurrentHashMap<>();
                msgData.put("type", "new_message");
                msgData.put("sessionId", sessionId);
                msgData.put("content", content);
                msgData.put("isSelf", false);
                msgData.put("time", java.time.LocalDateTime.now().toString());
                targetSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(msgData)));
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = getUserIdFromSession(session);
        if (userId != null) {
            userSessions.remove(userId);
        }
    }

    public void sendToUser(Long userId, Object message) throws Exception {
        WebSocketSession session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        }
    }

    public boolean isUserOnline(Long userId) {
        WebSocketSession session = userSessions.get(userId);
        return session != null && session.isOpen();
    }

    private Long getUserIdFromSession(WebSocketSession session) {
        String userIdStr = (String) session.getAttributes().get("userId");
        if (userIdStr != null) {
            return Long.parseLong(userIdStr);
        }
        return null;
    }
}