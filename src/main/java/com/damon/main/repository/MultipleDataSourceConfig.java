package com.damon.main.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * 该类作用：
 * 多数据源的配置
 *
 */
@Configuration
public class MultipleDataSourceConfig {
    @Bean(name = "primaryDataSource")
//    @Qualifier("primaryDataSource")不需要这个注解
    @Primary
    @ConfigurationProperties(prefix="spring.datasource.primary")
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create().build();
    }

/*    @Bean(name = "secondaryDataSource")
//    @Qualifier("secondaryDataSource")不需要这个注解
    @ConfigurationProperties(prefix="spring.datasource.secondary")
    public DataSource secondaryDataSource() {
        return DataSourceBuilder.create().build();
    }*/

}
