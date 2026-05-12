package com.farmproduct.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;

/**
 * CORS配置类
 * <p>
 * 配置跨域资源共享（CORS），允许前端应用与后端API进行跨域通信
 * </p>
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    /**
     * 配置CORS映射
     * <p>
     * 为/api/**路径配置CORS规则，允许所有来源、指定方法和请求头
     * </p>
     * 
     * @param registry CORS注册表
     */
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/api/**") // 配置/api/**路径的CORS规则
                .allowedOrigins("*") // 允许所有来源
                .allowedMethods("GET", "POST", "PUT", "DELETE") // 允许的HTTP方法
                .allowedHeaders("*") // 允许所有请求头
                .maxAge(3600); // 预检请求的缓存时间（秒）
    }
}