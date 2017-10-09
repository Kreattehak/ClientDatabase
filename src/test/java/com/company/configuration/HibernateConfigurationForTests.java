package com.company.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

import static com.company.Constants.TEST_DB_URL_PROPERTY_NAME;
import static org.hibernate.cfg.AvailableSettings.DRIVER;
import static org.hibernate.cfg.AvailableSettings.PASS;
import static org.hibernate.cfg.AvailableSettings.USER;

@Configuration
@EnableTransactionManagement
public class HibernateConfigurationForTests {

    @Autowired
    private Environment env;

    @Bean
    @Profile("test")
    public DataSource getDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getRequiredProperty(DRIVER));
        dataSource.setUrl(env.getRequiredProperty(TEST_DB_URL_PROPERTY_NAME));
        dataSource.setUsername(env.getRequiredProperty(USER));
        dataSource.setPassword(env.getRequiredProperty(PASS));
        return dataSource;
    }
}