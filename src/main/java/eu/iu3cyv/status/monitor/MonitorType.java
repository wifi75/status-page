package eu.iu3cyv.status.monitor;

import java.net.URI;
import java.util.regex.Pattern;

public enum MonitorType {
    HTTP("Sito web (HTTP/HTTPS)", "https://esempio.it"),
    TCP("Porta TCP", "host:porta, es. 192.168.1.10:22"),
    TLS_CERT("Scadenza certificato TLS", "host o host:porta, es. esempio.it");

    private static final Pattern HOST_PORT = Pattern.compile("^[A-Za-z0-9.\\-]+:(\\d{1,5})$");
    private static final Pattern HOST_OPTIONAL_PORT = Pattern.compile("^[A-Za-z0-9.\\-]+(:(\\d{1,5}))?$");

    private final String label;
    private final String hint;

    MonitorType(String label, String hint) {
        this.label = label;
        this.hint = hint;
    }

    public String label() { return label; }
    public String hint() { return hint; }

    /** Verifica il formato del target; restituisce il messaggio d'errore o null se valido. */
    public String validateTarget(String target) {
        return switch (this) {
            case HTTP -> {
                try {
                    var uri = URI.create(target);
                    var scheme = uri.getScheme();
                    yield ("http".equals(scheme) || "https".equals(scheme)) && uri.getHost() != null
                            ? null : "Inserisci un indirizzo che inizia con http:// o https://";
                } catch (IllegalArgumentException e) {
                    yield "Indirizzo non valido";
                }
            }
            case TCP -> {
                var m = HOST_PORT.matcher(target);
                yield m.matches() && validPort(m.group(1)) ? null : "Formato atteso host:porta, es. 192.168.1.10:22";
            }
            case TLS_CERT -> {
                var m = HOST_OPTIONAL_PORT.matcher(target);
                yield m.matches() && (m.group(2) == null || validPort(m.group(2)))
                        ? null : "Formato atteso host oppure host:porta";
            }
        };
    }

    private static boolean validPort(String port) {
        int p = Integer.parseInt(port);
        return p >= 1 && p <= 65535;
    }
}
