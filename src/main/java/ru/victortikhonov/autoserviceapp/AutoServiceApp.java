package ru.victortikhonov.autoserviceapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;


@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class AutoServiceApp {

	public static void main(String[] args) {
		SpringApplication.run(AutoServiceApp.class, args);
	}
}
