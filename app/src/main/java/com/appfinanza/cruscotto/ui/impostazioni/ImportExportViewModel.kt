package com.appfinanza.cruscotto.ui.impostazioni

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appfinanza.cruscotto.data.io.FormatoFile
import com.appfinanza.cruscotto.data.io.RigaEsportata
import com.appfinanza.cruscotto.data.io.TabellaDelimitata
import com.appfinanza.cruscotto.data.io.TipoRiga
import com.appfinanza.cruscotto.data.io.XlsxFormato
import com.appfinanza.cruscotto.data.model.Incasso
import com.appfinanza.cruscotto.data.model.Spesa
import com.appfinanza.cruscotto.data.repository.FinanzaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream

data class RisultatoImportazione(
    val incassiImportati: Int,
    val speseImportate: Int,
    val vuoto: Boolean
)

/**
 * Esporta e importa incassi e spese in CSV, Excel (XLSX) e TXT, con lo stesso
 * schema a colonne (tipo, data, voce, dettaglio, importo) usato nelle tre
 * modalità, così i file sono intercambiabili tra loro.
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
        val righeIncassi = incassi.map { RigaEsportata(TipoRiga.INCASSO, it.data, it.cliente, it.prestazione, it.importo) }
        val righeSpese = spese.map { RigaEsportata(TipoRiga.SPESA, it.data, it.descrizione, it.categoria, it.importo) }
        return (righeIncassi + righeSpese).sortedByDescending { it.data }
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
        righe.forEach { riga ->
            when (riga.tipo) {
                TipoRiga.INCASSO -> {
                    repository.aggiungiIncasso(Incasso.nuovo(riga.data, riga.voce, riga.dettaglio, riga.importo))
                    incassiImportati++
                }
                TipoRiga.SPESA -> {
                    repository.aggiungiSpesa(Spesa.nuova(riga.data, riga.voce, riga.dettaglio, riga.importo))
                    speseImportate++
                }
            }
        }
        _risultatoImportazione.value = RisultatoImportazione(incassiImportati, speseImportate, righe.isEmpty())
    }
}
