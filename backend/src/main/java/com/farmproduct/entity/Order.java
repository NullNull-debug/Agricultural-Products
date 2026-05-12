package com.farmproduct.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 订单实体类
 * <p>
 * 用于存储订单信息，包括订单基本信息、状态、金额等
 * </p>
 */
@Data
@TableName("orders") // 指定数据库表名
public class Order {
    /**
     * 订单ID
     * <p>
     * 自增主键
     * </p>
     */
    @TableId(type = IdType.AUTO) // 主键生成策略：自动递增
    private Long id;
    
    /**
     * 订单号
     * <p>
     * 订单的唯一标识
     * </p>
     */
    private String orderNo;
    
    /**
     * 用户ID
     * <p>
     * 订单所属用户的ID
     * </p>
     */
    private Long userId;
    
    /**
     * 总金额
     * <p>
     * 订单的总金额
     * </p>
     */
    private Double totalAmount;
    
    /**
     * 订单状态
     * <p>
     * 0: 未支付, 1: 已支付, 2: 已发货, 3: 已评价, 4: 已取消
     * </p>
     */
    private Integer status;
    
    /**
     * 创建时间
     * <p>
     * 订单的创建时间
     * </p>
     */
    private Date createTime;
    
    /**
     * 更新时间
     * <p>
     * 订单的最后更新时间
     * </p>
     */
    private Date updateTime;
}