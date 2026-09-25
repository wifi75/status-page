package eu.iu3cyv.status.monitor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;

/** Conteggio dei controlli di un servizio in un giorno (fuso del server). */
@Entity
@Table(name = "daily_stat")
public class DailyStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JdbcTypeCode(SqlTypes.INTEGER)
    private Long id;

    @Column(name = "monitor_id", nullable = false)
    @JdbcTypeCode(SqlTypes.INTEGER)
    private Long monitorId;

    @Column(nullable = false)
    private LocalDate day;

    @Column(name = "checks_total", nullable = false)
    private int total;

    @Column(name = "checks_up", nullable = false)
    private int up;

    @Column(name = "checks_degraded", nullable = false)
    private int degraded;

    @Column(name = "checks_down", nullable = false)
    private int down;

    protected DailyStat() {
    }

    DailyStat(Long monitorId, LocalDate day) {
        this.monitorId = monitorId;
        this.day = day;
    }

    void count(MonitorStatus status) {
        total++;
        switch (status) {
            case UP -> up++;
            case DEGRADED -> degraded++;
            case DOWN -> down++;
            case PENDING -> { }
        }
    }

    public Long getMonitorId() { return monitorId; }
    public LocalDate getDay() { return day; }
    public int getTotal() { return total; }
    public int getUp() { return up; }
    public int getDegraded() { return degraded; }
    public int getDown() { return down; }
}
