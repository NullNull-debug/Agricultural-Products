package com.farmproduct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.mybatis.spring.annotation.MapperScan;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.config.GlobalConfig.DbConfig;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import javax.sql.DataSource;

/**
 * 应用主类
 * <p>
 * 该类是Spring Boot应用的入口点，负责启动应用并配置MyBatis-Plus相关组件
 * </p>
 */
@SpringBootApplication
@MapperScan("com.farmproduct.mapper") // 扫描Mapper接口所在包
public class Application {
    
    /**
     * 应用主入口方法
     * <p>
     * 启动Spring Boot应用，加载所有配置并初始化Spring容器
     * </p>
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
    /**
     * 配置MyBatis-Plus拦截器
     * <p>
     * 添加分页拦截器，支持H2数据库的分页查询
     * </p>
     * 
     * @return MyBatis-Plus拦截器实例
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加分页拦截器，指定数据库类型为H2
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.H2));
        return interceptor;
    }
    
    /**
     * 配置MyBatis-Plus全局配置
     * <p>
     * 设置主键生成策略为自动递增
     * </p>
     * 
     * @return 全局配置实例
     */
    @Bean
    public GlobalConfig globalConfig() {
        GlobalConfig globalConfig = new GlobalConfig();
        DbConfig dbConfig = new DbConfig();
        // 设置主键生成策略为自动递增
        dbConfig.setIdType(com.baomidou.mybatisplus.annotation.IdType.AUTO);
        globalConfig.setDbConfig(dbConfig);
        return globalConfig;
    }
    
    /**
     * 配置SqlSessionFactory
     * <p>
     * 设置数据源、Mapper文件位置、类型别名包和全局配置
     * </p>
     * 
     * @param dataSource 数据源实例
     * @return SqlSessionFactory实例
     * @throws Exception 配置过程中可能出现的异常
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean factoryBean = new MybatisSqlSessionFactoryBean();
        // 设置数据源
        factoryBean.setDataSource(dataSource);
        // 设置Mapper文件位置
        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml"));
        // 设置类型别名包
        factoryBean.setTypeAliasesPackage("com.farmproduct.entity");
        // 设置全局配置
        factoryBean.setGlobalConfig(globalConfig());
        return factoryBean.getObject();
    }
}