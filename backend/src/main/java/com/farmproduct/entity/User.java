package com.farmproduct.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户实体类
 * <p>
 * 用于存储用户信息，包括普通用户和管理员
 * </p>
 */
@Data
@TableName("users") // 指定数据库表名
public class User {
    /**
     * 用户ID
     * <p>
     * 自增主键
     * </p>
     */
    @TableId(type = IdType.AUTO) // 主键生成策略：自动递增
    private Long id;
    
    /**
     * 用户名
     * <p>
     * 登录账号，唯一
     * </p>
     */
    private String username;
    
    /**
     * 密码
     * <p>
     * 加密存储的用户密码
     * </p>
     */
    private String password;
    
    /**
     * 真实姓名
     * <p>
     * 用户的真实姓名
     * </p>
     */
    private String name;
    
    /**
     * 电话号码
     * <p>
     * 用户的联系电话
     * </p>
     */
    private String phone;
    
    /**
     * 地址
     * <p>
     * 用户的收货地址
     * </p>
     */
    private String address;
    
    /**
     * 角色
     * <p>
     * 0: 普通用户, 1: 管理员
     * </p>
     */
    private Integer role;
}