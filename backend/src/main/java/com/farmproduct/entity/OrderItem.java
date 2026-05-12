package com.farmproduct.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 订单项实体类
 * <p>
 * 用于存储订单中的商品信息，包括商品ID、数量、价格等
 * </p>
 */
@Data
@TableName("order_items") // 指定数据库表名
public class OrderItem {
    /**
     * 订单项ID
     * <p>
     * 自增主键
     * </p>
     */
    @TableId(type = IdType.AUTO) // 主键生成策略：自动递增
    private Long id;
    
    /**
     * 订单ID
     * <p>
     * 所属订单的ID
     * </p>
     */
    private Long orderId;
    
    /**
     * 商品ID
     * <p>
     * 商品的ID
     * </p>
     */
    private Long productId;
    
    /**
     * 商品数量
     * <p>
     * 订单中该商品的数量
     * </p>
     */
    private Integer quantity;
    
    /**
     * 商品价格
     * <p>
     * 订单中该商品的价格
     * </p>
     */
    private Double price;
}