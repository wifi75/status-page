package eu.iu3cyv.status.monitor;

public enum MonitorStatus {
    /** Mai controllato. */
    PENDING("in attesa", "is-idle"),
    UP("operativo", "is-ok"),
    /** Risponde, ma con un problema (es. certificato in scadenza). */
    DEGRADED("degradato", "is-warn"),
    DOWN("non raggiungibile", "is-down");

    private final String label;
    private final String cssClass;

    MonitorStatus(String label, String cssClass) {
        this.label = label;
        this.cssClass = cssClass;
    }

    public String label() { return label; }
    public String cssClass() { return cssClass; }
}
