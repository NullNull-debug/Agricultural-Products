package com.farmproduct.controller;

import com.farmproduct.entity.Review;
import com.farmproduct.mapper.ReviewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 评价控制器
 * <p>
 * 处理评价相关的HTTP请求，包括添加、查询和删除操作
 * </p>
 */
@RestController
@RequestMapping("/api/review") // 映射路径
@CrossOrigin // 允许跨域请求
public class ReviewController {
    
    @Autowired
    private ReviewMapper reviewMapper; // 评价Mapper，用于数据库操作
    
    /**
     * 添加评价
     * <p>
     * 接收评价信息，设置创建时间，添加到数据库
     * </p>
     * 
     * @param review 评价信息
     * @return 添加的评价信息
     */
    @PostMapping("/add")
    public Review add(@RequestBody Review review) {
        // 设置评价的创建时间
        review.setCreateTime(new Date());
        // 插入评价到数据库
        reviewMapper.insert(review);
        return review;
    }
    
    /**
     * 根据商品ID获取评价列表
     * <p>
     * 通过商品ID查询该商品的所有评价
     * </p>
     * 
     * @param productId 商品ID
     * @return 评价列表
     */
    @GetMapping("/listByProduct/{productId}")
    public List<Review> listByProduct(@PathVariable Long productId) {
        return reviewMapper.findByProductId(productId);
    }
    
    /**
     * 根据用户ID获取评价列表
     * <p>
     * 通过用户ID查询该用户的所有评价
     * </p>
     * 
     * @param userId 用户ID
     * @return 评价列表
     */
    @GetMapping("/listByUser/{userId}")
    public List<Review> listByUser(@PathVariable Long userId) {
        return reviewMapper.findByUserId(userId);
    }
    
    /**
     * 获取评价详情
     * <p>
     * 根据评价ID查询评价详情
     * </p>
     * 
     * @param id 评价ID
     * @return 评价详情
     */
    @GetMapping("/get/{id}")
    public Review get(@PathVariable Long id) {
        return reviewMapper.selectById(id);
    }
    
    /**
     * 删除评价
     * <p>
     * 根据评价ID删除评价
     * </p>
     * 
     * @param id 评价ID
     */
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        reviewMapper.deleteById(id);
    }
}
