package eu.iu3cyv.status.monitor;

/** Esito di un singolo controllo. */
public record CheckOutcome(MonitorStatus status, Integer responseMs, String message) {

    public CheckOutcome {
        if (message != null && message.length() > 300) {
            message = message.substring(0, 300);
        }
    }

    static CheckOutcome up(long ms, String message) {
        return new CheckOutcome(MonitorStatus.UP, (int) ms, message);
    }

    static CheckOutcome degraded(long ms, String message) {
        return new CheckOutcome(MonitorStatus.DEGRADED, (int) ms, message);
    }

    static CheckOutcome down(String message) {
        return new CheckOutcome(MonitorStatus.DOWN, null, message);
    }
}
