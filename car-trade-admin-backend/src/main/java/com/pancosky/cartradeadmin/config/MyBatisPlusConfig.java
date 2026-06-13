package com.pancosky.cartradeadmin.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyBatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // Pagination plugin (PostgreSQL)
        PaginationInnerInterceptor paginationInner = new PaginationInnerInterceptor(DbType.POSTGRE_SQL);
        paginationInner.setMaxLimit(500L);
        paginationInner.setOverflow(false);
        interceptor.addInnerInterceptor(paginationInner);
        // Optimistic lock plugin
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return interceptor;
    }
}
