package com.appfinanza.cruscotto.ui.incassi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appfinanza.cruscotto.data.model.Incasso
import com.appfinanza.cruscotto.data.repository.FinanzaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class IncassiViewModel(private val repository: FinanzaRepository) : ViewModel() {

    val incassi: StateFlow<List<Incasso>> = repository.incassi.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun aggiungi(data: LocalDate, cliente: String, prestazione: String, importo: Double) {
        viewModelScope.launch {
            repository.aggiungiIncasso(Incasso.nuovo(data, cliente, prestazione, importo))
        }
    }

    fun aggiorna(incasso: Incasso) {
        viewModelScope.launch { repository.aggiornaIncasso(incasso) }
    }

    fun elimina(incasso: Incasso) {
        viewModelScope.launch { repository.eliminaIncasso(incasso) }
    }
}
