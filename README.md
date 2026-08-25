# Il mio studio

App Android (Kotlin + Jetpack Compose) per la gestione del lavoro di uno
studio privato (psicologa/psicologo): sedute pagate, spese di studio,
scadenze, riempimento dell'agenda, compenso orario reale e persone seguite.

Sviluppata da **Giovanni Bedocchi** — giovanni@bedocchi.it

## Schermate

Navigazione a cinque sezioni con selettore a pillola in alto (senza barra di
navigazione inferiore), un unico mese selezionato condiviso da tutte le
schermate, palette calda (crema, petrolio, teal, corallo, senape, salvia,
blu) e font Nunito/Inter:

- **Il mese**: incassato del mese con il "nastro" — la fascia colorata che
  mostra come si divide ogni euro —, le cinque voci di ripartizione (tasse,
  spese di studio, stipendio, fondo sicurezza, futuro), le spese di studio
  con barra rispetto alla quota prevista, i mesi di autonomia con indicatore
  a tacche, le prossime scadenze e l'andamento degli incassi sui 12 mesi
  dell'anno con media tratteggiata.
- **Movimenti**: un unico form scuro con toggle Sedute incassate/Spese di
  studio — data, importo (con scorciatoia per la tariffa abituale), paziente
  o descrizione, tipo di seduta o categoria — ed elenco del mese selezionato.
- **Studio**: quanto è pieno il mese (sedute su capacità settimanale),
  tariffa nominale vs compenso orario reale (al netto delle ore non
  fatturabili) e sedute per tipo.
- **Persone**: le persone seguite nell'anno, con totale, numero di sedute,
  ultima visita e un avviso quando una persona pesa più del 30% degli
  incassi dell'anno.
- **Regole**: percentuali di divisione dell'incasso (devono sommare 100%),
  parametri dello studio (tariffa, sedute a settimana, ore non fatturabili),
  dati personali, scadenze (aggiungi/elimina), le "tre medie" dell'anno,
  l'esportazione/importazione dei dati e le informazioni sull'app e sullo
  sviluppatore.

## Import/export

Incassi e spese si esportano e si importano con lo stesso schema a colonne
(tipo, data, voce, dettaglio, importo) in tre formati intercambiabili,
raggiungibili dalla scheda Regole:

- **CSV** — separatore `;`, compatibile con Excel e Fogli Google.
- **Excel (.xlsx)** — scritto e letto senza librerie esterne (il formato
  OOXML è generato/analizzato direttamente come zip di XML).
- **TXT** — stesso schema, separatore tabulazione.

L'importazione riconosce automaticamente il formato dall'estensione del file
scelto e aggiunge le righe a quelle già presenti.

## Stack tecnico

- Kotlin, Jetpack Compose (Material 3), font Nunito/Inter (Google Fonts)
- Room (incassi, spese, scadenze)
- DataStore Preferences (percentuali di ripartizione, parametri dello
  studio, periodo selezionato — condiviso da tutte le schermate)
- Navigazione a stato semplice con tab bar a pillola personalizzata
  (riusata anche per il toggle Sedute/Spese nella scheda Movimenti)
- Grafici e "nastro" di ripartizione disegnati con `Canvas`/Compose puro
  (nessuna libreria esterna)
- Import/export CSV, XLSX, TXT tramite Storage Access Framework, nessuna
  dipendenza esterna per la lettura/scrittura del formato Excel

## Build

```bash
./gradlew :app:assembleDebug
./gradlew :app:testDebugUnitTest   # test di roundtrip import/export
```

Richiede Android SDK (compileSdk 34, minSdk 26) referenziato in
`local.properties` (`sdk.dir=...`), non incluso nel repository.
