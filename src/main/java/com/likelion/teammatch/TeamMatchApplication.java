package com.likelion.teammatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TeamMatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(TeamMatchApplication.class, args);
	}

}
