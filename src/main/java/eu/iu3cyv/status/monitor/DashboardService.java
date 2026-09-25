package eu.iu3cyv.status.monitor;

import eu.iu3cyv.status.monitor.Dashboard.DayBar;
import eu.iu3cyv.status.monitor.Dashboard.Hero;
import eu.iu3cyv.status.monitor.Dashboard.ServiceCard;
import eu.iu3cyv.status.monitor.Dashboard.Spark;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/** Calcola la pagina pubblica: barre dei 90 giorni, disponibilità, grafico delle 24 ore. */
@Service
public class DashboardService {

    static final int DAYS = 90;
    private static final int SPARK_BUCKETS = 48;
    private static final DateTimeFormatter DAY_LABEL = DateTimeFormatter.ofPattern("d MMM", Locale.ITALIAN);

    private final MonitorService monitorService;
    private final DailyStatRepository dailyStats;
    private final CheckResultRepository checkResults;

    public DashboardService(MonitorService monitorService, DailyStatRepository dailyStats,
                            CheckResultRepository checkResults) {
        this.monitorService = monitorService;
        this.dailyStats = dailyStats;
        this.checkResults = checkResults;
    }

    @Transactional(readOnly = true)
    public Dashboard build(Instant now) {
        var zone = ZoneId.systemDefault();
        var today = LocalDate.ofInstant(now, zone);
        var monitors = monitorService.publicMonitors();
        var ids = monitors.stream().map(Monitor::getId).toList();

        Map<Long, Map<LocalDate, DailyStat>> statsByMonitor = ids.isEmpty() ? Map.of()
                : dailyStats.findByMonitorIdInAndDayGreaterThanEqual(ids, today.minusDays(DAYS - 1)).stream()
                .collect(Collectors.groupingBy(DailyStat::getMonitorId,
                        Collectors.toMap(DailyStat::getDay, s -> s)));
        Map<Long, List<CheckResult>> recentByMonitor = ids.isEmpty() ? Map.of()
                : checkResults.findByMonitorIdInAndCheckedAtAfterOrderByCheckedAt(ids, now.minus(Duration.ofHours(24)))
                .stream().collect(Collectors.groupingBy(CheckResult::getMonitorId));

        long allTotal = 0;
        long allAvailable = 0;
        var cards = new ArrayList<ServiceCard>();
        for (var m : monitors) {
            var days = statsByMonitor.getOrDefault(m.getId(), Map.of());
            long total = days.values().stream().mapToLong(DailyStat::getTotal).sum();
            long available = days.values().stream().mapToLong(s -> s.getUp() + s.getDegraded()).sum();
            allTotal += total;
            allAvailable += available;

            Integer certDays = m.getCertExpiresAt() == null ? null
                    : (int) Duration.between(now, m.getCertExpiresAt()).toDays();
            cards.add(new ServiceCard(
                    m.getName(),
                    m.getType().label(),
                    m.getLastStatus(),
                    m.getLastResponseMs() == null || m.getLastStatus() == MonitorStatus.DOWN ? "—" : m.getLastResponseMs() + " ms",
                    percent(available, total),
                    certDays,
                    certDays != null && certDays < Checker.CERT_WARNING_DAYS,
                    bars(days, today),
                    spark(recentByMonitor.getOrDefault(m.getId(), List.of()), now)));
        }
        return new Dashboard(hero(cards, percent(allAvailable, allTotal)), cards);
    }

