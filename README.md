# Status Page

Pagina pubblica sullo stato dei servizi: controlla periodicamente siti e servizi (HTTP, porte TCP, scadenza dei certificati TLS), mostra lo storico e gli incidenti, e avvisa via Telegram ed email.

Istanza pubblica: <https://status.iu3cyv.eu>

> Stato: **fase 1** — base del progetto e catena di deploy. I controlli arrivano nella fase 2 (vedi [TODO.md](TODO.md)).

## Stack

| Componente | Versione |
|---|---|
| Java | 25 (LTS) |
| Spring Boot | 4.1.1 (Web MVC, Thymeleaf, Data JPA, Security, Validation, Actuator, Mail) |
| PostgreSQL | 18.6 |
| Migrazioni | Flyway |
| Test | JUnit 5 + Testcontainers |

## Sviluppo locale

Serve Java 25 e un PostgreSQL raggiungibile.

```bash
DB_PASSWORD=... ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Oppure, con Docker, un PostgreSQL usa e getta avviato da Testcontainers:

```bash
./mvnw spring-boot:test-run
```

Test (richiedono Docker):

```bash
./mvnw verify
```

## Deploy con Docker e Portainer

L'immagine viene costruita da GitHub Actions e pubblicata su `ghcr.io/wifi75/status-page`:

- push su `main` → tag `edge`
- tag `vX.Y.Z` → tag `X.Y.Z`, `X.Y` e `latest`

In Portainer: **Stacks → Add stack → Repository**, URL di questo repository, compose path `docker-compose.yml`, poi le variabili d'ambiente prese da [.env.example](.env.example). Per aggiornare: **Pull and redeploy** sullo stack (o il webhook dello stack).

L'app ascolta sulla porta `APP_PORT` (default 8085) del server; l'HTTPS pubblico è gestito dal reverse proxy.

### Password admin

La password non viene mai salvata in chiaro: si imposta il suo hash bcrypt in `ADMIN_PASSWORD_HASH`. Per generarlo:

```bash
docker run --rm httpd:alpine htpasswd -nbBC 10 "" 'la-tua-password' | tr -d ':\n' | sed 's/^\$2y/$2a/'
```

Se `ADMIN_PASSWORD_HASH` è vuoto l'area admin resta disabilitata.

## Sicurezza

- La pagina pubblica non espone mai URL o host interni dei servizi controllati.
- Header di sicurezza (CSP restrittiva, `X-Frame-Options: DENY`, Referrer-Policy).
- Tutto ciò che non è esplicitamente pubblico è negato; Actuator espone solo `health`.
- Container eseguito come utente non privilegiato.

## Licenza

[MIT](LICENSE)
