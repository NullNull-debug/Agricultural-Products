package com.farmproduct.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.farmproduct.entity.Review;

import java.util.List;

/**
 * 评价Mapper接口
 * <p>
 * 继承自BaseMapper，提供评价相关的数据库操作方法
 * </p>
 */
public interface ReviewMapper extends BaseMapper<Review> {
    /**
     * 根据商品ID查找评价
     * <p>
     * 通过商品ID查询该商品的所有评价
     * </p>
     * 
     * @param productId 商品ID
     * @return 评价列表
     */
    List<Review> findByProductId(Long productId);
    
    /**
     * 根据用户ID查找评价
     * <p>
     * 通过用户ID查询该用户的所有评价
     * </p>
     * 
     * @param userId 用户ID
     * @return 评价列表
     */
    List<Review> findByUserId(Long userId);
}