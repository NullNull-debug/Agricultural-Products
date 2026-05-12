package com.farmproduct.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.farmproduct.entity.Message;
import com.farmproduct.entity.MessageContent;
import com.farmproduct.entity.MessageSession;
import com.farmproduct.mapper.MessageContentMapper;
import com.farmproduct.mapper.MessageMapper;
import com.farmproduct.mapper.MessageSessionMapper;
import com.farmproduct.mapper.UserMapper;
import com.farmproduct.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private MessageSessionMapper sessionMapper;

    @Autowired
    private MessageContentMapper contentMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String UNREAD_COUNT_KEY = "message:unread:user:";
    private static final String CHAT_CACHE_KEY = "message:chat:session:";

    public List<Message> getUserMessages(Long userId) {
        QueryWrapper<Message> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("status", 1);
        wrapper.orderByDesc("create_time");
        return messageMapper.selectList(wrapper);
    }

    public List<MessageSession> getUserSessions(Long userId) {
        QueryWrapper<MessageSession> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("status", 1);
        wrapper.orderByDesc("last_time");
        return sessionMapper.selectList(wrapper);
    }

    public List<MessageContent> getSessionMessages(Long sessionId, int page, int size) {
        QueryWrapper<MessageContent> wrapper = new QueryWrapper<>();
        wrapper.eq("session_id", sessionId);
        wrapper.eq("status", 1);
        wrapper.orderByDesc("message_time");
        wrapper.last("LIMIT " + (page - 1) * size + ", " + size);
        List<MessageContent> messages = contentMapper.selectList(wrapper);
        java.util.Collections.reverse(messages);
        return messages;
    }

    public int getUnreadCount(Long userId) {
        String key = UNREAD_COUNT_KEY + userId;
        Integer count = null;
        try {
            count = (Integer) redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            // Redis连接失败，直接从数据库读取
        }
        
        if (count == null) {
            QueryWrapper<MessageSession> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", userId);
            wrapper.eq("status", 1);
            wrapper.apply("unread_count > 0");
            count = sessionMapper.selectCount(wrapper).intValue();
            try {
                redisTemplate.opsForValue().set(key, count, 30, TimeUnit.MINUTES);
            } catch (Exception e) {
                // Redis连接失败，跳过缓存
            }
        }
        return count;
    }

    public void markSessionRead(Long sessionId, Long userId) {
        MessageSession session = sessionMapper.selectById(sessionId);
        if (session != null && session.getUserId().equals(userId)) {
            session.setUnreadCount(0);
            sessionMapper.updateById(session);

            String key = UNREAD_COUNT_KEY + userId;
            try {
                redisTemplate.delete(key);
            } catch (Exception e) {
                // Redis连接失败，跳过缓存清除
            }
        }
    }

    public void markAllRead(Long userId) {
        QueryWrapper<MessageSession> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("status", 1);

        List<MessageSession> sessions = sessionMapper.selectList(wrapper);
        for (MessageSession session : sessions) {
            session.setUnreadCount(0);
            sessionMapper.updateById(session);
        }

        String key = UNREAD_COUNT_KEY + userId;
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            // Redis连接失败，跳过缓存清除
        }
    }

    public void clearAllMessages(Long userId) {
        QueryWrapper<Message> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        List<Message> messages = messageMapper.selectList(wrapper);
        for (Message message : messages) {
            message.setStatus(0);
            messageMapper.updateById(message);
        }

        QueryWrapper<MessageSession> sessionWrapper = new QueryWrapper<>();
        sessionWrapper.eq("user_id", userId);
        List<MessageSession> sessions = sessionMapper.selectList(sessionWrapper);
        for (MessageSession session : sessions) {
            session.setStatus(0);
            sessionMapper.updateById(session);
        }
    }

    public MessageSession sendMessage(Long sessionId, Long userId, String content, Boolean isSelf) {
        MessageContent messageContent = new MessageContent();
        messageContent.setSessionId(sessionId);
        messageContent.setUserId(userId);
        messageContent.setContent(content);
        messageContent.setIsSelf(isSelf);
        messageContent.setMessageTime(LocalDateTime.now());
        messageContent.setStatus(1);
        messageContent.setCreateTime(LocalDateTime.now());
        contentMapper.insert(messageContent);

        MessageSession session = sessionMapper.selectById(sessionId);
        if (session != null) {
            session.setLastMessage(content);
            session.setLastTime(LocalDateTime.now());
            if (!isSelf) {
                session.setUnreadCount(session.getUnreadCount() + 1);
            }
            sessionMapper.updateById(session);

            String key = UNREAD_COUNT_KEY + session.getUserId();
            try {
                redisTemplate.delete(key);
            } catch (Exception e) {
                // Redis连接失败，跳过缓存清除
            }

            return session;
        }
        return null;
    }

    public MessageSession createServiceSession(Long userId) {
        QueryWrapper<MessageSession> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("session_type", "service");
        wrapper.eq("status", 1);
        MessageSession existing = sessionMapper.selectOne(wrapper);

        if (existing != null) {
            return existing;
        }

        MessageSession session = new MessageSession();
        session.setUserId(userId);
        session.setSessionType("service");
        session.setTargetName("客服中心");
        session.setTargetId(1L);
        session.setUnreadCount(0);
        session.setStatus(1);
        session.setCreateTime(LocalDateTime.now());
        session.setUpdateTime(LocalDateTime.now());
        sessionMapper.insert(session);
        return session;
    }

    public MessageSession createPrivateSession(Long userId, String contactId) {
        User contactUser = userMapper.findByUsername(contactId);
        if (contactUser == null) {
            throw new RuntimeException("用户不存在");
        }

        Long targetUserId = contactUser.getId();
        String targetUserName = contactUser.getUsername();

        QueryWrapper<MessageSession> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("session_type", "private");
        wrapper.eq("target_id", targetUserId);
        wrapper.eq("status", 1);
        MessageSession existing = sessionMapper.selectOne(wrapper);

        if (existing != null) {
            return existing;
        }

        Long conversationId = generateConversationId(userId, targetUserId);
        
        QueryWrapper<MessageSession> reverseWrapper = new QueryWrapper<>();
        reverseWrapper.eq("user_id", targetUserId);
        reverseWrapper.eq("session_type", "private");
        reverseWrapper.eq("target_id", userId);
        reverseWrapper.eq("status", 1);
        MessageSession reverseExisting = sessionMapper.selectOne(reverseWrapper);

        if (reverseExisting != null) {
            return reverseExisting;
        }

        MessageSession session = new MessageSession();
        session.setId(conversationId);
        session.setUserId(userId);
        session.setSessionType("private");
        session.setTargetName(targetUserName);
        session.setTargetId(targetUserId);
        session.setLastMessage("开始聊天吧");
        session.setLastTime(LocalDateTime.now());
        session.setUnreadCount(0);
        session.setStatus(1);
        session.setCreateTime(LocalDateTime.now());
        session.setUpdateTime(LocalDateTime.now());
        sessionMapper.insert(session);

        User currentUser = userMapper.selectById(userId);
        MessageSession reverseSession = new MessageSession();
        reverseSession.setId(conversationId);
        reverseSession.setUserId(targetUserId);
        reverseSession.setSessionType("private");
        reverseSession.setTargetName(currentUser != null ? currentUser.getUsername() : "用户");
        reverseSession.setTargetId(userId);
        reverseSession.setLastMessage("开始聊天吧");
        reverseSession.setLastTime(LocalDateTime.now());
        reverseSession.setUnreadCount(0);
        reverseSession.setStatus(1);
        reverseSession.setCreateTime(LocalDateTime.now());
        reverseSession.setUpdateTime(LocalDateTime.now());
        sessionMapper.insert(reverseSession);

        return session;
    }

    private Long generateConversationId(Long userId1, Long userId2) {
        if (userId1 < userId2) {
            return Long.parseLong(userId1.toString() + userId2.toString());
        } else {
            return Long.parseLong(userId2.toString() + userId1.toString());
        }
    }

    public List<MessageSession> getAdminSessions() {
        QueryWrapper<MessageSession> wrapper = new QueryWrapper<>();
        wrapper.eq("session_type", "service");
        wrapper.eq("status", 1);
        wrapper.orderByDesc("last_time");
        return sessionMapper.selectList(wrapper);
    }

    public void cacheMessage(Long sessionId, List<MessageContent> messages) {
        String key = CHAT_CACHE_KEY + sessionId;
        try {
            redisTemplate.opsForValue().set(key, messages, 30, TimeUnit.MINUTES);
        } catch (Exception e) {
            // Redis连接失败，跳过缓存
        }
    }

    public List<MessageContent> getCachedMessages(Long sessionId) {
        String key = CHAT_CACHE_KEY + sessionId;
        try {
            return (List<MessageContent>) redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            // Redis连接失败，返回null
            return null;
        }
    }
}