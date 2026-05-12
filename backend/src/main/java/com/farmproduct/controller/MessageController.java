package com.farmproduct.controller;

import com.farmproduct.config.RabbitMQConfig;
import com.farmproduct.entity.Message;
import com.farmproduct.entity.MessageContent;
import com.farmproduct.entity.MessageSession;
import com.farmproduct.service.MessageService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/message")
@CrossOrigin(origins = "*")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/list")
    public Map<String, Object> getMessageList(@RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            System.out.println("========== 获取消息列表 ==========");
            System.out.println("userId: " + userId);

            List<Message> messages = messageService.getUserMessages(userId);
            List<MessageSession> sessions = messageService.getUserSessions(userId);

            System.out.println("消息数量: " + messages.size());
            System.out.println("会话数量: " + sessions.size());

            Map<String, Object> data = new HashMap<>();
            data.put("messages", messages);
            data.put("sessions", sessions);
            data.put("totalUnread", messageService.getUnreadCount(userId));

            result.put("code", 200);
            result.put("message", "success");
            result.put("data", data);
        } catch (Exception e) {
            System.out.println("========== 获取消息列表异常 ==========");
            e.printStackTrace();
            result.put("code", 500);
            result.put("message", "获取消息列表失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/chat/{sessionId}")
    public Map<String, Object> getChatMessages(
            @PathVariable Long sessionId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Map<String, Object> result = new HashMap<>();
        try {
            System.out.println("========== 获取聊天记录 ==========");
            System.out.println("sessionId: " + sessionId);

            List<MessageContent> messages = messageService.getSessionMessages(sessionId, page, size);

            System.out.println("消息数量: " + messages.size());
            messages.forEach(msg -> {
                System.out.println("  - id: " + msg.getId() + ", content: " + msg.getContent() + ", isSelf: " + msg.getIsSelf());
            });

            result.put("code", 200);
            result.put("message", "success");
            result.put("data", messages);
        } catch (Exception e) {
            System.out.println("========== 获取聊天记录异常 ==========");
            e.printStackTrace();
            result.put("code", 500);
            result.put("message", "获取聊天记录失败: " + e.getMessage());
        }
        return result;
    }

    @PostMapping("/send")
    public Map<String, Object> sendMessage(
            @RequestParam Long sessionId,
            @RequestParam Long userId,
            @RequestParam String content) {
        Map<String, Object> result = new HashMap<>();
        try {
            System.out.println("========== 开始发送消息 ==========");
            System.out.println("sessionId: " + sessionId);
            System.out.println("userId: " + userId);
            System.out.println("content: " + content);

            MessageSession session = messageService.sendMessage(sessionId, userId, content, true);

            if (session != null) {
                System.out.println("会话信息: " + session);
                System.out.println("session.id: " + session.getId());
                System.out.println("session.userId: " + session.getUserId());
                System.out.println("session.targetId: " + session.getTargetId());
                System.out.println("session.sessionType: " + session.getSessionType());

                Map<String, Object> msgData = new HashMap<>();
                msgData.put("type", "new_message");
                msgData.put("sessionId", session.getId());
                msgData.put("userId", userId);
                msgData.put("content", content);
                msgData.put("targetUserId", session.getTargetId());
                msgData.put("isSelf", true);
                msgData.put("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));

                String routingKey = "user." + session.getTargetId();
                System.out.println("计算得到的路由键: " + routingKey);

                try {
                    System.out.println("准备发送到RabbitMQ:");
                    System.out.println("  Exchange: " + RabbitMQConfig.EXCHANGE_NAME);
                    System.out.println("  RoutingKey: " + routingKey);
                    System.out.println("  Message: " + msgData);

                    rabbitTemplate.convertAndSend(
                            RabbitMQConfig.EXCHANGE_NAME,
                            routingKey,
                            msgData
                    );

                    System.out.println("RabbitMQ消息发送成功！");
                } catch (Exception mqException) {
                    System.out.println("========== RabbitMQ发送失败 ==========");
                    System.out.println("错误信息: " + mqException.getMessage());
                    System.out.println("但消息已保存到数据库！");
                }

                result.put("code", 200);
                result.put("message", "发送成功");
                result.put("data", msgData);
                System.out.println("返回给前端的数据: " + result);
            } else {
                System.out.println("发送失败，会话不存在");
                result.put("code", 400);
                result.put("message", "发送失败，会话不存在");
            }
        } catch (Exception e) {
            System.out.println("========== 发送消息异常 ==========");
            e.printStackTrace();
            result.put("code", 500);
            result.put("message", "发送消息失败: " + e.getMessage());
        }
        return result;
    }

    @PostMapping("/mark-read")
    public Map<String, Object> markRead(
            @RequestParam Long sessionId,
            @RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            messageService.markSessionRead(sessionId, userId);
            result.put("code", 200);
            result.put("message", "已标记已读");
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "标记已读失败: " + e.getMessage());
        }
        return result;
    }

    @PostMapping("/mark-message-read")
    public Map<String, Object> markMessageRead(
            @RequestParam Long messageId,
            @RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            messageService.markMessageRead(messageId, userId);
            result.put("code", 200);
            result.put("message", "已标记已读");
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "标记已读失败: " + e.getMessage());
        }
        return result;
    }

    @PostMapping("/mark-all-read")
    public Map<String, Object> markAllRead(@RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            messageService.markAllRead(userId);
            result.put("code", 200);
            result.put("message", "已全部标记已读");
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "标记已读失败: " + e.getMessage());
        }
        return result;
    }

    @DeleteMapping("/clear")
    public Map<String, Object> clearAll(@RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            messageService.clearAllMessages(userId);
            result.put("code", 200);
            result.put("message", "已清空所有消息");
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "清空消息失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/unread-count")
    public Map<String, Object> getUnreadCount(@RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            int count = messageService.getUnreadCount(userId);
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", count);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取未读数失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/service-session")
    public Map<String, Object> getServiceSession(@RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            MessageSession session = messageService.createServiceSession(userId);
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", session);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取客服会话失败: " + e.getMessage());
        }
        return result;
    }

    @PostMapping("/private-session")
    public Map<String, Object> createPrivateSession(
            @RequestParam Long userId,
            @RequestParam String contactId) {
        Map<String, Object> result = new HashMap<>();
        try {
            System.out.println("========== 创建私信会话 ==========");
            System.out.println("userId: " + userId);
            System.out.println("contactId: " + contactId);

            MessageSession session = messageService.createPrivateSession(userId, contactId);

            System.out.println("创建的会话: " + session);
            System.out.println("会话ID: " + session.getId());

            Map<String, Object> data = new HashMap<>();
            data.put("sessionId", session.getId());
            result.put("code", 200);
            result.put("message", "创建成功");
            result.put("data", data);
        } catch (Exception e) {
            System.out.println("========== 创建私信会话异常 ==========");
            e.printStackTrace();
            result.put("code", 500);
            result.put("message", "创建会话失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/admin/sessions")
    public Map<String, Object> getAdminSessions() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<MessageSession> sessions = messageService.getAdminSessions();
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", sessions);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取会话列表失败: " + e.getMessage());
        }
        return result;
    }

    @PostMapping("/admin/send")
    public Map<String, Object> adminSendMessage(
            @RequestParam Long sessionId,
            @RequestParam Long adminId,
            @RequestParam String content) {
        Map<String, Object> result = new HashMap<>();
        try {
            System.out.println("========== 管理员发送消息 ==========");
            System.out.println("sessionId: " + sessionId);
            System.out.println("adminId: " + adminId);
            System.out.println("content: " + content);

            MessageSession session = messageService.sendMessage(sessionId, adminId, content, false);

            if (session != null) {
                System.out.println("会话信息: " + session);
                System.out.println("session.userId (目标用户): " + session.getUserId());

                String uniqueMessageId = System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);

                Map<String, Object> msgData = new HashMap<>();
                msgData.put("type", "new_message");
                msgData.put("messageId", uniqueMessageId);
                msgData.put("sessionId", sessionId);
                msgData.put("content", content);
                msgData.put("isSelf", false);
                msgData.put("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
                msgData.put("targetUserId", session.getUserId());

                String routingKey = "user." + session.getUserId();

                System.out.println("准备发送到RabbitMQ:");
                System.out.println("  Exchange: " + RabbitMQConfig.EXCHANGE_NAME);
                System.out.println("  RoutingKey: " + routingKey);
                System.out.println("  Message: " + msgData);

                rabbitTemplate.convertAndSend(
                        RabbitMQConfig.EXCHANGE_NAME,
                        routingKey,
                        msgData
                );

                System.out.println("RabbitMQ消息发送成功！");

                result.put("code", 200);
                result.put("message", "发送成功");
                result.put("data", msgData);
            } else {
                System.out.println("发送失败，会话不存在");
                result.put("code", 400);
                result.put("message", "发送失败，会话不存在");
            }
        } catch (Exception e) {
            System.out.println("========== 管理员发送消息异常 ==========");
            e.printStackTrace();
            result.put("code", 500);
            result.put("message", "发送消息失败: " + e.getMessage());
        }
        return result;
    }
}