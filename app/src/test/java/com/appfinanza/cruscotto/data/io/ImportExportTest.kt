package com.appfinanza.cruscotto.data.io

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream
import java.time.LocalDate

class ImportExportTest {

    private val righeDiProva = listOf(
        RigaEsportata(TipoRiga.INCASSO, LocalDate.of(2026, 8, 5), "Cliente A", "Seduta", 70.0),
        RigaEsportata(TipoRiga.SPESA, LocalDate.of(2026, 8, 2), "Affitto studio", "Studio", 350.5),
        RigaEsportata(TipoRiga.INCASSO, LocalDate.of(2026, 8, 10), "Cliente; con \"virgolette\"", "Consulenza", 120.0)
    )

    @Test
    fun `csv roundtrip preserva tutte le righe`() {
        val csv = TabellaDelimitata.scrivi(righeDiProva, ';')
        val rilette = TabellaDelimitata.leggi(csv, ';')
        assertEquals(righeDiProva, rilette)
    }

    @Test
    fun `txt roundtrip preserva tutte le righe`() {
        val txt = TabellaDelimitata.scrivi(righeDiProva, '\t')
        val rilette = TabellaDelimitata.leggi(txt, '\t')
        assertEquals(righeDiProva, rilette)
    }

    @Test
    fun `xlsx roundtrip preserva tutte le righe`() {
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
