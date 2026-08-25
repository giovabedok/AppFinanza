package com.appfinanza.cruscotto.data.repository

import com.appfinanza.cruscotto.data.db.IncassoDao
import com.appfinanza.cruscotto.data.db.ScadenzaDao
import com.appfinanza.cruscotto.data.db.SpesaDao
import com.appfinanza.cruscotto.data.model.Incasso
import com.appfinanza.cruscotto.data.model.Scadenza
import com.appfinanza.cruscotto.data.model.Spesa
import com.appfinanza.cruscotto.data.settings.PeriodoSelezionato
import com.appfinanza.cruscotto.data.settings.RipartizioneSettings
import com.appfinanza.cruscotto.data.settings.SettingsRepository
import com.appfinanza.cruscotto.data.settings.StudioSettings
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/** Le scadenze proposte al primo avvio, finché l'utente non le modifica. */
private val SCADENZE_INIZIALI = listOf(
    Scadenza(titolo = "Acconto imposte", data = LocalDate.of(LocalDate.now().year, 6, 30), ricorrente = true),
    Scadenza(titolo = "Contributi ENPAP", data = LocalDate.of(LocalDate.now().year, 10, 31), ricorrente = true),
    Scadenza(titolo = "Rinnovo assicurazione professionale", data = LocalDate.of(LocalDate.now().year, 11, 15), ricorrente = true)
)

/**
 * Punto unico di accesso ai dati: incassi, spese, scadenze e impostazioni
 * (ripartizione dell'incasso e parametri dello studio).
 */
class FinanzaRepository(
    private val incassoDao: IncassoDao,
    private val spesaDao: SpesaDao,
    private val scadenzaDao: ScadenzaDao,
    private val settingsRepository: SettingsRepository
) {
    val incassi: Flow<List<Incasso>> = incassoDao.osservaTutti()
    val spese: Flow<List<Spesa>> = spesaDao.osservaTutte()
    val scadenze: Flow<List<Scadenza>> = scadenzaDao.osservaTutte()
    val ripartizione: Flow<RipartizioneSettings> = settingsRepository.ripartizione
    val periodoSelezionato: Flow<PeriodoSelezionato> = settingsRepository.periodoSelezionato
    val studio: Flow<StudioSettings> = settingsRepository.studio

    suspend fun aggiungiIncasso(incasso: Incasso) = incassoDao.inserisci(incasso)
    suspend fun aggiornaIncasso(incasso: Incasso) = incassoDao.aggiorna(incasso)
    suspend fun eliminaIncasso(incasso: Incasso) = incassoDao.elimina(incasso)

    suspend fun aggiungiSpesa(spesa: Spesa) = spesaDao.inserisci(spesa)
    suspend fun aggiornaSpesa(spesa: Spesa) = spesaDao.aggiorna(spesa)
    suspend fun eliminaSpesa(spesa: Spesa) = spesaDao.elimina(spesa)

    suspend fun aggiungiScadenza(scadenza: Scadenza) = scadenzaDao.inserisci(scadenza)
    suspend fun eliminaScadenza(scadenza: Scadenza) = scadenzaDao.elimina(scadenza)

    suspend fun seminaScadenzeIniziali() {
        if (scadenzaDao.conta() == 0) {
            SCADENZE_INIZIALI.forEach { scadenzaDao.inserisci(it) }
        }
    }

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

    suspend fun aggiornaStudio(tariffa: Double, seduteSettimana: Double, oreNonFatturabiliSett: Double) =
        settingsRepository.aggiornaStudio(tariffa, seduteSettimana, oreNonFatturabiliSett)
}
