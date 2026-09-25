package eu.iu3cyv.status;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@SpringBootApplication
@ConfigurationPropertiesScan
public class StatusPageApplication {

	public static void main(String[] args) throws IOException {
		// SQLite crea il file ma non la cartella che lo contiene
		var dbPath = Path.of(System.getenv().getOrDefault("DB_PATH", "./data/status.db")).toAbsolutePath();
		Files.createDirectories(dbPath.getParent());
		SpringApplication.run(StatusPageApplication.class, args);
	}

}
