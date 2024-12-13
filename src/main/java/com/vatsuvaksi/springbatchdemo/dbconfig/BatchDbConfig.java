package com.vatsuvaksi.springbatchdemo.dbconfig;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.batch.BatchDataSource;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "entityManagerFactoryBatchReadWrite",
        transactionManagerRef = "transactionManager", basePackages = {
        "com.vatsuvaksi.springbatchdemo.repository"})
public class BatchDbConfig {

    private final String connectionTestQuery = "SELECT 1";

    @Bean("dataSource")
    @BatchDataSource
    @Primary
    public HikariDataSource dataSourceBatchReadWrite() {

        HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:postgresql://localhost:5432/batchdb");
        config.setUsername("batchuser");
        config.setPassword("batchpassword");
        config.setDriverClassName("org.postgresql.Driver");
        config.setMaximumPoolSize(10);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);

        return new HikariDataSource(config);
    }

    @Bean("entityManagerFactoryBatchReadWrite")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBatchReadWrite(
            @Qualifier("dataSource") HikariDataSource dataSourceReadWrite, EntityManagerFactoryBuilder builder) {
        return builder.dataSource(dataSourceReadWrite).packages("com.vatsuvaksi.springbatchdemo.entities").persistenceUnit("batchPersistenceUnit").build();
    }

    @Bean("transactionManager")
    public PlatformTransactionManager transactionManagerBatchReadWrite(
            @Qualifier("entityManagerFactoryBatchReadWrite") EntityManagerFactory entityManagerFactoryReadWrite) {
        return new JpaTransactionManager(entityManagerFactoryReadWrite);
    }
}
