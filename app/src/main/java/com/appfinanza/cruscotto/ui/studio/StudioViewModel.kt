package com.appfinanza.cruscotto.ui.studio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appfinanza.cruscotto.data.model.Incasso
import com.appfinanza.cruscotto.data.model.TipiSeduta
import com.appfinanza.cruscotto.data.repository.FinanzaRepository
import com.appfinanza.cruscotto.data.settings.PeriodoSelezionato
import com.appfinanza.cruscotto.data.settings.StudioSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.YearMonth
import kotlin.math.roundToInt

private const val DURATA_SEDUTA_ORE = 50.0 / 60.0

/**
 * Quanto è pieno il mese e quanto vale davvero un'ora di lavoro: replica il
 * foglio "Studio" del mockup, combinando incassi del mese, capacità
 * settimanale e ore non fatturabili.
 */
class StudioViewModel(private val repository: FinanzaRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(StudioUiState())
    val uiState: StateFlow<StudioUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.incassi,
                repository.studio,
                repository.periodoSelezionato
            ) { incassi, studio, periodo ->
                costruisciStato(incassi, studio, periodo)
            }.collect { _uiState.value = it }
        }
    }

    private fun costruisciStato(
        incassi: List<Incasso>,
        studio: StudioSettings,
        periodo: PeriodoSelezionato
    ): StudioUiState {
        val delMese = incassi.filter { it.codiceMese == periodo.codiceMese }
        val sedute = delMese.size
        val incassiDelMese = delMese.sumOf { it.importo }

        val settimaneDelMese = YearMonth.of(periodo.anno, periodo.mese).lengthOfMonth() / 7.0
        val capacita = (studio.seduteSettimana * settimaneDelMese).roundToInt()
        val riempimento = if (capacita > 0) sedute.toDouble() / capacita else 0.0

        val oreFatturate = sedute * DURATA_SEDUTA_ORE
        val oreNonFatturabili = studio.oreNonFatturabiliSett * settimaneDelMese
        val oreTotali = oreFatturate + oreNonFatturabili
        val orarioReale = if (oreTotali > 0) incassiDelMese / oreTotali else 0.0
        val orarioNominale = studio.tariffa / DURATA_SEDUTA_ORE

        val seduteTipo = TipiSeduta.LISTA.mapNotNull { tipo ->
            val n = delMese.count { it.prestazione == tipo }
            if (n > 0) VoceTipoSeduta(tipo, n) else null
        }

        return StudioUiState(
            anno = periodo.anno,
            mese = periodo.mese,
            sedute = sedute,
            incassiDelMese = incassiDelMese,
            capacita = capacita,
            riempimento = riempimento,
            tariffa = studio.tariffa,
            seduteSettimana = studio.seduteSettimana,
            oreNonFatturabiliSett = studio.oreNonFatturabiliSett,
            oreFatturate = oreFatturate,
            oreNonFatturabili = oreNonFatturabili,
            orarioReale = orarioReale,
            orarioNominale = orarioNominale,
            seduteTipo = seduteTipo,
            caricato = true
        )
    }

    fun aggiornaImpostazioni(tariffa: Double, seduteSettimana: Double, oreNonFatturabiliSett: Double) {
        viewModelScope.launch {
            repository.aggiornaStudio(tariffa, seduteSettimana, oreNonFatturabiliSett)
        }
    }
}
