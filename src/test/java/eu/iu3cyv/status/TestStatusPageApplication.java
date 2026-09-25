package eu.iu3cyv.status;

import org.springframework.boot.SpringApplication;

public class TestStatusPageApplication {

	public static void main(String[] args) {
		SpringApplication.from(StatusPageApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
