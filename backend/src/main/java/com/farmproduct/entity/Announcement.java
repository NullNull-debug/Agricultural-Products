package com.farmproduct.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 公告实体类
 * <p>
 * 用于存储系统公告信息，包括标题、内容、发布状态等
 * </p>
 */
@Data
@TableName("announcements") // 指定数据库表名
public class Announcement {
    /**
     * 公告ID
     * <p>
     * 自增主键
     * </p>
     */
    @TableId(type = IdType.AUTO) // 主键生成策略：自动递增
    private Long id;
    
    /**
     * 公告标题
     * <p>
     * 公告的标题
     * </p>
     */
    private String title;
    
    /**
     * 公告内容
     * <p>
     * 公告的详细内容
     * </p>
     */
    private String content;
    
    /**
     * 公告状态
     * <p>
     * 0: 未发布, 1: 已发布
     * </p>
     */
    private Integer status;
    
    /**
     * 创建时间
     * <p>
     * 公告的创建时间
     * </p>
     */
    private Date createTime;
    
    /**
     * 更新时间
     * <p>
     * 公告的最后更新时间
     * </p>
     */
    private Date updateTime;
}
