package com.farmproduct.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.farmproduct.entity.Message;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {
}