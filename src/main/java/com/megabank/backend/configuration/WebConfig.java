package com.megabank.backend.configuration;

import com.megabank.backend.service.rest.component.LoggerHandlerInterceptorAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {


	@Bean
	LoggerHandlerInterceptorAdapter getLoggerHandlerInterceptorAdapter() {
		return new LoggerHandlerInterceptorAdapter();
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(getLoggerHandlerInterceptorAdapter());
	}
}
