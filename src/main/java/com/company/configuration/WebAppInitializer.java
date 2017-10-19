package com.company.configuration;

import org.springframework.mobile.device.DeviceResolverRequestFilter;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.EnumSet;

import static com.company.util.Mappings.SLASH;

public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    private final String ANY_PATH = "/*";
    private final String GET_FILTER_NAME = "formGetMethodConvertingFilter";

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{AppConfiguration.class, SpringSecurityInitializer.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return null;
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{SLASH};
    }

    @Override
    protected Filter[] getServletFilters() {
        return new Filter[]{ new EncodingFilter(), new HiddenHttpMethodFilter(),
                new DeviceResolverRequestFilter() };
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        FilterRegistration.Dynamic encodingFilter = servletContext.addFilter(
                GET_FILTER_NAME, new FormGetMethodConvertingFilter());
        encodingFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.FORWARD), false, ANY_PATH);

        super.onStartup(servletContext);
    }
}
