CREATE TABLE monitor (
    id               INTEGER PRIMARY KEY AUTOINCREMENT,
    name             VARCHAR(100)  NOT NULL,
    type             VARCHAR(20)   NOT NULL CHECK (type IN ('HTTP', 'TCP', 'TLS_CERT')),
    target           VARCHAR(500)  NOT NULL,
    interval_seconds INTEGER       NOT NULL DEFAULT 60 CHECK (interval_seconds BETWEEN 30 AND 3600),
    enabled          BOOLEAN       NOT NULL DEFAULT 1,
    display_order    INTEGER       NOT NULL DEFAULT 0,
    created_at       TIMESTAMP     NOT NULL
);

CREATE INDEX monitor_enabled_order_idx ON monitor (enabled, display_order);
