package eu.iu3cyv.status.monitor;

import org.springframework.stereotype.Component;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/** Esegue i controlli di rete. Nessun accesso al database: gira in parallelo. */
@Component
public class Checker {

    static final Duration TIMEOUT = Duration.ofSeconds(10);
    /** Sotto questa soglia il certificato è "degradato". */
    static final int CERT_WARNING_DAYS = 14;

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(TIMEOUT)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    public CheckOutcome check(MonitorType type, String target) {
        try {
            return switch (type) {
                case HTTP -> checkHttp(target);
                case TCP -> checkTcp(target);
                case TLS_CERT -> checkCertificate(target);
            };
        } catch (Exception e) {
            return CheckOutcome.down(describe(e));
        }
    }

    private CheckOutcome checkHttp(String target) throws Exception {
        var request = HttpRequest.newBuilder(URI.create(target))
                .timeout(TIMEOUT)
                .header("User-Agent", "status-page (+https://github.com/wifi75/status-page)")
                .GET()
                .build();
        long start = System.nanoTime();
        var response = http.send(request, HttpResponse.BodyHandlers.discarding());
        long ms = elapsedMs(start);
        int code = response.statusCode();
        return code < 400 ? CheckOutcome.up(ms, "HTTP " + code) : CheckOutcome.down("HTTP " + code);
    }

    private CheckOutcome checkTcp(String target) throws Exception {
        var address = parse(target, -1);
        long start = System.nanoTime();
        try (var socket = new Socket()) {
            socket.connect(address, (int) TIMEOUT.toMillis());
        }
        return CheckOutcome.up(elapsedMs(start), "porta aperta");
    }

    private CheckOutcome checkCertificate(String target) throws Exception {
        var address = parse(target, 443);
        long start = System.nanoTime();
        try (var raw = new Socket()) {
            raw.connect(address, (int) TIMEOUT.toMillis());
            raw.setSoTimeout((int) TIMEOUT.toMillis());
            var factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            try (var ssl = (SSLSocket) factory.createSocket(raw, address.getHostString(), address.getPort(), true)) {
                // l'handshake fallisce già se il certificato è scaduto o non valido
                ssl.startHandshake();
                long ms = elapsedMs(start);
                var cert = (X509Certificate) ssl.getSession().getPeerCertificates()[0];
                long days = ChronoUnit.DAYS.between(Instant.now(), cert.getNotAfter().toInstant());
                var message = "scade tra " + days + " giorni";
                return days < CERT_WARNING_DAYS ? CheckOutcome.degraded(ms, message) : CheckOutcome.up(ms, message);
            }
        }
    }

    private static InetSocketAddress parse(String target, int defaultPort) {
        int colon = target.lastIndexOf(':');
        if (colon < 0) {
            return new InetSocketAddress(target, defaultPort);
        }
        return new InetSocketAddress(target.substring(0, colon), Integer.parseInt(target.substring(colon + 1)));
    }

    private static long elapsedMs(long startNanos) {
        return Duration.ofNanos(System.nanoTime() - startNanos).toMillis();
    }

    private static String describe(Exception e) {
        return switch (e) {
            case java.net.http.HttpTimeoutException t -> "timeout";
            case java.net.SocketTimeoutException t -> "timeout";
            case java.net.UnknownHostException u -> "host sconosciuto";
            case java.net.ConnectException c -> "connessione rifiutata";
            case javax.net.ssl.SSLHandshakeException s -> "certificato non valido o scaduto";
            default -> e.getClass().getSimpleName();
        };
    }
}
