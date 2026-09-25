package eu.iu3cyv.status.monitor;

/**
 * Dati di un servizio mostrati sulla pagina pubblica.
 * Il target (URL/host interno) non viene mai esposto.
 */
public record MonitorView(Long id, String name, MonitorType type) {

    static MonitorView of(Monitor monitor) {
        return new MonitorView(monitor.getId(), monitor.getName(), monitor.getType());
    }
}
