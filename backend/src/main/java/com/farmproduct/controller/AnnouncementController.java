package com.farmproduct.controller;

import com.farmproduct.entity.Announcement;
import com.farmproduct.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公告控制器
 * <p>
 * 处理公告相关的HTTP请求，包括添加、更新、删除、查询操作
 * </p>
 */
@RestController
@RequestMapping("/api/announcement") // 映射路径
@CrossOrigin // 允许跨域请求
public class AnnouncementController {
    
    @Autowired
    private AnnouncementService announcementService; // 公告服务，处理业务逻辑
    
    /**
     * 添加公告
     * <p>
     * 接收公告信息，调用AnnouncementService进行添加处理
     * </p>
     * 
     * @param announcement 公告信息
     * @return 添加的公告信息
     */
    @PostMapping("/add")
    public Announcement add(@RequestBody Announcement announcement) {
        return announcementService.add(announcement);
    }
    
    /**
     * 更新公告
     * <p>
     * 接收公告更新信息，调用AnnouncementService进行更新处理
     * </p>
     * 
     * @param announcement 公告更新信息
     * @return 更新后的公告信息
     */
    @PutMapping("/update")
    public Announcement update(@RequestBody Announcement announcement) {
        return announcementService.update(announcement);
    }
    
    /**
     * 删除公告
     * <p>
     * 根据公告ID删除公告，调用AnnouncementService进行删除处理
     * </p>
     * 
     * @param id 公告ID
     */
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        announcementService.delete(id);
    }
    
    /**
     * 获取公告详情
     * <p>
     * 根据公告ID获取公告详情，调用AnnouncementService进行查询处理
     * </p>
     * 
     * @param id 公告ID
     * @return 公告详情
     */
    @GetMapping("/get/{id}")
    public Announcement get(@PathVariable Long id) {
        return announcementService.get(id);
    }
    
    /**
     * 获取公告列表
     * <p>
     * 查询所有公告信息，调用AnnouncementService进行查询处理
     * </p>
     * 
     * @return 公告列表
     */
    @GetMapping("/list")
    public List<Announcement> list() {
        return announcementService.list();
    }
    
    /**
     * 获取已发布的公告列表
     * <p>
     * 查询已发布的公告信息，调用AnnouncementService进行查询处理
     * </p>
     * 
     * @return 已发布的公告列表
     */
    @GetMapping("/listPublished")
    public List<Announcement> listPublished() {
        return announcementService.listPublished();
    }
}
