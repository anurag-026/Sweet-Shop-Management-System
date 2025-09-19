package com.anurag.SweetShopBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SweetShopBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SweetShopBackendApplication.class, args);
	}

}
