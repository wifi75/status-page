package eu.iu3cyv.status.monitor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class MonitorService {

    private final MonitorRepository repository;
    private final CheckResultRepository checkResults;
    private final DailyStatRepository dailyStats;

    public MonitorService(MonitorRepository repository, CheckResultRepository checkResults,
                          DailyStatRepository dailyStats) {
        this.repository = repository;
        this.checkResults = checkResults;
        this.dailyStats = dailyStats;
    }

    /** Dati minimi per eseguire un controllo fuori transazione. */
    public record DueMonitor(Long id, String name, MonitorType type, String target) {
    }

    @Transactional(readOnly = true)
    public List<Monitor> publicMonitors() {
        return repository.findByEnabledTrueOrderByDisplayOrderAscNameAsc();
    }

    @Transactional(readOnly = true)
    public List<Monitor> allMonitors() {
        return repository.findAllByOrderByDisplayOrderAscNameAsc();
    }

    @Transactional(readOnly = true)
    public Monitor get(Long id) {
        return repository.findById(id).orElseThrow(() -> new NoSuchElementException("Servizio " + id + " non trovato"));
    }

    @Transactional
    public Monitor create(MonitorForm form) {
        var monitor = new Monitor(form.name().strip(), form.type(), form.target().strip());
        apply(monitor, form);
        return repository.save(monitor);
    }

    @Transactional
    public void update(Long id, MonitorForm form) {
        apply(get(id), form);
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(get(id));
    }

    @Transactional(readOnly = true)
    public List<DueMonitor> dueMonitors(Instant now) {
        return repository.findByEnabledTrueOrderByDisplayOrderAscNameAsc().stream()
                .filter(m -> m.isDue(now))
                .map(m -> new DueMonitor(m.getId(), m.getName(), m.getType(), m.getTarget()))
                .toList();
    }

    @Transactional
    public void recordCheck(Long monitorId, CheckOutcome outcome, Instant at) {
        // il servizio può essere stato eliminato mentre il controllo era in corso
        repository.findById(monitorId).ifPresent(monitor -> {
            monitor.recordCheck(outcome, at);
            checkResults.save(new CheckResult(monitorId, outcome, at));
            var day = LocalDate.ofInstant(at, ZoneId.systemDefault());
            var stat = dailyStats.findByMonitorIdAndDay(monitorId, day)
                    .orElseGet(() -> new DailyStat(monitorId, day));
            stat.count(outcome.status());
            dailyStats.save(stat);
        });
    }

    /** Il dettaglio serve solo per il grafico delle 24 ore; i riepiloghi giornalieri durano 90+ giorni. */
    @Transactional
    public void purgeOldData(Instant now) {
        checkResults.deleteOlderThan(now.minus(Duration.ofDays(2)));
        dailyStats.deleteOlderThan(LocalDate.ofInstant(now, ZoneId.systemDefault()).minusDays(100));
    }

    private static void apply(Monitor monitor, MonitorForm form) {
        monitor.update(form.name().strip(), form.type(), form.target().strip(),
                form.intervalSeconds(), form.isEnabled(), form.displayOrder());
    }
}
