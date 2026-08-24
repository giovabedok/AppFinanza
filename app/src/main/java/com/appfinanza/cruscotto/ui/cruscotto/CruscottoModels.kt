package com.appfinanza.cruscotto.ui.cruscotto

import java.time.LocalDate

data class DatoMensile(
    val numeroMese: Int,
    val etichetta: String,
    val incassi: Double,
    val spese: Double
)

data class VoceRipartizione(
    val etichetta: String,
    val importo: Double
)

data class CruscottoUiState(
    val anno: Int = LocalDate.now().year,
    val mese: Int = LocalDate.now().monthValue,
    val incassiDelMese: Double = 0.0,
    val speseProfessionaliDelMese: Double = 0.0,
    val daAccantonareTasse: Double = 0.0,
    val stipendioPersonale: Double = 0.0,
    val daFondoSicurezza: Double = 0.0,
    val daFuturo: Double = 0.0,
    val incassiTotaliAnno: Double = 0.0,
    val mesiAutonomia: Double = 0.0,
    val fondoSicurezzaAccumulato: Double = 0.0,
    val spesePersonaliMedie: Double = 0.0,
    val datiMensili: List<DatoMensile> = emptyList(),
    val ripartizioneMese: List<VoceRipartizione> = emptyList(),
    val percentualiCorrette: Boolean = true,
    val caricato: Boolean = false
) {
    companion object {
        val NOMI_MESI = listOf(
            "Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno",
            "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre"
        )
    }
}
