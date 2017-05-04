package com.company.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

@Component
public class LoggerInjector implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessBeforeInitialization(final Object bean, String beanName) throws BeansException {
        ReflectionUtils.doWithLocalFields(bean.getClass(), field -> {
            ReflectionUtils.makeAccessible(field);
            if (field.getAnnotation(InjectLogger.class) != null) {
                Logger logger = LogManager.getLogger(field.getAnnotation(InjectLogger.class).value());
                field.set(bean, logger);
            }
        });
        return bean;
    }
}