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


}
