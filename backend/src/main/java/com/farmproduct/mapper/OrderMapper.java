package com.farmproduct.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.farmproduct.entity.Order;

import java.util.List;

/**
 * 订单Mapper接口
 * <p>
 * 继承自BaseMapper，提供订单相关的数据库操作方法
 * </p>
 */
public interface OrderMapper extends BaseMapper<Order> {
    /**
     * 根据用户ID查找订单
     * <p>
     * 通过用户ID查询该用户的所有订单
     * </p>
     * 
     * @param userId 用户ID
     * @return 订单列表
     */
    List<Order> findByUserId(Long userId);
    
    /**
     * 根据订单号查找订单
     * <p>
     * 通过订单号查询订单详情
     * </p>
     * 
     * @param orderNo 订单号
     * @return 订单信息
     */
    Order findByOrderNo(String orderNo);
}