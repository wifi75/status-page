package eu.iu3cyv.status.monitor;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MonitorTypeTests {

    @Test
    void httpNeedsSchemeAndHost() {
        assertThat(MonitorType.HTTP.validateTarget("https://iu3cyv.eu")).isNull();
        assertThat(MonitorType.HTTP.validateTarget("iu3cyv.eu")).isNotNull();
        assertThat(MonitorType.HTTP.validateTarget("ftp://iu3cyv.eu")).isNotNull();
    }

    @Test
    void tcpNeedsValidPort() {
        assertThat(MonitorType.TCP.validateTarget("192.168.1.10:22")).isNull();
        assertThat(MonitorType.TCP.validateTarget("192.168.1.10")).isNotNull();
        assertThat(MonitorType.TCP.validateTarget("host:70000")).isNotNull();
    }

    @Test
    void certificatePortIsOptional() {
        assertThat(MonitorType.TLS_CERT.validateTarget("iu3cyv.eu")).isNull();
        assertThat(MonitorType.TLS_CERT.validateTarget("iu3cyv.eu:8443")).isNull();
        assertThat(MonitorType.TLS_CERT.validateTarget("https://iu3cyv.eu")).isNotNull();
    }

    @Test
    void unreachableTcpPortIsDown() {
        // porta 1 su localhost: chiusa, la connessione viene rifiutata subito
        var outcome = new Checker().check(MonitorType.TCP, "127.0.0.1:1");
        assertThat(outcome.status()).isEqualTo(MonitorStatus.DOWN);
    }
}
