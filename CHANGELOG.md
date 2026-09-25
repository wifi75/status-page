# Changelog

Formato basato su [Keep a Changelog](https://keepachangelog.com/it/1.1.0/), versioni secondo [SemVer](https://semver.org/lang/it/).

## [Non rilasciato]

## [0.1.0] - 2026-09-25

### Aggiunto
- Base del progetto Spring Boot 4.1.1 su Java 25 con PostgreSQL 18 e Flyway.
- Tabella `monitor` e pagina pubblica con l'elenco dei servizi.
- Spring Security: pagina pubblica, area `/admin` con login, tutto il resto negato, header di sicurezza.
- Dockerfile multi-stage (JRE, utente non privilegiato, healthcheck) e `docker-compose.yml` per Portainer.
- GitHub Actions: test con Testcontainers e pubblicazione dell'immagine su GHCR.
