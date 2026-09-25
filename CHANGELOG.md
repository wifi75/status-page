# Changelog

Formato basato su [Keep a Changelog](https://keepachangelog.com/it/1.1.0/), versioni secondo [SemVer](https://semver.org/lang/it/).

## [Non rilasciato]

### Modificato
- I test usano SQLite in memoria: niente più file `test-*.db` accumulati in `target/`.

## [0.4.0] - 2026-09-25

### Aggiunto
- Nuova grafica "Console radio": fondo scuro, spie luminose, valori in monospazio, variante chiara automatica. Design token in `static/css/tokens.css`, font Space Grotesk e JetBrains Mono self-hosted (licenza OFL).
- Barra dei 90 giorni per servizio con dettaglio al passaggio del mouse, disponibilità percentuale, grafico dei tempi di risposta delle ultime 24 ore.
- Giorni alla scadenza del certificato anche per i siti HTTPS; sotto i 14 giorni il servizio diventa "attenzione".
- Riepiloghi giornalieri (`daily_stat`, migrazione `V3`) e pulizia notturna dei controlli più vecchi di 2 giorni.
- Variabile `STATUS_BADGE` per l'etichetta accanto al titolo.
- Aggiornamento automatico della pagina pubblica ogni 60 secondi.

## [0.3.0] - 2026-09-25

### Aggiunto
- Area admin: aggiunta, modifica ed eliminazione dei servizi, con validazione dell'indirizzo in base al tipo.
- Controlli automatici su virtual thread: sito web (HTTP/HTTPS), porta TCP, scadenza del certificato TLS (degradato sotto i 14 giorni).
- Stato attuale e storico dei controlli (tabella `check_result`, migrazione `V2`).
- Pagina pubblica con spia per servizio, tempo di risposta e stato complessivo.
- Pagina di login in italiano e link "Accesso" nel piè di pagina.

## [0.2.0] - 2026-09-25

### Modificato
- Database passato da PostgreSQL a **SQLite** (file unico nel volume `/data`, modalità WAL): un solo container da gestire.
- Test senza Docker: ogni contesto usa un file SQLite nuovo in `target/`.
- La migrazione `V1` è stata riscritta per SQLite: nessuna installazione aveva ancora applicato la versione PostgreSQL.

### Rimosso
- Container PostgreSQL e Testcontainers.

## [0.1.0] - 2026-09-25

### Aggiunto
- Base del progetto Spring Boot 4.1.1 su Java 25 con PostgreSQL 18 e Flyway.
- Tabella `monitor` e pagina pubblica con l'elenco dei servizi.
- Spring Security: pagina pubblica, area `/admin` con login, tutto il resto negato, header di sicurezza.
- Dockerfile multi-stage (JRE, utente non privilegiato, healthcheck) e `docker-compose.yml` per Portainer.
- GitHub Actions: test con Testcontainers e pubblicazione dell'immagine su GHCR.
