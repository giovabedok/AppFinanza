package com.appfinanza.cruscotto.ui.ripartizione

import com.appfinanza.cruscotto.data.model.Scadenza
import com.appfinanza.cruscotto.data.settings.RipartizioneSettings
import com.appfinanza.cruscotto.data.settings.StudioSettings

data class RipartizioneUiState(
    val settings: RipartizioneSettings = RipartizioneSettings(),
    val studio: StudioSettings = StudioSettings(),
    val scadenze: List<Scadenza> = emptyList(),
    val mediaMensileAnno: Double = 0.0,
    val meseBassoAnno: Double = 0.0,
    val meseAltoAnno: Double = 0.0,
    val caricato: Boolean = false
)
