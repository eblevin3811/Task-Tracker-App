package com.example.demo;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    @Bean
    public DataSource getDataSource(){
        DataSourceBuilder dsb= DataSourceBuilder.create();
        dsb.driverClassName("com.mysql.cj.jdbc.Driver");
        dsb.url("jdbc:mysql://admin@tododb.cluster-ck5cshs9f6iu.us-east-1.rds.amazonaws.com:3306/tododb");
        dsb.username("admin");
        dsb.password("CS321proj");
        return dsb.build();
    }
}
