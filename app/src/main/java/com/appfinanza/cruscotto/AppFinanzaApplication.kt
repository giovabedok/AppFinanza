package com.appfinanza.cruscotto

import android.app.Application
import com.appfinanza.cruscotto.data.db.AppDatabase
import com.appfinanza.cruscotto.data.repository.FinanzaRepository
import com.appfinanza.cruscotto.data.settings.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppFinanzaApplication : Application() {

    lateinit var repository: FinanzaRepository
        private set

    private val ambitoApplicazione = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        val database = AppDatabase.getInstance(this)
        val settingsRepository = SettingsRepository(this)
        repository = FinanzaRepository(
            incassoDao = database.incassoDao(),
            spesaDao = database.spesaDao(),
            scadenzaDao = database.scadenzaDao(),
            settingsRepository = settingsRepository
        )

        ambitoApplicazione.launch {
            repository.seminaScadenzeIniziali()
        }
    }
}
