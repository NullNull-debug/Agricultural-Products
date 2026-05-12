package com.farmproduct.controller;

import com.farmproduct.entity.Product;
import com.farmproduct.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品控制器
 * <p>
 * 处理商品相关的HTTP请求，包括添加、查询、更新和删除操作
 * </p>
 */
@RestController
@RequestMapping("/api/product") // 映射路径
@CrossOrigin // 允许跨域请求
public class ProductController {
    
    @Autowired
    private ProductMapper productMapper; // 商品Mapper，用于数据库操作
    
    /**
     * 添加商品
     * <p>
     * 接收商品信息，添加到数据库
     * </p>
     * 
     * @param product 商品信息
     * @return 添加的商品信息
     */
    @PostMapping("/add")
    public Product add(@RequestBody Product product) {
        productMapper.insert(product);
        return product;
    }
    
    /**
     * 获取商品列表
     * <p>
     * 查询所有商品信息
     * </p>
     * 
     * @return 商品列表
     */
    @GetMapping("/list")
    public List<Product> list() {
        return productMapper.selectList(null);
    }
    
    /**
     * 根据分类获取商品列表
     * <p>
     * 通过商品分类查询商品列表
     * </p>
     * 
     * @param category 商品分类
     * @return 商品列表
     */
    @GetMapping("/listByCategory/{category}")
    public List<Product> listByCategory(@PathVariable String category) {
        return productMapper.findByCategory(category);
    }
    
    /**
     * 根据状态获取商品列表
     * <p>
     * 通过商品状态查询商品列表
     * </p>
     * 
     * @param status 商品状态
     * @return 商品列表
     */
    @GetMapping("/listByStatus/{status}")
    public List<Product> listByStatus(@PathVariable Integer status) {
        return productMapper.findByStatus(status);
    }
    
    /**
     * 获取商品详情
     * <p>
     * 根据商品ID查询商品详情
     * </p>
     * 
     * @param id 商品ID
     * @return 商品详情
     */
    @GetMapping("/get/{id}")
    public Product get(@PathVariable Long id) {
        return productMapper.selectById(id);
    }
    
    /**
     * 更新商品信息
     * <p>
     * 接收商品更新信息，更新数据库
     * </p>
     * 
     * @param product 商品更新信息
     * @return 更新后的商品信息
     */
    @PutMapping("/update")
    public Product update(@RequestBody Product product) {
        productMapper.updateById(product);
        return product;
    }
    
    /**
     * 删除商品
     * <p>
     * 根据商品ID删除商品
     * </p>
     * 
     * @param id 商品ID
     */
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        productMapper.deleteById(id);
    }
}