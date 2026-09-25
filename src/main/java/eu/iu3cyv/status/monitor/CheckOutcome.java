package eu.iu3cyv.status.monitor;

import java.time.Instant;

/** Esito di un singolo controllo. certExpiresAt è valorizzato solo quando c'è TLS. */
public record CheckOutcome(MonitorStatus status, Integer responseMs, String message, Instant certExpiresAt) {

    public CheckOutcome {
        if (message != null && message.length() > 300) {
            message = message.substring(0, 300);
        }
    }

    static CheckOutcome up(long ms, String message) {
        return new CheckOutcome(MonitorStatus.UP, (int) ms, message, null);
    }

    static CheckOutcome down(String message) {
        return new CheckOutcome(MonitorStatus.DOWN, null, message, null);
    }

    /** Servizio raggiungibile con certificato: degradato se scade entro la soglia. */
    static CheckOutcome withCertificate(long ms, String message, Instant expiresAt, int warningDays) {
        long days = java.time.Duration.between(Instant.now(), expiresAt).toDays();
        var status = days < warningDays ? MonitorStatus.DEGRADED : MonitorStatus.UP;
        var text = message == null ? "certificato: " + days + " giorni" : message + " · certificato: " + days + " giorni";
        return new CheckOutcome(status, (int) ms, text, expiresAt);
    }
}
