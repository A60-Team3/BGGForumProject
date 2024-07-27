package com.example.bggforumproject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class TestConfig {

    @Bean
    public DataSource dataSource(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.mariadb.jdbc.Driver");
        dataSource.setUrl(System.getenv("dbUrl"));
        dataSource.setUsername(System.getenv("dbUsername"));
        dataSource.setPassword(System.getenv("dbPassword"));

        return dataSource;
    }
}
