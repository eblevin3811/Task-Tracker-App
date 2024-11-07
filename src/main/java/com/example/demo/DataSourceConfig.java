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
        dsb.url(**URL**);
        dsb.username(**USERNAME**);
        dsb.password(**PASSWORD**);
        return dsb.build();
    }
}
