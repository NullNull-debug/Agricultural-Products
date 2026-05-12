package com.farmproduct.mq;

import com.farmproduct.config.RabbitMQConfig;
import com.farmproduct.websocket.MessageWebSocketHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MessageListener {

    @Autowired
    private MessageWebSocketHandler webSocketHandler;

    @RabbitListener(queues = RabbitMQConfig.USER_QUEUE)
    public void handleUserMessage(@Payload Map<String, Object> message) {
        try {
            System.out.println("========== 收到用户队列消息 ==========");
            System.out.println("消息内容: " + message);
            
            Long targetUserId = null;
            if (message.get("targetUserId") != null) {
                targetUserId = ((Number) message.get("targetUserId")).longValue();
            } else if (message.get("userId") != null) {
                targetUserId = ((Number) message.get("userId")).longValue();
            }
            
            System.out.println("目标用户ID: " + targetUserId);
            webSocketHandler.sendToUser(targetUserId, message);
            System.out.println("已通过WebSocket发送给用户");
            System.out.println("====================================");
        } catch (Exception e) {
            System.out.println("========== 处理用户队列消息失败 ==========");
            e.printStackTrace();
            System.out.println("=======================================");
        }
    }

    @RabbitListener(queues = RabbitMQConfig.ADMIN_QUEUE)
    public void handleAdminMessage(@Payload Map<String, Object> message) {
        try {
            System.out.println("========== 收到管理员队列消息 ==========");
            System.out.println("消息内容: " + message);
            
            Long targetAdminId = null;
            if (message.get("targetAdminId") != null) {
                targetAdminId = ((Number) message.get("targetAdminId")).longValue();
            } else if (message.get("targetUserId") != null) {
                targetAdminId = ((Number) message.get("targetUserId")).longValue();
            } else if (message.get("userId") != null) {
                targetAdminId = ((Number) message.get("userId")).longValue();
            }
            
            System.out.println("目标管理员ID: " + targetAdminId);
            webSocketHandler.sendToUser(targetAdminId, message);
            System.out.println("已通过WebSocket发送给管理员");
            System.out.println("=======================================");
        } catch (Exception e) {
            System.out.println("========== 处理管理员队列消息失败 ==========");
            e.printStackTrace();
            System.out.println("=========================================");
        }
    }
}