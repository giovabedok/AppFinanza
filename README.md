# Le mie Finanze

App Android (Kotlin + Jetpack Compose) che aiuta chi lavora in proprio a
sapere sempre dove va ogni euro incassato: incassi, spese professionali,
ripartizione automatica dell'incasso e un cruscotto mensile con grafici.

Sviluppata da **Giovanni Bedocchi** — giovanni@bedocchi.it

## Schermate

Navigazione a quattro sezioni con selettore a pillola in alto (senza barra
di navigazione inferiore), palette calda (crema, petrolio, teal, corallo,
senape, salvia, blu) e font Nunito/Inter, in stile con il mockup di
riferimento del cruscotto:

- **Il mese**: incassato del mese con il "nastro" — la fascia colorata che
  mostra come si divide ogni euro —, le cinque voci di ripartizione (tasse,
  spese professionali, stipendio, fondo sicurezza, futuro), le spese
  professionali del mese con barra rispetto alla quota prevista, i mesi di
  autonomia con indicatore a tacche e l'andamento degli incassi sui 12 mesi
  dell'anno con media tratteggiata.
- **Incassi**: form scuro sempre visibile per registrare un pagamento
  (data, cliente, prestazione, importo) ed elenco del mese selezionato.
- **Spese**: form scuro per registrare una spesa professionale con categoria
  a tendina (Studio, Commercialista, Formazione, Supervisione, Assicurazioni,
  Software, Telefono e Internet, Marketing e sito, Trasporti, Attrezzature,
  Altro) ed elenco del mese selezionato.
- **Regole**: percentuali di divisione dell'incasso (devono sommare 100%),
  dati personali (fondo sicurezza accumulato, spese personali medie), le "tre
  medie" dell'anno, l'esportazione/importazione dei dati e le informazioni
  sull'app e sullo sviluppatore.

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
- Room (persistenza di incassi e spese)
- DataStore Preferences (percentuali di ripartizione, periodo selezionato)
- Navigazione a stato semplice con tab bar a pillola personalizzata
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
