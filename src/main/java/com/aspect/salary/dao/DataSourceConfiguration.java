package com.aspect.salary.dao;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfiguration {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "app.datasource")
    public DataSourceProperties appDataSourceProperties(){
        return new DataSourceProperties();
    }

    @Bean(name="app_datasource")
    @Primary
    @ConfigurationProperties(prefix = "app.datasource.configuration")
    public HikariDataSource appDataSource() {
        return appDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }


    /*@Bean
    @ConfigurationProperties(prefix = "bitrix.datasource")
    public DataSourceProperties bitrixDataSourceProperties(){
        return new DataSourceProperties();
    }

    @Bean(name="bitrix_datasource")
    @ConfigurationProperties(prefix = "bitrix.datasource.configuration")
    public HikariDataSource bitrixDataSource(){
        return bitrixDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }
    */

    @Bean
    @ConfigurationProperties(prefix = "bitrix.datasource")
    public DataSourceProperties bitrixDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "dsBitrix")
    @ConfigurationProperties(prefix="bitrix.datasource.configuration")
    public HikariDataSource bitrixDataSource() {
        return bitrixDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean(name = "jdbcBitrix")
    @Autowired
    public JdbcTemplate bitrixJdbcTemplate(@Qualifier("dsBitrix") HikariDataSource dsBitrix) {
        return new JdbcTemplate(dsBitrix);
    }


}
