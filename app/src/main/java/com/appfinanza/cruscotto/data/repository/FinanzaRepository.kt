package com.appfinanza.cruscotto.data.repository

import com.appfinanza.cruscotto.data.db.IncassoDao
import com.appfinanza.cruscotto.data.db.SpesaDao
import com.appfinanza.cruscotto.data.model.Incasso
import com.appfinanza.cruscotto.data.model.Spesa
import com.appfinanza.cruscotto.data.settings.PeriodoSelezionato
import com.appfinanza.cruscotto.data.settings.RipartizioneSettings
import com.appfinanza.cruscotto.data.settings.SettingsRepository
import kotlinx.coroutines.flow.Flow

/**
 * Punto unico di accesso ai dati: incassi, spese e impostazioni di ripartizione,
 * equivalente ai quattro fogli del cruscotto Excel originale.
 */
class FinanzaRepository(
    private val incassoDao: IncassoDao,
    private val spesaDao: SpesaDao,
    private val settingsRepository: SettingsRepository
) {
    val incassi: Flow<List<Incasso>> = incassoDao.osservaTutti()
    val spese: Flow<List<Spesa>> = spesaDao.osservaTutte()
    val ripartizione: Flow<RipartizioneSettings> = settingsRepository.ripartizione
    val periodoSelezionato: Flow<PeriodoSelezionato> = settingsRepository.periodoSelezionato

    suspend fun aggiungiIncasso(incasso: Incasso) = incassoDao.inserisci(incasso)
    suspend fun aggiornaIncasso(incasso: Incasso) = incassoDao.aggiorna(incasso)
    suspend fun eliminaIncasso(incasso: Incasso) = incassoDao.elimina(incasso)

    suspend fun aggiungiSpesa(spesa: Spesa) = spesaDao.inserisci(spesa)
    suspend fun aggiornaSpesa(spesa: Spesa) = spesaDao.aggiorna(spesa)
    suspend fun eliminaSpesa(spesa: Spesa) = spesaDao.elimina(spesa)

    suspend fun aggiornaPercentuali(settings: RipartizioneSettings) = settingsRepository.aggiornaPercentuali(
        pctTasse = settings.pctTasse,
        pctSpeseProfessionali = settings.pctSpeseProfessionali,
        pctStipendio = settings.pctStipendio,
        pctFondoSicurezza = settings.pctFondoSicurezza,
        pctFuturo = settings.pctFuturo
    )

    suspend fun aggiornaDatiPersonali(fondoSicurezzaAccumulato: Double, spesePersonaliMedie: Double) =
        settingsRepository.aggiornaDatiPersonali(fondoSicurezzaAccumulato, spesePersonaliMedie)

    suspend fun aggiornaPeriodo(anno: Int, mese: Int) = settingsRepository.aggiornaPeriodo(anno, mese)
}
