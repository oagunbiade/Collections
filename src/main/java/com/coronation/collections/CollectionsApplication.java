package com.coronation.collections;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;

/**
 * @author Olatunbosun
 *
 */
@SpringBootApplication
@EnableScheduling
public class CollectionsApplication {
	public static void main(String[] args) {
		SpringApplication.run(CollectionsApplication.class, args);
	}
}
