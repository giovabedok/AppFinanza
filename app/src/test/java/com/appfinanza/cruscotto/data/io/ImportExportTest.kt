package com.appfinanza.cruscotto.data.io

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream
import java.time.LocalDate

class ImportExportTest {

    private val righeDiProva = listOf(
        RigaEsportata(TipoRiga.INCASSO, LocalDate.of(2026, 8, 5), "Cliente A", "Seduta individuale", 70.0),
        RigaEsportata(TipoRiga.SPESA, LocalDate.of(2026, 8, 2), "Affitto studio", "Studio", 350.5),
        RigaEsportata(TipoRiga.INCASSO, LocalDate.of(2026, 8, 10), "Cliente; con \"virgolette\"", "Coppia", 120.0),
        RigaEsportata(TipoRiga.SCADENZA, LocalDate.of(2026, 10, 31), "Contributi ENPAP", "ricorrente", 0.0),
        RigaEsportata(TipoRiga.IMPOSTAZIONE, null, ChiaviImpostazione.PCT_TASSE, "", 0.35),
        RigaEsportata(TipoRiga.IMPOSTAZIONE, null, ChiaviImpostazione.TARIFFA, "", 70.0)
    )

    @Test
    fun `csv roundtrip preserva tutte le righe, incluse scadenze e impostazioni`() {
        val csv = TabellaDelimitata.scrivi(righeDiProva, ';')
        val rilette = TabellaDelimitata.leggi(csv, ';')
        assertEquals(righeDiProva, rilette)
    }

    @Test
    fun `txt roundtrip preserva tutte le righe, incluse scadenze e impostazioni`() {
        val txt = TabellaDelimitata.scrivi(righeDiProva, '\t')
        val rilette = TabellaDelimitata.leggi(txt, '\t')
        assertEquals(righeDiProva, rilette)
    }

    @Test
    fun `xlsx roundtrip preserva tutte le righe, incluse scadenze e impostazioni`() {
        val bytes = XlsxFormato.scrivi(righeDiProva)
        val rilette = XlsxFormato.leggi(ByteArrayInputStream(bytes))
        assertEquals(righeDiProva, rilette)
    }

    @Test
    fun `csv con intestazione vuota non genera righe`() {
        val rilette = TabellaDelimitata.leggi("", ';')
        assertTrue(rilette.isEmpty())
    }
}
