package com.appfinanza.cruscotto.ui.ripartizione

import com.appfinanza.cruscotto.data.settings.RipartizioneSettings

data class RipartizioneUiState(
    val settings: RipartizioneSettings = RipartizioneSettings(),
    val mediaMensileAnno: Double = 0.0,
    val meseBassoAnno: Double = 0.0,
    val meseAltoAnno: Double = 0.0,
    val caricato: Boolean = false
)
