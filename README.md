# AppFinanza — Cruscotto finanziario

App Android (Kotlin + Jetpack Compose) che replica il cruscotto finanziario Excel
per liberi professionisti: incassi, spese professionali, ripartizione automatica
dell'incasso e un cruscotto con grafici, generato a partire dal file
`Cruscotto_finanze_Francesca.xlsx`.

## Schermate

- **Cruscotto**: selettore anno/mese, KPI del mese (incassi, spese professionali,
  quota tasse, stipendio personale, fondo sicurezza, futuro), incassi totali
  dell'anno, grafico a barre entrate/uscite sui 12 mesi, grafico a ciambella con
  la ripartizione del mese e la sezione "La tua sicurezza" (mesi di autonomia).
- **Incassi**: elenco dei pagamenti ricevuti (data, cliente, prestazione,
  importo), con aggiunta/eliminazione.
- **Spese**: elenco delle spese professionali con categoria (Studio,
  Commercialista, Formazione, Supervisione, Assicurazioni, Software, Telefono e
  Internet, Marketing e sito, Trasporti, Attrezzature, Altro).
- **Ripartizione**: percentuali di divisione dell'incasso (tasse, spese
  professionali, stipendio, fondo sicurezza, futuro — devono sommare 100%),
  dati personali (fondo sicurezza accumulato, spese personali medie) e le "tre
  medie" (media mensile, mese più basso, mese più alto dell'anno).

## Stack tecnico

- Kotlin, Jetpack Compose (Material 3)
- Room (persistenza di incassi e spese)
- DataStore Preferences (percentuali di ripartizione, periodo selezionato)
- Navigation Compose con bottom bar a 4 sezioni
- Grafici disegnati con `Canvas` di Compose (nessuna libreria esterna)

## Build

```bash
./gradlew :app:assembleDebug
```

Richiede Android SDK (compileSdk 34, minSdk 26) referenziato in
`local.properties` (`sdk.dir=...`), non incluso nel repository.
