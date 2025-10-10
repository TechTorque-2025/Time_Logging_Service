package com.techtorque.time_logging_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
public class TimeLoggingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TimeLoggingServiceApplication.class, args);
	}

}
