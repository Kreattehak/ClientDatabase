package com.company.configuration;

import com.company.util.LocalizedMessages;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.mobile.device.DeviceResolver;
import org.springframework.mobile.device.DeviceResolverHandlerInterceptor;
import org.springframework.mobile.device.DeviceWebArgumentResolver;
import org.springframework.mobile.device.LiteDeviceResolver;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.ServletWebArgumentResolverAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.validation.Validator;
import java.util.List;
import java.util.Locale;

import static com.company.util.Mappings.ANY_SUBPATH;
import static com.company.util.Mappings.DEFAULT_ENCODING_VALUE;
import static com.company.util.Mappings.RESOLVER_PREFIX;
import static com.company.util.Mappings.RESOLVER_SUFFIX;
import static com.company.util.Mappings.RESOURCES;
import static com.company.util.Mappings.SLASH;

@Configuration
@EnableWebMvc
public class SpringConfiguration extends WebMvcConfigurerAdapter {

    private static final String DEFAULT_LANGUAGE = "en";
    private static final String LOCALE_COOKIE_NAME = "myLocaleCookie";
    private static final int LOCALE_COOKIE_MAX_AGE = 4800;
    private static final String I18_INTERCEPTOR_NAME = "language";
    private static final String I18_MESSAGE_SOURCE_BASENAME = "languages/messages";

    @Bean
    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix(RESOLVER_PREFIX);
        resolver.setSuffix(RESOLVER_SUFFIX);
        return resolver;
    }

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(I18_MESSAGE_SOURCE_BASENAME);
        messageSource.setDefaultEncoding(DEFAULT_ENCODING_VALUE);
        return messageSource;
    }

    @Bean
    public LocalizedMessages localizedMessages() {
        return new LocalizedMessages(messageSource());
    }

    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setDefaultLocale(new Locale(DEFAULT_LANGUAGE));
        resolver.setCookieName(LOCALE_COOKIE_NAME);
        resolver.setCookieMaxAge(LOCALE_COOKIE_MAX_AGE);
        return resolver;
    }

    @Bean
    public DeviceResolver deviceResolver(){
        return new LiteDeviceResolver();
    }

    @Bean
    public DeviceResolverHandlerInterceptor deviceResolverHandlerInterceptor() {
        return new DeviceResolverHandlerInterceptor();
    }

    @Bean
    public Validator validator() {
        LocalValidatorFactoryBean lvfb = new LocalValidatorFactoryBean();
        lvfb.setValidationMessageSource(messageSource());
        return lvfb;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName(I18_INTERCEPTOR_NAME);
        registry.addInterceptor(interceptor);

        registry.addInterceptor(deviceResolverHandlerInterceptor()).addPathPatterns("/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(RESOURCES + ANY_SUBPATH)
                .addResourceLocations(RESOURCES + SLASH);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new ServletWebArgumentResolverAdapter(new DeviceWebArgumentResolver()));
    }
}