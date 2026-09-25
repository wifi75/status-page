package eu.iu3cyv.status.monitor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class DashboardServiceTests {

    @Autowired
    private MonitorService monitorService;

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private MonitorRepository repository;

    @BeforeEach
    void clean() {
        repository.deleteAll();
    }

    @Test
    void checksFeedBarsUptimeAndSparkline() {
        var monitor = monitorService.create(new MonitorForm("Sito", MonitorType.HTTP, "https://esempio.it", 60, true, 0));
        var now = Instant.now();
        // tre controlli ok nelle ultime ore e uno fallito
        monitorService.recordCheck(monitor.getId(), CheckOutcome.up(100, "HTTP 200"), now.minus(Duration.ofHours(3)));
        monitorService.recordCheck(monitor.getId(), CheckOutcome.up(200, "HTTP 200"), now.minus(Duration.ofHours(2)));
        monitorService.recordCheck(monitor.getId(), CheckOutcome.down("timeout"), now.minus(Duration.ofMinutes(90)));
        monitorService.recordCheck(monitor.getId(), CheckOutcome.up(150, "HTTP 200"), now.minus(Duration.ofMinutes(1)));

        var card = dashboardService.build(now).services().getFirst();

        assertThat(card.uptime()).isEqualTo("75,00%");
        assertThat(card.bars()).hasSize(DashboardService.DAYS);
        assertThat(card.bars().getLast().cssClass()).isIn("down", "ok");
        assertThat(card.spark()).isNotNull();
        assertThat(card.status()).isEqualTo(MonitorStatus.UP);
        assertThat(dashboardService.build(now).hero().cssClass()).isEqualTo("is-ok");
    }

    @Test
    void expiringCertificateIsDegraded() {
        var soon = Instant.now().plus(Duration.ofDays(5));
        var outcome = CheckOutcome.withCertificate(50, "HTTP 200", soon, Checker.CERT_WARNING_DAYS);
        assertThat(outcome.status()).isEqualTo(MonitorStatus.DEGRADED);
        assertThat(outcome.certExpiresAt()).isEqualTo(soon);
    }
}
