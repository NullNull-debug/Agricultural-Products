package com.farmproduct.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.farmproduct.entity.Product;

import java.util.List;

/**
 * 商品Mapper接口
 * <p>
 * 继承自BaseMapper，提供商品相关的数据库操作方法
 * </p>
 */
public interface ProductMapper extends BaseMapper<Product> {
    /**
     * 根据分类查找商品
     * <p>
     * 通过商品分类查询商品列表
     * </p>
     * 
     * @param category 商品分类
     * @return 商品列表
     */
    List<Product> findByCategory(String category);
    
    /**
     * 根据状态查找商品
     * <p>
     * 通过商品状态查询商品列表，如查询上架商品
     * </p>
     * 
     * @param status 商品状态
     * @return 商品列表
     */
    List<Product> findByStatus(Integer status);
}