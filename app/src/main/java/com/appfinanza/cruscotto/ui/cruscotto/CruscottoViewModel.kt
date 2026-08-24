package com.appfinanza.cruscotto.ui.cruscotto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appfinanza.cruscotto.data.model.Incasso
import com.appfinanza.cruscotto.data.model.Spesa
import com.appfinanza.cruscotto.data.repository.FinanzaRepository
import com.appfinanza.cruscotto.data.settings.PeriodoSelezionato
import com.appfinanza.cruscotto.data.settings.RipartizioneSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * Replica le formule del foglio "Cruscotto": SUMIFS su Incassi/Spese filtrati per codice mese,
 * ripartizione percentuale del mese selezionato e serie dei 12 mesi per il grafico a barre.
 */
class CruscottoViewModel(private val repository: FinanzaRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(CruscottoUiState())
    val uiState: StateFlow<CruscottoUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.incassi,
                repository.spese,
                repository.ripartizione,
                repository.periodoSelezionato
            ) { incassi, spese, ripartizione, periodo ->
                costruisciStato(incassi, spese, ripartizione, periodo)
            }.collect { _uiState.value = it }
        }
    }

    private fun costruisciStato(
        incassi: List<Incasso>,
        spese: List<Spesa>,
        ripartizione: RipartizioneSettings,
        periodo: PeriodoSelezionato
    ): CruscottoUiState {
        val codiceMese = periodo.codiceMese
        val incassiDelMese = incassi.filter { it.codiceMese == codiceMese }.sumOf { it.importo }
        val speseDelMese = spese.filter { it.codiceMese == codiceMese }.sumOf { it.importo }
        val incassiTotaliAnno = incassi.filter { it.data.year == periodo.anno }.sumOf { it.importo }

        val datiMensili = (1..12).map { m ->
            val cm = periodo.anno * 100 + m
            DatoMensile(
                numeroMese = m,
                etichetta = CruscottoUiState.NOMI_MESI[m - 1],
                incassi = incassi.filter { it.codiceMese == cm }.sumOf { it.importo },
                spese = spese.filter { it.codiceMese == cm }.sumOf { it.importo }
            )
        }

        val mesiAutonomia = if (ripartizione.spesePersonaliMedie != 0.0) {
            ripartizione.fondoSicurezzaAccumulato / ripartizione.spesePersonaliMedie
        } else 0.0

        val ripartizioneMese = listOf(
            VoceRipartizione("Tasse e contributi", incassiDelMese * ripartizione.pctTasse),
            VoceRipartizione("Spese professionali", incassiDelMese * ripartizione.pctSpeseProfessionali),
            VoceRipartizione("Stipendio personale", incassiDelMese * ripartizione.pctStipendio),
            VoceRipartizione("Fondo sicurezza", incassiDelMese * ripartizione.pctFondoSicurezza),
            VoceRipartizione("Futuro", incassiDelMese * ripartizione.pctFuturo)
        )

        return CruscottoUiState(
            anno = periodo.anno,
            mese = periodo.mese,
            incassiDelMese = incassiDelMese,
            speseProfessionaliDelMese = speseDelMese,
            daAccantonareTasse = incassiDelMese * ripartizione.pctTasse,
            stipendioPersonale = incassiDelMese * ripartizione.pctStipendio,
            daFondoSicurezza = incassiDelMese * ripartizione.pctFondoSicurezza,
            daFuturo = incassiDelMese * ripartizione.pctFuturo,
            incassiTotaliAnno = incassiTotaliAnno,
            mesiAutonomia = mesiAutonomia,
            fondoSicurezzaAccumulato = ripartizione.fondoSicurezzaAccumulato,
            spesePersonaliMedie = ripartizione.spesePersonaliMedie,
            datiMensili = datiMensili,
            ripartizioneMese = ripartizioneMese,
            percentualiCorrette = ripartizione.percentualiCorrette,
            caricato = true
        )
    }

    fun cambiaPeriodo(anno: Int, mese: Int) {
        viewModelScope.launch { repository.aggiornaPeriodo(anno, mese) }
    }
}
