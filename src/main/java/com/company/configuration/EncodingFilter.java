package com.company.configuration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

import static com.company.util.Mappings.DEFAULT_ENCODING_VALUE;

public class EncodingFilter implements Filter {

    private String encoding = DEFAULT_ENCODING_VALUE;

    @Override
    public void destroy() {
        //do nothing
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        request.setCharacterEncoding(encoding);
        response.setCharacterEncoding(encoding);
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
        String encodingParameter = "encoding";
        if (config.getInitParameter(encodingParameter) != null) {
            encoding = config.getInitParameter(encodingParameter);
        }
    }
}