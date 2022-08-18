package com.rodrigo.testefix;

import io.allune.quickfixj.spring.boot.starter.EnableQuickFixJClient;
import io.allune.quickfixj.spring.boot.starter.EnableQuickFixJServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableQuickFixJServer
@EnableQuickFixJClient
@SpringBootApplication
public class TesteFixApplication {

	public static void main(String[] args) {
		SpringApplication.run(TesteFixApplication.class, args);
	}

}
