-- stato attuale, aggiornato a ogni controllo
ALTER TABLE monitor ADD COLUMN last_status VARCHAR(20) NOT NULL DEFAULT 'PENDING'
    CHECK (last_status IN ('PENDING', 'UP', 'DEGRADED', 'DOWN'));
ALTER TABLE monitor ADD COLUMN last_checked_at TIMESTAMP;
ALTER TABLE monitor ADD COLUMN last_response_ms INTEGER;
ALTER TABLE monitor ADD COLUMN last_message VARCHAR(300);

-- storico dei controlli, base per la barra dei 90 giorni
CREATE TABLE check_result (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    monitor_id  INTEGER      NOT NULL REFERENCES monitor (id) ON DELETE CASCADE,
    checked_at  TIMESTAMP    NOT NULL,
    status      VARCHAR(20)  NOT NULL CHECK (status IN ('UP', 'DEGRADED', 'DOWN')),
    response_ms INTEGER,
    message     VARCHAR(300)
);

CREATE INDEX check_result_monitor_time_idx ON check_result (monitor_id, checked_at);
