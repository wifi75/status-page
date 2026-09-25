package eu.iu3cyv.status;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class StatusPageApplicationTests {

	@Test
	void contextLoads() {
	}

}
