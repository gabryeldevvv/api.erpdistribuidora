package com.api.erpdistribuidora;

import com.api.erpdistribuidora.config.SupabaseProps;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(SupabaseProps.class)
public class ErpdistribuidoraApplication {

	public static void main(String[] args) {
		SpringApplication.run(ErpdistribuidoraApplication.class, args);
	}

}
