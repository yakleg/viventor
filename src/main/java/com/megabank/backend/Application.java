package com.megabank.backend;

import com.google.common.cache.CacheBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.guava.GuavaCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.concurrent.TimeUnit;

@SpringBootApplication(
		scanBasePackages = {
				"com.megabank.backend.configuration",
				"com.megabank.backend.service.**.aspect",
				"com.megabank.backend.service.**.dao",
				"com.megabank.backend.service.**.component",
				"com.megabank.backend.service.**.controller"
		},
		exclude = {
				RepositoryRestMvcAutoConfiguration.class
		}
)
@EnableAspectJAutoProxy
public class Application {
	/*
	ToDo: - Data validation
	 */

	@Bean
	public CacheManager cacheManager() {
		GuavaCacheManager guavaCacheManager = new GuavaCacheManager();
		guavaCacheManager.setCacheBuilder(CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.MINUTES));
		return guavaCacheManager;
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
