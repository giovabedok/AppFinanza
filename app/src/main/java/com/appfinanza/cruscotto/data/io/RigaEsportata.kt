package com.appfinanza.cruscotto.data.io

import java.time.LocalDate

enum class TipoRiga { INCASSO, SPESA }

/**
 * Rappresentazione unificata di un incasso o una spesa, usata per esportare e
 * importare i dati in CSV, Excel (XLSX) e TXT con lo stesso schema di colonne:
 * tipo, data, voce (cliente/descrizione), dettaglio (prestazione/categoria), importo.
 */
data class RigaEsportata(
    val tipo: TipoRiga,
    val data: LocalDate,
    val voce: String,
    val dettaglio: String,
    val importo: Double
)
