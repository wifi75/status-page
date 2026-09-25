CREATE TABLE monitor (
    id               BIGSERIAL PRIMARY KEY,
    name             VARCHAR(100)  NOT NULL,
    type             VARCHAR(20)   NOT NULL,
    target           VARCHAR(500)  NOT NULL,
    interval_seconds INTEGER       NOT NULL DEFAULT 60 CHECK (interval_seconds BETWEEN 30 AND 3600),
    enabled          BOOLEAN       NOT NULL DEFAULT TRUE,
    display_order    INTEGER       NOT NULL DEFAULT 0,
    created_at       TIMESTAMPTZ   NOT NULL DEFAULT now(),
    CONSTRAINT monitor_type_chk CHECK (type IN ('HTTP', 'TCP', 'TLS_CERT'))
);

CREATE INDEX monitor_enabled_order_idx ON monitor (enabled, display_order);
