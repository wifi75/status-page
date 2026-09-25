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

    protected Monitor() {
    }

    public Monitor(String name, MonitorType type, String target) {
        this.name = name;
        this.type = type;
        this.target = target;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public MonitorType getType() { return type; }
    public String getTarget() { return target; }
    public int getIntervalSeconds() { return intervalSeconds; }
    public boolean isEnabled() { return enabled; }
    public int getDisplayOrder() { return displayOrder; }
    public Instant getCreatedAt() { return createdAt; }
}
