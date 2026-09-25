package eu.iu3cyv.status.monitor;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Dati di un servizio mostrati sulla pagina pubblica.
 * Il target (URL/host interno) e i messaggi d'errore non vengono mai esposti.
 */
public record MonitorView(Long id, String name, MonitorType type, MonitorStatus status,
                          Integer responseMs, Instant checkedAt) {

    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("dd/MM HH:mm:ss");

    /** Ora dell'ultimo controllo nel fuso del server (TZ). */
    public String checkedAtText() {
        return checkedAt == null ? "" : TIME.format(checkedAt.atZone(ZoneId.systemDefault()));
    }

    static MonitorView of(Monitor monitor) {
        return new MonitorView(monitor.getId(), monitor.getName(), monitor.getType(),
                monitor.getLastStatus(), monitor.getLastResponseMs(), monitor.getLastCheckedAt());
    }
}
