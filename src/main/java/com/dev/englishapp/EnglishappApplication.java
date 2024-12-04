package com.dev.englishapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;


@SpringBootApplication
public class EnglishappApplication {

	public static void main(String[] args) {

		Dotenv dotenv = Dotenv.configure().load();
		System.setProperty("OPENAI_URL", dotenv.get("OPENAI_URL"));
		System.setProperty("OPENAI_KEY", dotenv.get("OPENAI_KEY"));

		SpringApplication.run(EnglishappApplication.class, args);

		System.out.println("Hello English App");
	}

}

