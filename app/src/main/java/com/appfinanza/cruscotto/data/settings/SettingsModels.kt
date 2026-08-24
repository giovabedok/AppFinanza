package com.appfinanza.cruscotto.data.settings

/** Equivalente al foglio "Ripartizione": come si divide ogni euro incassato. */
data class RipartizioneSettings(
    val pctTasse: Double = 0.35,
    val pctSpeseProfessionali: Double = 0.10,
    val pctStipendio: Double = 0.40,
    val pctFondoSicurezza: Double = 0.10,
    val pctFuturo: Double = 0.05,
    val fondoSicurezzaAccumulato: Double = 0.0,
    val spesePersonaliMedie: Double = 1800.0
) {
    val totalePercentuali: Double
        get() = pctTasse + pctSpeseProfessionali + pctStipendio + pctFondoSicurezza + pctFuturo

    val percentualiCorrette: Boolean
        get() = kotlin.math.round(totalePercentuali * 10000) / 10000.0 == 1.0
}

/** Anno e mese attualmente visualizzati nel Cruscotto. */
data class PeriodoSelezionato(
    val anno: Int,
    val mese: Int
) {
    val codiceMese: Int
        get() = anno * 100 + mese
}
