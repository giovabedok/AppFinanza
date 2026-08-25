package com.appfinanza.cruscotto.ui.common

import androidx.compose.ui.graphics.Color
import com.appfinanza.cruscotto.ui.theme.Blu
import com.appfinanza.cruscotto.ui.theme.Corallo
import com.appfinanza.cruscotto.ui.theme.Petrolio
import com.appfinanza.cruscotto.ui.theme.Salvia
import com.appfinanza.cruscotto.ui.theme.Senape

data class VoceRipartizioneInfo(val etichetta: String, val nota: String, val colore: Color)

/** Le cinque voci di ripartizione dell'incasso, in ordine fisso: tasse, spese, stipendio, fondo, futuro. */
val VOCI_RIPARTIZIONE = listOf(
    VoceRipartizioneInfo("Tasse e contributi", "Da spostare subito sul conto tasse", Petrolio),
    VoceRipartizioneInfo("Spese di studio", "Affitto, software, formazione", Senape),
    VoceRipartizioneInfo("Stipendio personale", "Il denaro davvero tuo", Corallo),
    VoceRipartizioneInfo("Fondo sicurezza", "Mesi deboli e imprevisti", Blu),
    VoceRipartizioneInfo("Futuro", "Risparmio e pensione integrativa", Salvia)
)
