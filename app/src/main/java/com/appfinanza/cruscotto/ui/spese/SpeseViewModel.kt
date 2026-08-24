package com.appfinanza.cruscotto.ui.spese

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appfinanza.cruscotto.data.model.Spesa
import com.appfinanza.cruscotto.data.repository.FinanzaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class SpeseViewModel(private val repository: FinanzaRepository) : ViewModel() {

    val spese: StateFlow<List<Spesa>> = repository.spese.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun aggiungi(data: LocalDate, descrizione: String, categoria: String, importo: Double) {
        viewModelScope.launch {
            repository.aggiungiSpesa(Spesa.nuova(data, descrizione, categoria, importo))
        }
    }

    fun aggiorna(spesa: Spesa) {
        viewModelScope.launch { repository.aggiornaSpesa(spesa) }
    }

    fun elimina(spesa: Spesa) {
        viewModelScope.launch { repository.eliminaSpesa(spesa) }
    }
}
