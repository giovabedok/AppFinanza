package com.appfinanza.cruscotto.ui.ripartizione

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appfinanza.cruscotto.data.model.Scadenza
import com.appfinanza.cruscotto.data.repository.FinanzaRepository
import com.appfinanza.cruscotto.data.settings.RipartizioneSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * Replica il foglio "Regole": percentuali di divisione dell'incasso, le "tre
 * medie" dell'anno selezionato, i parametri dello studio e le scadenze.
 */
class RipartizioneViewModel(private val repository: FinanzaRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(RipartizioneUiState())
    val uiState: StateFlow<RipartizioneUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.ripartizione,
                repository.incassi,
                repository.periodoSelezionato,
                repository.studio,
                repository.scadenze
            ) { settings, incassi, periodo, studio, scadenze ->
                val valoriMensili = (1..12).map { m ->
                    val cm = periodo.anno * 100 + m
                    incassi.filter { it.codiceMese == cm }.sumOf { it.importo }
                }
                val nonZero = valoriMensili.filter { it > 0.0 }
                val media = if (nonZero.isNotEmpty()) nonZero.average() else 0.0
                val basso = if (nonZero.isNotEmpty()) nonZero.min() else 0.0
                val alto = valoriMensili.maxOrNull() ?: 0.0
                RipartizioneUiState(
                    settings = settings,
                    studio = studio,
                    scadenze = scadenze.sortedBy { it.data },
                    mediaMensileAnno = media,
                    meseBassoAnno = basso,
                    meseAltoAnno = alto,
                    caricato = true
                )
            }.collect { _uiState.value = it }
        }
    }

    fun aggiornaPercentuali(
        pctTasse: Double,
        pctSpeseProfessionali: Double,
        pctStipendio: Double,
        pctFondoSicurezza: Double,
        pctFuturo: Double
    ) {
        viewModelScope.launch {
            repository.aggiornaPercentuali(
                RipartizioneSettings(
                    pctTasse = pctTasse,
                    pctSpeseProfessionali = pctSpeseProfessionali,
                    pctStipendio = pctStipendio,
                    pctFondoSicurezza = pctFondoSicurezza,
                    pctFuturo = pctFuturo,
                    fondoSicurezzaAccumulato = _uiState.value.settings.fondoSicurezzaAccumulato,
                    spesePersonaliMedie = _uiState.value.settings.spesePersonaliMedie
                )
            )
        }
    }

    fun aggiornaDatiPersonali(fondoSicurezzaAccumulato: Double, spesePersonaliMedie: Double) {
        viewModelScope.launch {
            repository.aggiornaDatiPersonali(fondoSicurezzaAccumulato, spesePersonaliMedie)
        }
    }

    fun aggiornaStudio(tariffa: Double, seduteSettimana: Double, oreNonFatturabiliSett: Double) {
        viewModelScope.launch {
            repository.aggiornaStudio(tariffa, seduteSettimana, oreNonFatturabiliSett)
        }
    }

    fun aggiungiScadenza(titolo: String, data: LocalDate) {
        viewModelScope.launch {
            repository.aggiungiScadenza(Scadenza(titolo = titolo, data = data))
        }
    }

    fun eliminaScadenza(scadenza: Scadenza) {
        viewModelScope.launch { repository.eliminaScadenza(scadenza) }
    }
}
