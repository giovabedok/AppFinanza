package com.appfinanza.cruscotto.ui.persone

data class VocePaziente(
    val nome: String,
    val totale: Double,
    val sedute: Int,
    val giorniUltimaVisita: Long
)

data class PersoneUiState(
    val anno: Int = 2026,
    val totaleAnno: Double = 0.0,
    val numeroSedute: Int = 0,
    val pazienti: List<VocePaziente> = emptyList(),
    val nomeConcentrato: String? = null,
    val percentualeConcentrato: Double = 0.0,
    val caricato: Boolean = false
)
