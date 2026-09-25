package eu.iu3cyv.status.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

/**
 * Ogni 15 secondi controlla i servizi scaduti: la rete in parallelo su virtual thread,
 * le scritture in sequenza (SQLite ammette un solo scrittore).
 */
@Component
public class CheckScheduler {

    private static final Logger log = LoggerFactory.getLogger(CheckScheduler.class);

    private final MonitorService monitorService;
    private final Checker checker;

    public CheckScheduler(MonitorService monitorService, Checker checker) {
        this.monitorService = monitorService;
        this.checker = checker;
    }

    @Scheduled(fixedDelayString = "${status.scheduler-delay-ms:15000}", initialDelay = 3000)
    public void runDueChecks() {
        var due = monitorService.dueMonitors(Instant.now());
        if (due.isEmpty()) {
            return;
        }
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = due.stream()
                    .map(m -> CompletableFuture.supplyAsync(() -> checker.check(m.type(), m.target()), executor)
                            .thenApply(outcome -> new Done(m, outcome)))
                    .toList();
            for (var future : futures) {
                var done = future.join();
                monitorService.recordCheck(done.monitor().id(), done.outcome(), Instant.now());
                log.debug("{} → {} ({})", done.monitor().name(), done.outcome().status(), done.outcome().message());
            }
        }
    }

    @Scheduled(cron = "0 17 3 * * *")
    public void purge() {
        monitorService.purgeOldData(Instant.now());
    }

    private record Done(MonitorService.DueMonitor monitor, CheckOutcome outcome) {
    }
}
