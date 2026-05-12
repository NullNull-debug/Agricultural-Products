package com.farmproduct.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("message_session")
public class MessageSession {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String sessionType;
    private String targetName;
    private Long targetId;
    private String lastMessage;
    private LocalDateTime lastTime;
    private Integer unreadCount;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}