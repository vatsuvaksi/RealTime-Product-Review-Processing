package com.vatsuvaksi.springbatchdemo.configurations;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class HelperBeans {

    @Bean
    public WebClient webClientBuilder() {
        return WebClient.builder().build();
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        HikariConfig config = new HikariConfig();

        // Set PostgreSQL specific properties
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/batchdb");
        config.setUsername("batchuser");
        config.setPassword("batchpassword");
        config.setDriverClassName("org.postgresql.Driver");
        config.setMaximumPoolSize(10);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);


        return new DataSourceTransactionManager(
                new HikariDataSource(config)
        );
    }

}
