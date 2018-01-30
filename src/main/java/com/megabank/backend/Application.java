package com.megabank.backend;

import com.google.common.cache.CacheBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.guava.GuavaCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableAutoConfiguration(exclude = RepositoryRestMvcAutoConfiguration.class)
@EnableJpaRepositories
@EnableJpaAuditing
public class Application {
	/*
	ToDo: - Data validation
	 */

	@Bean
	public CacheManager cacheManager() {
		GuavaCacheManager guavaCacheManager =  new GuavaCacheManager();
		guavaCacheManager.setCacheBuilder(CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.MINUTES));
		return guavaCacheManager;
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
