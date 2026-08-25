package com.appfinanza.cruscotto.ui.impostazioni

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appfinanza.cruscotto.data.io.ChiaviImpostazione
import com.appfinanza.cruscotto.data.io.FormatoFile
import com.appfinanza.cruscotto.data.io.RigaEsportata
import com.appfinanza.cruscotto.data.io.TabellaDelimitata
import com.appfinanza.cruscotto.data.io.TipoRiga
import com.appfinanza.cruscotto.data.io.XlsxFormato
import com.appfinanza.cruscotto.data.model.Incasso
import com.appfinanza.cruscotto.data.model.Scadenza
import com.appfinanza.cruscotto.data.model.Spesa
import com.appfinanza.cruscotto.data.repository.FinanzaRepository
import com.appfinanza.cruscotto.data.settings.RipartizioneSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream

data class RisultatoImportazione(
    val incassiImportati: Int,
    val speseImportate: Int,
    val scadenzeImportate: Int,
    val impostazioniAggiornate: Boolean,
    val vuoto: Boolean
)

/**
 * Backup completo dei dati dell'app: incassi, spese, scadenze e tutte le
 * impostazioni (percentuali di ripartizione, dati personali, parametri dello
 * studio) in CSV, Excel (XLSX) e TXT, con lo stesso schema a colonne
 * (tipo, data, voce, dettaglio, importo) nelle tre modalità, così i file
 * sono intercambiabili tra loro e un'esportazione può ricostruire l'app.
 */
class ImportExportViewModel(private val repository: FinanzaRepository) : ViewModel() {

    private val _risultatoImportazione = MutableStateFlow<RisultatoImportazione?>(null)
    val risultatoImportazione: StateFlow<RisultatoImportazione?> = _risultatoImportazione.asStateFlow()

    fun azzeraRisultato() {
        _risultatoImportazione.value = null
    }

    suspend fun generaContenutoDelimitato(formato: FormatoFile): String {
        val delimitatore = if (formato == FormatoFile.TXT) '\t' else ';'
        return TabellaDelimitata.scrivi(righeEsportabili(), delimitatore)
    }

    suspend fun generaContenutoXlsx(): ByteArray = XlsxFormato.scrivi(righeEsportabili())

    private suspend fun righeEsportabili(): List<RigaEsportata> {
        val incassi = repository.incassi.first()
        val spese = repository.spese.first()
        val scadenze = repository.scadenze.first()
        val ripartizione = repository.ripartizione.first()
        val studio = repository.studio.first()

        val righeMovimenti = (
            incassi.map { RigaEsportata(TipoRiga.INCASSO, it.data, it.cliente, it.prestazione, it.importo) } +
                spese.map { RigaEsportata(TipoRiga.SPESA, it.data, it.descrizione, it.categoria, it.importo) } +
                scadenze.map { RigaEsportata(TipoRiga.SCADENZA, it.data, it.titolo, if (it.ricorrente) "ricorrente" else "", 0.0) }
            ).sortedByDescending { it.data }

        val righeImpostazioni = listOf(
            RigaEsportata(TipoRiga.IMPOSTAZIONE, null, ChiaviImpostazione.PCT_TASSE, "", ripartizione.pctTasse),
            RigaEsportata(TipoRiga.IMPOSTAZIONE, null, ChiaviImpostazione.PCT_SPESE_PROFESSIONALI, "", ripartizione.pctSpeseProfessionali),
            RigaEsportata(TipoRiga.IMPOSTAZIONE, null, ChiaviImpostazione.PCT_STIPENDIO, "", ripartizione.pctStipendio),
            RigaEsportata(TipoRiga.IMPOSTAZIONE, null, ChiaviImpostazione.PCT_FONDO_SICUREZZA, "", ripartizione.pctFondoSicurezza),
            RigaEsportata(TipoRiga.IMPOSTAZIONE, null, ChiaviImpostazione.PCT_FUTURO, "", ripartizione.pctFuturo),
            RigaEsportata(TipoRiga.IMPOSTAZIONE, null, ChiaviImpostazione.FONDO_SICUREZZA_ACCUMULATO, "", ripartizione.fondoSicurezzaAccumulato),
            RigaEsportata(TipoRiga.IMPOSTAZIONE, null, ChiaviImpostazione.SPESE_PERSONALI_MEDIE, "", ripartizione.spesePersonaliMedie),
            RigaEsportata(TipoRiga.IMPOSTAZIONE, null, ChiaviImpostazione.TARIFFA, "", studio.tariffa),
            RigaEsportata(TipoRiga.IMPOSTAZIONE, null, ChiaviImpostazione.SEDUTE_SETTIMANA, "", studio.seduteSettimana),
            RigaEsportata(TipoRiga.IMPOSTAZIONE, null, ChiaviImpostazione.ORE_NON_FATTURABILI_SETT, "", studio.oreNonFatturabiliSett)
        )

        return righeMovimenti + righeImpostazioni
    }

