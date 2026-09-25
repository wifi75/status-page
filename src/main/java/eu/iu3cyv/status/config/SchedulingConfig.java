package eu.iu3cyv.status.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/** Controlli pianificati; spenti nei test con status.scheduler-enabled=false. */
@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "status.scheduler-enabled", havingValue = "true", matchIfMissing = true)
public class SchedulingConfig {
}
