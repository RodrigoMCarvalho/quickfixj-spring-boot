package com.rodrigo.testefix;

import io.allune.quickfixj.spring.boot.starter.EnableQuickFixJClient;
import io.allune.quickfixj.spring.boot.starter.EnableQuickFixJServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import quickfix.*;

@EnableQuickFixJServer
@EnableQuickFixJClient
@SpringBootApplication
public class TesteFixApplication {

	public static void main(String[] args) {
		SpringApplication.run(TesteFixApplication.class, args);
	}

	@Bean
	public Application clientApplication() {
		return new ApplicationAdapter();
	}

	@Bean
	public Initiator clientInitiator(
			quickfix.Application clientApplication,
			MessageStoreFactory clientMessageStoreFactory,
			SessionSettings clientSessionSettings,
			LogFactory clientLogFactory,
			MessageFactory clientMessageFactory
	) throws ConfigError {

		return new ThreadedSocketInitiator(
				clientApplication,
				clientMessageStoreFactory,
				clientSessionSettings,
				clientLogFactory,
				clientMessageFactory
		);
	}

}
