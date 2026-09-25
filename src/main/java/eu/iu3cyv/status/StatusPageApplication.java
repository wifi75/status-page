package eu.iu3cyv.status;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class StatusPageApplication {

	public static void main(String[] args) {
		SpringApplication.run(StatusPageApplication.class, args);
	}

}
