package com.revseev.vkbotpoc;

import com.revseev.vkbotpoc.bot.BotServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class VkbotPocApplication {

	public static void main(String[] args) {
		SpringApplication.run(VkbotPocApplication.class, args);
	}

	@Bean(initMethod = "run")
	@PostConstruct
	public BotServer vkServer() {
		return new BotServer();
	}
}
