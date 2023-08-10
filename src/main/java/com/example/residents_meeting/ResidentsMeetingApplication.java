package com.example.residents_meeting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ResidentsMeetingApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResidentsMeetingApplication.class, args);
	}

}
