package com.company.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import static com.company.configuration.AppConfiguration.APP_PROPERTIES_LOCATION;
import static com.company.configuration.AppConfiguration.EN_MESSAGES_LOCATION;
import static com.company.configuration.AppConfiguration.PL_MESSAGES_LOCATION;
import static com.company.util.Mappings.DEFAULT_COMPONENT_SCAN_PACKAGE;

@Configuration
@ComponentScan(DEFAULT_COMPONENT_SCAN_PACKAGE)
@Import({HibernateConfiguration.class, SpringConfiguration.class, MultiSecurityConfiguration.class})
@PropertySource({APP_PROPERTIES_LOCATION, EN_MESSAGES_LOCATION, PL_MESSAGES_LOCATION})
public class AppConfiguration {
    static final String APP_PROPERTIES_LOCATION = "classpath:app.properties";
    static final String EN_MESSAGES_LOCATION = "classpath:/languages/messages_en.properties";
    static final String PL_MESSAGES_LOCATION = "classpath:/languages/messages_pl.properties";
}