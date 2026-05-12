package com.farmproduct.service;

import com.farmproduct.entity.Announcement;
import com.farmproduct.mapper.AnnouncementMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 公告服务类
 * <p>
 * 处理公告相关的业务逻辑，包括添加、更新、删除、查询操作
 * </p>
 */
@Service
public class AnnouncementService {
    
    @Autowired
    private AnnouncementMapper announcementMapper; // 公告Mapper，用于数据库操作
    
    /**
     * 添加公告
     * <p>
     * 处理公告添加逻辑，设置创建和更新时间，保存到数据库
     * </p>
     * 
     * @param announcement 公告信息
     * @return 添加的公告信息
     */
    public Announcement add(Announcement announcement) {
        // 设置创建时间
        announcement.setCreateTime(new Date());
        // 设置更新时间
        announcement.setUpdateTime(new Date());
        // 插入公告到数据库
        announcementMapper.insert(announcement);
        return announcement;
    }
    
    /**
     * 更新公告
     * <p>
     * 处理公告更新逻辑，检查公告是否存在，更新公告信息
     * </p>
     * 
     * @param announcement 公告更新信息
     * @return 更新后的公告信息
     * @throws RuntimeException 如果公告不存在
     */
    public Announcement update(Announcement announcement) {
        // 查找公告是否存在
        Announcement existing = announcementMapper.selectById(announcement.getId());
        if (existing == null) {
            throw new RuntimeException("公告不存在");
        }
        // 更新公告信息
        existing.setTitle(announcement.getTitle());
        existing.setContent(announcement.getContent());
        existing.setStatus(announcement.getStatus());
        existing.setUpdateTime(new Date());
        // 保存更新
        announcementMapper.updateById(existing);
        return existing;
    }
    
    /**
     * 删除公告
     * <p>
     * 根据公告ID删除公告
     * </p>
     * 
     * @param id 公告ID
     */
    public void delete(Long id) {
        announcementMapper.deleteById(id);
    }
    
    /**
     * 获取公告详情
     * <p>
     * 根据公告ID获取公告详情
     * </p>
     * 
     * @param id 公告ID
     * @return 公告详情
     * @throws RuntimeException 如果公告不存在
     */
    public Announcement get(Long id) {
        // 根据ID查找公告
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null) {
            throw new RuntimeException("公告不存在");
        }
        return announcement;
    }
    
    /**
     * 获取公告列表
     * <p>
     * 查询所有公告信息
     * </p>
     * 
     * @return 公告列表
     */
    public List<Announcement> list() {
        return announcementMapper.selectList(null);
    }
    
    /**
     * 获取已发布的公告列表
     * <p>
     * 查询已发布的公告信息，并按创建时间降序排序
     * </p>
     * 
     * @return 已发布的公告列表
     */
    public List<Announcement> listPublished() {
        return announcementMapper.findByStatusOrderByCreateTimeDesc(1);
    }
}
