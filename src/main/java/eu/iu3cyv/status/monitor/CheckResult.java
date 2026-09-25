package eu.iu3cyv.status.monitor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Entity
@Table(name = "check_result")
public class CheckResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JdbcTypeCode(SqlTypes.INTEGER)
    private Long id;

    @Column(name = "monitor_id", nullable = false)
    @JdbcTypeCode(SqlTypes.INTEGER)
    private Long monitorId;

    @Column(name = "checked_at", nullable = false)
    private Instant checkedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MonitorStatus status;

    @Column(name = "response_ms")
    private Integer responseMs;

    @Column(length = 300)
    private String message;

    protected CheckResult() {
    }

    CheckResult(Long monitorId, CheckOutcome outcome, Instant checkedAt) {
        this.monitorId = monitorId;
        this.checkedAt = checkedAt;
        this.status = outcome.status();
        this.responseMs = outcome.responseMs();
        this.message = outcome.message();
    }
}
