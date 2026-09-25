# Status Page

Pagina pubblica sullo stato dei servizi: controlla periodicamente siti e servizi (HTTP, porte TCP, scadenza dei certificati TLS), mostra lo storico e gli incidenti, e avvisa via Telegram ed email.

Istanza pubblica: <https://status.iu3cyv.eu>

> Stato: servizi gestibili dall'area admin e controllati automaticamente. Storico a 90 giorni e notifiche in arrivo (vedi [TODO.md](TODO.md)).

## Come si aggiungono i servizi

1. Apri `/admin` (link **Accesso** in fondo alla pagina) ed entra con l'utente admin.
2. **+ Aggiungi servizio**: nome pubblico, tipo di controllo e indirizzo.
   - *Sito web*: `https://esempio.it`
   - *Porta TCP*: `192.168.1.10:22`
   - *Scadenza certificato TLS*: `esempio.it` (porta 443) o `esempio.it:8443`
3. Entro pochi secondi parte il primo controllo; poi si ripete all'intervallo scelto.

L'indirizzo resta privato: sulla pagina pubblica compaiono solo nome, stato e tempo di risposta.

## Stack

| Componente | Versione |
|---|---|
| Java | 25 (LTS) |
| Spring Boot | 4.1.1 (Web MVC, Thymeleaf, Data JPA, Security, Validation, Actuator, Mail) |
| Database | SQLite 3.53 (file unico, modalità WAL) |
| Migrazioni | Flyway |
| Test | JUnit 5 + MockMvc |
| Font | Space Grotesk, JetBrains Mono (SIL OFL 1.1, self-hosted) |

## Sviluppo locale

Serve solo Java 25: il database è un file SQLite creato in `./data/status.db`.

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Poi apri <http://localhost:8080>.

Test:

```bash
./mvnw verify
```

## Deploy con Docker e Portainer

L'immagine viene costruita da GitHub Actions e pubblicata su `ghcr.io/wifi75/status-page`:

- push su `main` → tag `edge`
- tag `vX.Y.Z` → tag `X.Y.Z`, `X.Y` e `latest`

In Portainer: **Stacks → Add stack → Repository**, URL di questo repository, compose path `docker-compose.yml`, poi le variabili d'ambiente prese da [.env.example](.env.example). Per aggiornare: **Pull and redeploy** sullo stack (o il webhook dello stack).

L'app ascolta sulla porta `APP_PORT` (default 8085) del server; l'HTTPS pubblico è gestito dal reverse proxy.

Il database è il file `/data/status.db` nel volume `status-data`: è l'unica cosa da includere nei backup.

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
