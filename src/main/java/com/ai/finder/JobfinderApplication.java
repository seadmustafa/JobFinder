package com.ai.jobfinder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JobfinderApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobfinderApplication.class, args);
	}

}
