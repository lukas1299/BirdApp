package com.birdapp.BirdApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class BirdAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(BirdAppApplication.class, args);
	}

}
