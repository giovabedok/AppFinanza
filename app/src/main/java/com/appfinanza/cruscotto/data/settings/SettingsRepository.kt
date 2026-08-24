package com.appfinanza.cruscotto.data.settings

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

private val Context.dataStore by preferencesDataStore(name = "appfinanza_settings")

class SettingsRepository(private val context: Context) {

    private object Keys {
        val PCT_TASSE = doublePreferencesKey("pct_tasse")
        val PCT_SPESE = doublePreferencesKey("pct_spese_professionali")
        val PCT_STIPENDIO = doublePreferencesKey("pct_stipendio")
        val PCT_FONDO = doublePreferencesKey("pct_fondo_sicurezza")
        val PCT_FUTURO = doublePreferencesKey("pct_futuro")
        val FONDO_ACCUMULATO = doublePreferencesKey("fondo_sicurezza_accumulato")
        val SPESE_MEDIE = doublePreferencesKey("spese_personali_medie")
        val ANNO_SELEZIONATO = intPreferencesKey("anno_selezionato")
        val MESE_SELEZIONATO = intPreferencesKey("mese_selezionato")
    }

    val ripartizione: Flow<RipartizioneSettings> = context.dataStore.data.map { prefs ->
        val default = RipartizioneSettings()
        RipartizioneSettings(
            pctTasse = prefs[Keys.PCT_TASSE] ?: default.pctTasse,
            pctSpeseProfessionali = prefs[Keys.PCT_SPESE] ?: default.pctSpeseProfessionali,
            pctStipendio = prefs[Keys.PCT_STIPENDIO] ?: default.pctStipendio,
            pctFondoSicurezza = prefs[Keys.PCT_FONDO] ?: default.pctFondoSicurezza,
            pctFuturo = prefs[Keys.PCT_FUTURO] ?: default.pctFuturo,
            fondoSicurezzaAccumulato = prefs[Keys.FONDO_ACCUMULATO] ?: default.fondoSicurezzaAccumulato,
            spesePersonaliMedie = prefs[Keys.SPESE_MEDIE] ?: default.spesePersonaliMedie
        )
    }

    val periodoSelezionato: Flow<PeriodoSelezionato> = context.dataStore.data.map { prefs ->
        val oggi = LocalDate.now()
        PeriodoSelezionato(
            anno = prefs[Keys.ANNO_SELEZIONATO] ?: oggi.year,
            mese = prefs[Keys.MESE_SELEZIONATO] ?: oggi.monthValue
        )
    }

    suspend fun aggiornaPercentuali(
        pctTasse: Double,
        pctSpeseProfessionali: Double,
        pctStipendio: Double,
        pctFondoSicurezza: Double,
        pctFuturo: Double
    ) {
        context.dataStore.edit { prefs ->
            prefs[Keys.PCT_TASSE] = pctTasse
            prefs[Keys.PCT_SPESE] = pctSpeseProfessionali
            prefs[Keys.PCT_STIPENDIO] = pctStipendio
            prefs[Keys.PCT_FONDO] = pctFondoSicurezza
            prefs[Keys.PCT_FUTURO] = pctFuturo
        }
    }

    suspend fun aggiornaDatiPersonali(fondoSicurezzaAccumulato: Double, spesePersonaliMedie: Double) {
        context.dataStore.edit { prefs ->
            prefs[Keys.FONDO_ACCUMULATO] = fondoSicurezzaAccumulato
            prefs[Keys.SPESE_MEDIE] = spesePersonaliMedie
        }
    }

    suspend fun aggiornaPeriodo(anno: Int, mese: Int) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ANNO_SELEZIONATO] = anno
            prefs[Keys.MESE_SELEZIONATO] = mese
        }
    }
}
