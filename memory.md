# Memory

Decisioni di progetto e il loro perché.

- **Thymeleaf, non API + SPA**: una sola immagine da distribuire, pagina pubblica leggera e indicizzabile.
- **Deploy via Portainer da repository Git**: il compose è nel repo, i segreti solo nelle variabili dello stack.
- **Immagine su GHCR costruita dalla CI**: il server non compila nulla, scarica solo l'immagine.
- **Password admin come hash bcrypt in variabile d'ambiente**: nessuna tabella utenti per un solo amministratore; senza hash l'area admin è chiusa, mai password di default.
- **La pagina pubblica non mostra i target**: host e URL interni restano privati (`MonitorView` espone solo nome e tipo).
- **PostgreSQL 18**: il volume si monta su `/var/lib/postgresql`, non più su `/var/lib/postgresql/data`.
- **Test solo con database reale** (Testcontainers): girano in CI; in locale serve Docker.
