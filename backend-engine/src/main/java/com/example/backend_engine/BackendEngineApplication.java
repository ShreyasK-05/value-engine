package com.example.backend_engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
@EnableCaching
public class BackendEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendEngineApplication.class, args);
	}

}
