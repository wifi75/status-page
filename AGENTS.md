# AGENTS.md

Istruzioni per chi lavora su questo repository (persone o agenti).

- Java 25, Spring Boot 4.1.1, Maven wrapper (`./mvnw`). Rispondere e documentare in italiano.
- Livelli separati: `monitor/` (dominio, repository, service), `web/` (controller), `config/`.
- DTO come `record`; injection via costruttore; logging con SLF4J.
- Schema database solo tramite Flyway (`src/main/resources/db/migration`), `ddl-auto: validate`. Mai modificare una migrazione già rilasciata: aggiungerne una nuova.
- Nessun segreto nel repository: configurazione via variabili d'ambiente, elencate in `.env.example`.
- La pagina pubblica non deve mai mostrare target interni dei servizi.
- Prima di un commit: `./mvnw verify` (richiede Docker per Testcontainers).
- Aggiornare `CHANGELOG.md` e `TODO.md` a ogni modifica rilevante; decisioni in `memory.md`.
