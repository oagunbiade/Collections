package com.coronation.collections;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Olatunbosun
 *
 */
@SpringBootApplication
@EnableScheduling
public class CollectionsApplication {
	
	/*
	 * @SuppressWarnings("deprecation")
	 * @Bean public SessionFactory sessionFactory(HibernateEntityManagerFactory
	 * hemf){ return hemf.getSessionFactory(); }
	 */
	
	public static void main(String[] args) {
		SpringApplication.run(CollectionsApplication.class, args);
	}

}
