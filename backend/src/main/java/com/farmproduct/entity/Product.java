package com.farmproduct.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 商品实体类
 * <p>
 * 用于存储商品信息，包括商品基本信息、价格、库存等
 * </p>
 */
@Data
@TableName("products") // 指定数据库表名
public class Product {
    /**
     * 商品ID
     * <p>
     * 自增主键
     * </p>
     */
    @TableId(type = IdType.AUTO) // 主键生成策略：自动递增
    private Long id;
    
    /**
     * 商品名称
     * <p>
     * 商品的名称
     * </p>
     */
    private String name;
    
    /**
     * 商品分类
     * <p>
     * 商品所属的分类，如蔬菜、水果、肉类等
     * </p>
     */
    private String category;
    
    /**
     * 商品描述
     * <p>
     * 商品的详细描述信息
     * </p>
     */
    private String description;
    
    /**
     * 商品价格
     * <p>
     * 商品的销售价格
     * </p>
     */
    private Double price;
    
    /**
     * 商品库存
     * <p>
     * 商品的库存数量
     * </p>
     */
    private Integer stock;
    
    /**
     * 商品图片
     * <p>
     * 商品图片的路径或URL
     * </p>
     */
    private String image;
    
    /**
     * 商品状态
     * <p>
     * 0: 下架, 1: 上架
     * </p>
     */
    private Integer status;
}