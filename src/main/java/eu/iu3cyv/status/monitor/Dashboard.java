package eu.iu3cyv.status.monitor;

import java.util.List;

/**
 * Tutto ciò che mostra la pagina pubblica, già calcolato.
 * Non contiene mai target interni né messaggi d'errore.
 */
public record Dashboard(Hero hero, List<ServiceCard> services) {

    /** Riquadro dello stato complessivo. */
    public record Hero(String cssClass, String title, String subtitle, String uptime) {
    }

    public record ServiceCard(String name, String kind, MonitorStatus status, String responseMs,
                              String uptime, Integer certDays, boolean certWarning,
                              List<DayBar> bars, Spark spark) {
    }

    /** Una colonnina della barra dei 90 giorni. */
    public record DayBar(String cssClass, String tooltip) {
    }

    /** Percorsi SVG del grafico dei tempi di risposta (viewBox 160×40). */
    public record Spark(String line, String area, String max) {
    }
}
