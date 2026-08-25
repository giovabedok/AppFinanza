package com.appfinanza.cruscotto.data.io

import java.time.LocalDate

enum class TipoRiga { INCASSO, SPESA, SCADENZA, IMPOSTAZIONE }

/**
 * Rappresentazione unificata di una riga del backup: un incasso, una spesa,
 * una scadenza o una singola impostazione (percentuali, dati personali,
 * parametri dello studio). Tutte condividono lo stesso schema di colonne —
 * tipo, data, voce, dettaglio, importo — così CSV, Excel e TXT sono
 * intercambiabili e un'esportazione contiene sempre un backup completo.
 *
 * Per le righe di tipo IMPOSTAZIONE, [data] non è significativa (nessuna
 * data associata) e [voce] contiene la chiave del parametro (vedi
 * [ChiaviImpostazione]) mentre [importo] ne contiene il valore.
 */
data class RigaEsportata(
    val tipo: TipoRiga,
    val data: LocalDate?,
    val voce: String,
    val dettaglio: String,
    val importo: Double
)

/** Chiavi delle righe IMPOSTAZIONE esportate/importate nel backup. */
object ChiaviImpostazione {
    const val PCT_TASSE = "pct_tasse"
    const val PCT_SPESE_PROFESSIONALI = "pct_spese_professionali"
    const val PCT_STIPENDIO = "pct_stipendio"
    const val PCT_FONDO_SICUREZZA = "pct_fondo_sicurezza"
    const val PCT_FUTURO = "pct_futuro"
    const val FONDO_SICUREZZA_ACCUMULATO = "fondo_sicurezza_accumulato"
    const val SPESE_PERSONALI_MEDIE = "spese_personali_medie"
    const val TARIFFA = "tariffa"
    const val SEDUTE_SETTIMANA = "sedute_settimana"
    const val ORE_NON_FATTURABILI_SETT = "ore_non_fatturabili_sett"
}
