package com.company.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("com.company")
@Import({HibernateConfiguration.class, SpringConfiguration.class, SecurityConfiguration.class})
@PropertySource("classpath:app.properties")
public class AppConfiguration {
}