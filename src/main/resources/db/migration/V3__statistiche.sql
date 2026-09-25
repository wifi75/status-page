-- scadenza del certificato, rilevata dai controlli HTTPS e TLS
ALTER TABLE monitor ADD COLUMN cert_expires_at TIMESTAMP;

-- riepilogo giornaliero: alimenta le barre dei 90 giorni senza scorrere tutti i controlli
CREATE TABLE daily_stat (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    monitor_id      INTEGER     NOT NULL REFERENCES monitor (id) ON DELETE CASCADE,
    day             DATE        NOT NULL,
    checks_total    INTEGER     NOT NULL DEFAULT 0,
    checks_up       INTEGER     NOT NULL DEFAULT 0,
    checks_degraded INTEGER     NOT NULL DEFAULT 0,
    checks_down     INTEGER     NOT NULL DEFAULT 0,
    CONSTRAINT daily_stat_monitor_day_uq UNIQUE (monitor_id, day)
);
