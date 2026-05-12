package com.farmproduct.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 评价实体类
 * <p>
 * 用于存储用户对商品的评价信息，包括评分、内容等
 * </p>
 */
@Data
@TableName("reviews") // 指定数据库表名
public class Review {
    /**
     * 评价ID
     * <p>
     * 自增主键
     * </p>
     */
    @TableId(type = IdType.AUTO) // 主键生成策略：自动递增
    private Long id;
    
    /**
     * 商品ID
     * <p>
     * 被评价商品的ID
     * </p>
     */
    private Long productId;
    
    /**
     * 用户ID
     * <p>
     * 评价用户的ID
     * </p>
     */
    private Long userId;
    
    /**
     * 评分
     * <p>
     * 用户对商品的评分，通常为1-5分
     * </p>
     */
    private Integer rating;
    
    /**
     * 评价内容
     * <p>
     * 用户对商品的详细评价内容
     * </p>
     */
    private String content;
    
    /**
     * 创建时间
     * <p>
     * 评价的创建时间
     * </p>
     */
    private Date createTime;
}
