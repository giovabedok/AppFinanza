# Bussola — Cruscotto finanziario

App Android (Kotlin + Jetpack Compose) che aiuta chi lavora in proprio a
sapere sempre dove va ogni euro incassato: incassi, spese professionali,
ripartizione automatica dell'incasso e un cruscotto con grafici.

Sviluppata da **Giovanni Bedocchi** — giovanni@bedocchi.it

## Schermate

- **Cruscotto**: selettore anno/mese, KPI del mese con icone (incassi, spese
  professionali, quota tasse, stipendio personale, fondo sicurezza, futuro),
  incassi totali dell'anno, grafico a barre entrate/uscite sui 12 mesi,
  grafico a ciambella con la ripartizione del mese e la sezione "La tua
  sicurezza" (mesi di autonomia).
- **Incassi**: elenco dei pagamenti ricevuti (data, cliente, prestazione,
  importo), con aggiunta/eliminazione.
- **Spese**: elenco delle spese professionali con categoria e icona dedicata
  (Studio, Commercialista, Formazione, Supervisione, Assicurazioni, Software,
  Telefono e Internet, Marketing e sito, Trasporti, Attrezzature, Altro).
- **Ripartizione**: percentuali di divisione dell'incasso (tasse, spese
  professionali, stipendio, fondo sicurezza, futuro — devono sommare 100%),
  dati personali (fondo sicurezza accumulato, spese personali medie) e le "tre
  medie" (media mensile, mese più basso, mese più alto dell'anno).
- **Impostazioni**: esportazione e importazione di tutti i dati in CSV, Excel
  (.xlsx) e TXT, più le informazioni sull'app e sullo sviluppatore.

## Import/export

Incassi e spese si esportano e si importano con lo stesso schema a colonne
(tipo, data, voce, dettaglio, importo) in tre formati intercambiabili:

- **CSV** — separatore `;`, compatibile con Excel e Fogli Google.
- **Excel (.xlsx)** — scritto e letto senza librerie esterne (il formato
  OOXML è generato/analizzato direttamente come zip di XML).
- **TXT** — stesso schema, separatore tabulazione.

L'importazione riconosce automaticamente il formato dall'estensione del file
scelto e aggiunge le righe a quelle già presenti.

## Stack tecnico

- Kotlin, Jetpack Compose (Material 3), font Nunito/Inter (Google Fonts)
- Room (persistenza di incassi e spese)
- DataStore Preferences (percentuali di ripartizione, periodo selezionato)
- Navigation Compose con bottom bar a 5 sezioni
- Grafici disegnati con `Canvas` di Compose (nessuna libreria esterna)
- Import/export CSV, XLSX, TXT tramite Storage Access Framework, nessuna
  dipendenza esterna per la lettura/scrittura del formato Excel

## Build

```bash
./gradlew :app:assembleDebug
./gradlew :app:testDebugUnitTest   # test di roundtrip import/export
```

Richiede Android SDK (compileSdk 34, minSdk 26) referenziato in
`local.properties` (`sdk.dir=...`), non incluso nel repository.
