package com.company.configuration;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

import static com.company.util.Mappings.DEFAULT_COMPONENT_SCAN_PACKAGE;
import static org.hibernate.cfg.AvailableSettings.*;

@Configuration
@EnableTransactionManagement
public class HibernateConfiguration {

    private final String MODEL_PACKAGE = ".model";
    private final Environment env;

    @Autowired
    public HibernateConfiguration(Environment env) {
        this.env = env;
    }

    @Bean
    @Profile({"dev", "default"})
    public DataSource getDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getRequiredProperty(DRIVER));
        dataSource.setUrl(env.getRequiredProperty(URL));
        dataSource.setUsername(env.getRequiredProperty(USER));
        dataSource.setPassword(env.getRequiredProperty(PASS));
        return dataSource;
    }

    @Bean
    public LocalSessionFactoryBean getSessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(getDataSource());
        sessionFactory.setPackagesToScan(DEFAULT_COMPONENT_SCAN_PACKAGE + MODEL_PACKAGE);
        sessionFactory.setHibernateProperties(getHibernateProperties());
        return sessionFactory;
    }

    @Bean
    public HibernateTransactionManager getTransactionManager(SessionFactory sessionFactory) {
        HibernateTransactionManager txManager = new HibernateTransactionManager();
        txManager.setSessionFactory(sessionFactory);
        return txManager;
    }

    private Properties getHibernateProperties() {
        Properties properties = new Properties();
        properties.put(DIALECT, env.getRequiredProperty(DIALECT));
        properties.put(SHOW_SQL, env.getRequiredProperty(SHOW_SQL));
        properties.put(FORMAT_SQL, env.getRequiredProperty(FORMAT_SQL));
        properties.put(STATEMENT_BATCH_SIZE, env.getRequiredProperty(STATEMENT_BATCH_SIZE));
        properties.put(HBM2DDL_AUTO, env.getRequiredProperty(HBM2DDL_AUTO));
        return properties;
    }
}