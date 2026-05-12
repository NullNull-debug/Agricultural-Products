package com.farmproduct.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security配置类
 * <p>
 * 配置Spring Security的相关设置，包括密码编码器和安全过滤链
 * </p>
 */
@Configuration
@EnableWebSecurity // 启用Web安全
public class SecurityConfig {
    
    /**
     * 配置密码编码器
     * <p>
     * 使用BCryptPasswordEncoder作为密码编码器，用于密码加密和验证
     * </p>
     * 
     * @return 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * 配置安全过滤链
     * <p>
     * 配置HTTP请求的安全规则，包括禁用CSRF、设置请求授权规则
     * </p>
     * 
     * @param http HttpSecurity对象
     * @return 安全过滤链
     * @throws Exception 配置过程中可能出现的异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable() // 禁用CSRF保护
            .authorizeRequests() // 配置请求授权规则
            .requestMatchers("/api/**").permitAll() // 允许所有/api/**路径的请求
            .requestMatchers("/ws/**").permitAll() // 允许WebSocket请求
            .anyRequest().permitAll(); // 其他请求也允许访问（前端已做路由拦截）
        return http.build();
    }
}
