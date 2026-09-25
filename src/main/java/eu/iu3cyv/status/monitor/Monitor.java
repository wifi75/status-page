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
@Table(name = "monitor")
public class Monitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // in SQLite l'autoincremento esiste solo su INTEGER PRIMARY KEY
    @JdbcTypeCode(SqlTypes.INTEGER)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MonitorType type;

    @Column(nullable = false, length = 500)
    private String target;

    @Column(name = "interval_seconds", nullable = false)
    private int intervalSeconds = 60;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "last_status", nullable = false, length = 20)
    private MonitorStatus lastStatus = MonitorStatus.PENDING;

    @Column(name = "last_checked_at")
    private Instant lastCheckedAt;

    @Column(name = "last_response_ms")
    private Integer lastResponseMs;

    @Column(name = "last_message", length = 300)
    private String lastMessage;

    protected Monitor() {
    }

    public Monitor(String name, MonitorType type, String target) {
        this.name = name;
        this.type = type;
        this.target = target;
        this.createdAt = Instant.now();
    }

    /** Applica i dati del modulo admin; un nuovo target riparte da "in attesa". */
    void update(String name, MonitorType type, String target, int intervalSeconds, boolean enabled, int displayOrder) {
        if (this.type != type || !target.equals(this.target)) {
            this.lastStatus = MonitorStatus.PENDING;
            this.lastCheckedAt = null;
            this.lastResponseMs = null;
            this.lastMessage = null;
        }
        this.name = name;
        this.type = type;
        this.target = target;
        this.intervalSeconds = intervalSeconds;
        this.enabled = enabled;
        this.displayOrder = displayOrder;
    }

    void recordCheck(CheckOutcome outcome, Instant at) {
        this.lastStatus = outcome.status();
        this.lastCheckedAt = at;
        this.lastResponseMs = outcome.responseMs();
        this.lastMessage = outcome.message();
    }

    boolean isDue(Instant now) {
        return lastCheckedAt == null || !lastCheckedAt.plusSeconds(intervalSeconds).isAfter(now);
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public MonitorType getType() { return type; }
    public String getTarget() { return target; }
    public int getIntervalSeconds() { return intervalSeconds; }
    public boolean isEnabled() { return enabled; }
    public int getDisplayOrder() { return displayOrder; }
    public Instant getCreatedAt() { return createdAt; }
    public MonitorStatus getLastStatus() { return lastStatus; }
    public Instant getLastCheckedAt() { return lastCheckedAt; }
    public Integer getLastResponseMs() { return lastResponseMs; }
    public String getLastMessage() { return lastMessage; }
}
