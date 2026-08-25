package com.appfinanza.cruscotto.ui.persone

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appfinanza.cruscotto.data.model.Incasso
import com.appfinanza.cruscotto.data.repository.FinanzaRepository
import com.appfinanza.cruscotto.data.settings.PeriodoSelezionato
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Le persone seguite nell'anno del periodo selezionato: quanto vale ognuna,
 * quante sedute, quando l'ultima — e un avviso se una persona pesa troppo
 * sul totale dell'anno.
 */
class PersoneViewModel(private val repository: FinanzaRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(PersoneUiState())
    val uiState: StateFlow<PersoneUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(repository.incassi, repository.periodoSelezionato) { incassi, periodo ->
                costruisciStato(incassi, periodo)
            }.collect { _uiState.value = it }
        }
    }

    private fun costruisciStato(incassi: List<Incasso>, periodo: PeriodoSelezionato): PersoneUiState {
        val delAnno = incassi.filter { it.data.year == periodo.anno }
        val totale = delAnno.sumOf { it.importo }
        val oggi = LocalDate.now()

        val pazienti = delAnno
            .groupBy { it.cliente.ifBlank { "Senza nome" } }
            .map { (nome, lista) ->
                val ultima = lista.maxOf { it.data }
                VocePaziente(
                    nome = nome,
                    totale = lista.sumOf { it.importo },
                    sedute = lista.size,
                    giorniUltimaVisita = ChronoUnit.DAYS.between(ultima, oggi)
                )
            }
            .sortedByDescending { it.totale }

        val concentrato = pazienti.firstOrNull { totale > 0 && it.totale / totale > 0.3 }

        return PersoneUiState(
            anno = periodo.anno,
            totaleAnno = totale,
            numeroSedute = delAnno.size,
            pazienti = pazienti,
            nomeConcentrato = concentrato?.nome,
            percentualeConcentrato = if (concentrato != null && totale > 0) concentrato.totale / totale else 0.0,
            caricato = true
        )
    }
}
