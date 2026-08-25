package com.appfinanza.cruscotto.ui.studio

data class VoceTipoSeduta(val tipo: String, val conteggio: Int)

data class StudioUiState(
    val anno: Int = 2026,
    val mese: Int = 1,
    val sedute: Int = 0,
    val incassiDelMese: Double = 0.0,
    val capacita: Int = 0,
    val riempimento: Double = 0.0,
    val tariffa: Double = 70.0,
    val seduteSettimana: Double = 20.0,
    val oreNonFatturabiliSett: Double = 8.0,
    val oreFatturate: Double = 0.0,
    val oreNonFatturabili: Double = 0.0,
    val orarioReale: Double = 0.0,
    val orarioNominale: Double = 0.0,
    val seduteTipo: List<VoceTipoSeduta> = emptyList(),
    val caricato: Boolean = false
)
