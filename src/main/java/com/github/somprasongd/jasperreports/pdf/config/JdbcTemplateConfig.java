package com.github.somprasongd.jasperreports.pdf.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class JdbcTemplateConfig {

    @Bean(name = "jdbcTemplateOPD")
    public JdbcTemplate jdbcTemplateOPD(@Qualifier("dataSourceOPD") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "jdbcTemplateIPD")
    public JdbcTemplate jdbcTemplateIPD(@Qualifier("dataSourceIPD") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}