package com.farmproduct.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.farmproduct.entity.User;

/**
 * 用户Mapper接口
 * <p>
 * 继承自BaseMapper，提供用户相关的数据库操作方法
 * </p>
 */
public interface UserMapper extends BaseMapper<User> {
    /**
     * 根据用户名查找用户
     * <p>
     * 通过用户名查询用户信息，用于登录验证等场景
     * </p>
     * 
     * @param username 用户名
     * @return 用户信息
     */
    User findByUsername(String username);
}