package com.github.somprasongd.jasperreports.pdf.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    @Bean(name = "dataSourceOPD")
    @ConfigurationProperties(prefix = "spring.datasource.opd")
    public DataSource dataSourceOPD() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "dataSourceIPD")
    @ConfigurationProperties(prefix = "spring.datasource.ipd")
    public DataSource dataSourceIPD() {
        return DataSourceBuilder.create().build();
    }
}