    fun importaTesto(testo: String, formato: FormatoFile) {
        viewModelScope.launch {
            val delimitatore = if (formato == FormatoFile.TXT) '\t' else ';'
            importaRighe(TabellaDelimitata.leggi(testo, delimitatore))
        }
    }

    fun importaXlsx(bytes: ByteArray) {
        viewModelScope.launch {
            importaRighe(XlsxFormato.leggi(ByteArrayInputStream(bytes)))
        }
    }

    private suspend fun importaRighe(righe: List<RigaEsportata>) {
        var incassiImportati = 0
        var speseImportate = 0
        var scadenzeImportate = 0
        val valoriImpostazioni = mutableMapOf<String, Double>()

        righe.forEach { riga ->
            when (riga.tipo) {
                TipoRiga.INCASSO -> riga.data?.let { data ->
                    repository.aggiungiIncasso(Incasso.nuovo(data, riga.voce, riga.dettaglio, riga.importo))
                    incassiImportati++
                }
                TipoRiga.SPESA -> riga.data?.let { data ->
                    repository.aggiungiSpesa(Spesa.nuova(data, riga.voce, riga.dettaglio, riga.importo))
                    speseImportate++
                }
                TipoRiga.SCADENZA -> riga.data?.let { data ->
                    repository.aggiungiScadenza(Scadenza(titolo = riga.voce, data = data, ricorrente = riga.dettaglio == "ricorrente"))
                    scadenzeImportate++
                }
                TipoRiga.IMPOSTAZIONE -> valoriImpostazioni[riga.voce] = riga.importo
            }
        }

        val impostazioniAggiornate = valoriImpostazioni.isNotEmpty()
        if (impostazioniAggiornate) {
            val ripartizioneAttuale = repository.ripartizione.first()
            val studioAttuale = repository.studio.first()
            fun valore(chiave: String, attuale: Double) = valoriImpostazioni[chiave] ?: attuale

            repository.aggiornaPercentuali(
                RipartizioneSettings(
                    pctTasse = valore(ChiaviImpostazione.PCT_TASSE, ripartizioneAttuale.pctTasse),
                    pctSpeseProfessionali = valore(ChiaviImpostazione.PCT_SPESE_PROFESSIONALI, ripartizioneAttuale.pctSpeseProfessionali),
                    pctStipendio = valore(ChiaviImpostazione.PCT_STIPENDIO, ripartizioneAttuale.pctStipendio),
                    pctFondoSicurezza = valore(ChiaviImpostazione.PCT_FONDO_SICUREZZA, ripartizioneAttuale.pctFondoSicurezza),
                    pctFuturo = valore(ChiaviImpostazione.PCT_FUTURO, ripartizioneAttuale.pctFuturo)
                )
            )
            repository.aggiornaDatiPersonali(
                fondoSicurezzaAccumulato = valore(ChiaviImpostazione.FONDO_SICUREZZA_ACCUMULATO, ripartizioneAttuale.fondoSicurezzaAccumulato),
                spesePersonaliMedie = valore(ChiaviImpostazione.SPESE_PERSONALI_MEDIE, ripartizioneAttuale.spesePersonaliMedie)
            )
            repository.aggiornaStudio(
                tariffa = valore(ChiaviImpostazione.TARIFFA, studioAttuale.tariffa),
                seduteSettimana = valore(ChiaviImpostazione.SEDUTE_SETTIMANA, studioAttuale.seduteSettimana),
                oreNonFatturabiliSett = valore(ChiaviImpostazione.ORE_NON_FATTURABILI_SETT, studioAttuale.oreNonFatturabiliSett)
            )
        }

        _risultatoImportazione.value = RisultatoImportazione(
            incassiImportati = incassiImportati,
            speseImportate = speseImportate,
            scadenzeImportate = scadenzeImportate,
            impostazioniAggiornate = impostazioniAggiornate,
            vuoto = righe.isEmpty()
        )
    }
}
