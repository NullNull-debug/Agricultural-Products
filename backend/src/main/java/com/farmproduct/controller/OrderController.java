package com.farmproduct.controller;

import com.farmproduct.entity.Order;
import com.farmproduct.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 订单控制器
 * <p>
 * 处理订单相关的HTTP请求，包括添加、查询、更新和删除操作
 * </p>
 */
@RestController
@RequestMapping("/api/order") // 映射路径
@CrossOrigin // 允许跨域请求
public class OrderController {
    
    @Autowired
    private OrderMapper orderMapper; // 订单Mapper，用于数据库操作
    
    /**
     * 添加订单
     * <p>
     * 接收订单信息，生成订单号，设置创建和更新时间，添加到数据库
     * </p>
     * 
     * @param order 订单信息
     * @return 添加的订单信息
     */
    @PostMapping("/add")
    public Order add(@RequestBody Order order) {
        // 生成订单号，格式为ORD+时间戳
        order.setOrderNo("ORD" + System.currentTimeMillis());
        // 设置创建时间
        order.setCreateTime(new Date());
        // 设置更新时间
        order.setUpdateTime(new Date());
        // 插入订单到数据库
        orderMapper.insert(order);
        return order;
    }
    
    /**
     * 获取订单列表
     * <p>
     * 查询所有订单信息
     * </p>
     * 
     * @return 订单列表
     */
    @GetMapping("/list")
    public List<Order> list() {
        return orderMapper.selectList(null);
    }
    
    /**
     * 根据用户ID获取订单列表
     * <p>
     * 通过用户ID查询该用户的所有订单
     * </p>
     * 
     * @param userId 用户ID
     * @return 订单列表
     */
    @GetMapping("/listByUser/{userId}")
    public List<Order> listByUser(@PathVariable Long userId) {
        return orderMapper.findByUserId(userId);
    }
    
    /**
     * 获取订单详情
     * <p>
     * 根据订单ID查询订单详情
     * </p>
     * 
     * @param id 订单ID
     * @return 订单详情
     */
    @GetMapping("/get/{id}")
    public Order get(@PathVariable Long id) {
        return orderMapper.selectById(id);
    }
    
    /**
     * 更新订单信息
     * <p>
     * 接收订单更新信息，更新更新时间，更新数据库
     * </p>
     * 
     * @param order 订单更新信息
     * @return 更新后的订单信息
     */
    @PutMapping("/update")
    public Order update(@RequestBody Order order) {
        // 更新订单的更新时间
        order.setUpdateTime(new Date());
        // 更新订单到数据库
        orderMapper.updateById(order);
        return order;
    }
    
    /**
     * 删除订单
     * <p>
     * 根据订单ID删除订单
     * </p>
     * 
     * @param id 订单ID
     */
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        orderMapper.deleteById(id);
    }
}