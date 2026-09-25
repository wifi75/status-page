# Changelog

Formato basato su [Keep a Changelog](https://keepachangelog.com/it/1.1.0/), versioni secondo [SemVer](https://semver.org/lang/it/).

## [Non rilasciato]

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
