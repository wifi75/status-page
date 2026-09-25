# TODO

## Fase 1 — base e deploy
- [x] Progetto Spring Boot, database, pagina pubblica
- [x] Docker, compose per Portainer, CI con immagine su GHCR
- [ ] Stack su Portainer (192.168.1.207) e frontend HAProxy per status.iu3cyv.eu

## Fase 2 — controlli e storico
- [x] Controlli pianificati HTTP, TCP, scadenza certificato TLS (virtual thread)
- [x] Tabella risultati, stato attuale, tempi di risposta
- [x] Barra dei 90 giorni e percentuale di disponibilità
- [x] Pulizia automatica dei risultati vecchi

## Fase 3 — admin, incidenti, notifiche
- [x] CRUD servizi con validazione
- [ ] Incidenti con aggiornamenti pubblicati sulla pagina
- [ ] Notifiche Telegram ed email su cambio di stato
- [ ] Limite tentativi di login

## Fase 4 — rifiniture
- [ ] Feed RSS/Atom degli incidenti
- [ ] Badge SVG per servizio
- [ ] Rilascio v1.0.0
