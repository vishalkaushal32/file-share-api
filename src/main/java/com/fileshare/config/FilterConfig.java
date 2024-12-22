package com.fileshare.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fileshare.filter.ApiKeyFilter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;

@Configuration
public class FilterConfig {

    public FilterRegistrationBean<OncePerRequestFilter> apiKeyFilter(ApiKeyFilter apiKeyFilter) {
        FilterRegistrationBean<OncePerRequestFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(apiKeyFilter);
        registrationBean.addUrlPatterns("/*"); // Apply to all endpoints or specific patterns
        return registrationBean;
    }
}
