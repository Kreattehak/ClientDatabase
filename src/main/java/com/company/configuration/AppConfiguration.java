package com.company.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(value={"com.company"})
@Import({HibernateConfiguration.class, SpringConfiguration.class})
public class AppConfiguration {

}