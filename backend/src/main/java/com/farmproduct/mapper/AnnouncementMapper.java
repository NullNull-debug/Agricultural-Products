package com.farmproduct.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.farmproduct.entity.Announcement;

import java.util.List;

/**
 * 公告Mapper接口
 * <p>
 * 继承自BaseMapper，提供公告相关的数据库操作方法
 * </p>
 */
public interface AnnouncementMapper extends BaseMapper<Announcement> {
    /**
     * 根据状态查找公告并按创建时间降序排序
     * <p>
     * 通过公告状态查询公告列表，并按创建时间降序排序，用于展示最新公告
     * </p>
     * 
     * @param status 公告状态
     * @return 公告列表
     */
    List<Announcement> findByStatusOrderByCreateTimeDesc(Integer status);
}