    private static Hero hero(List<ServiceCard> cards, String uptime) {
        var count = cards.size() == 1 ? "1 servizio controllato" : cards.size() + " servizi controllati";
        if (cards.isEmpty()) {
            return new Hero("is-idle", "Nessun servizio configurato", "Aggiungili dall'area di accesso", uptime);
        }
        long down = cards.stream().filter(c -> c.status() == MonitorStatus.DOWN).count();
        if (down > 0) {
            var title = down == 1 ? "Un servizio non è raggiungibile" : down + " servizi non sono raggiungibili";
            return new Hero("is-down", title, count, uptime);
        }
        if (cards.stream().anyMatch(c -> c.status() == MonitorStatus.DEGRADED)) {
            return new Hero("is-warn", "Alcuni servizi richiedono attenzione", count, uptime);
        }
        if (cards.stream().allMatch(c -> c.status() == MonitorStatus.PENDING)) {
            return new Hero("is-idle", "In attesa dei primi controlli", count, uptime);
        }
        return new Hero("is-ok", "Tutti i sistemi operativi", count, uptime);
    }

    /** Un giorno con anche un solo controllo fallito è rosso; con un problema minore è giallo. */
    private static List<DayBar> bars(Map<LocalDate, DailyStat> days, LocalDate today) {
        var bars = new ArrayList<DayBar>(DAYS);
        for (int i = DAYS - 1; i >= 0; i--) {
            var day = today.minusDays(i);
            var label = DAY_LABEL.format(day);
            var s = days.get(day);
            if (s == null || s.getTotal() == 0) {
                bars.add(new DayBar("nodata", label + " · nessun dato"));
            } else if (s.getDown() > 0) {
                bars.add(new DayBar("down", label + " · " + percent(s.getUp() + s.getDegraded(), s.getTotal())
                        + " · " + s.getDown() + (s.getDown() == 1 ? " controllo fallito" : " controlli falliti")));
            } else if (s.getDegraded() > 0) {
                bars.add(new DayBar("warn", label + " · attenzione"));
            } else {
                bars.add(new DayBar("ok", label + " · nessun disservizio"));
            }
        }
        return bars;
    }

    /** Media dei tempi per mezz'ora nelle ultime 24 ore; null se i punti sono troppo pochi. */
    static Spark spark(List<CheckResult> results, Instant now) {
        var start = now.minus(Duration.ofHours(24));
        long bucketMs = Duration.ofHours(24).toMillis() / SPARK_BUCKETS;
        var sums = new long[SPARK_BUCKETS];
        var counts = new int[SPARK_BUCKETS];
        for (var r : results) {
            if (r.getResponseMs() == null) {
                continue;
            }
            int b = (int) Math.min(SPARK_BUCKETS - 1, Duration.between(start, r.getCheckedAt()).toMillis() / bucketMs);
            if (b >= 0) {
                sums[b] += r.getResponseMs();
                counts[b]++;
            }
        }
        var points = new ArrayList<double[]>();
        double max = 0;
        for (int b = 0; b < SPARK_BUCKETS; b++) {
            if (counts[b] > 0) {
                double avg = (double) sums[b] / counts[b];
                points.add(new double[]{b, avg});
                max = Math.max(max, avg);
            }
        }
        if (points.size() < 2) {
            return null;
        }
        double scale = max == 0 ? 0 : 34 / max;
        var line = new StringBuilder();
        for (var p : points) {
            double x = p[0] * 160.0 / (SPARK_BUCKETS - 1);
            double y = 38 - p[1] * scale;
            line.append(line.isEmpty() ? "M" : " L").append(fmt(x)).append(',').append(fmt(y));
        }
        double firstX = points.getFirst()[0] * 160.0 / (SPARK_BUCKETS - 1);
        double lastX = points.getLast()[0] * 160.0 / (SPARK_BUCKETS - 1);
        var area = line + " L" + fmt(lastX) + ",40 L" + fmt(firstX) + ",40 Z";
        return new Spark(line.toString(), area, Math.round(max) + " ms");
    }

    private static String percent(long part, long total) {
        if (total == 0) {
            return "—";
        }
        double p = 100.0 * part / total;
        return p == 100 ? "100%" : String.format(Locale.ITALIAN, "%.2f%%", Math.floor(p * 100) / 100);
    }

    private static String fmt(double v) {
        return String.format(Locale.ROOT, "%.1f", v);
    }
}
