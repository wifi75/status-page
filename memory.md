# Memory

Decisioni di progetto e il loro perché.

- **Thymeleaf, non API + SPA**: una sola immagine da distribuire, pagina pubblica leggera e indicizzabile.
- **Deploy via Portainer da repository Git**: il compose è nel repo, i segreti solo nelle variabili dello stack.
- **Immagine su GHCR costruita dalla CI**: il server non compila nulla, scarica solo l'immagine.
- **Password admin come hash bcrypt in variabile d'ambiente**: nessuna tabella utenti per un solo amministratore; senza hash l'area admin è chiusa, mai password di default.
- **La pagina pubblica non mostra i target**: host e URL interni restano privati (`MonitorView` espone solo nome e tipo).
- **SQLite scelto da Tiziano** (25/09/2026) al posto di PostgreSQL: un solo container, backup = un file. Modalità WAL e pool di una connessione, perché SQLite ammette un solo scrittore.
- **ID come `INTEGER`** (`@JdbcTypeCode`): in SQLite l'autoincremento esiste solo su `INTEGER PRIMARY KEY`, mentre Hibernate si aspetterebbe `BIGINT` e la validazione dello schema fallirebbe.
- **Test su SQLite reale**, un file nuovo per contesto in `target/`: girano ovunque senza Docker.
