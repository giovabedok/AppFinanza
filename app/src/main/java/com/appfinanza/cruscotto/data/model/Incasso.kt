package com.appfinanza.cruscotto.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Una riga di incasso, equivalente a una riga del foglio "Incassi" del cruscotto Excel.
 * codiceMese replica la colonna calcolata E (anno*100+mese) usata per i SUMIFS.
 */
@Entity(tableName = "incassi")
data class Incasso(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val data: LocalDate,
    val cliente: String,
    val prestazione: String,
    val importo: Double,
    val codiceMese: Int
) {
    companion object {
        fun codiceMeseDi(data: LocalDate): Int = data.year * 100 + data.monthValue

        fun nuovo(data: LocalDate, cliente: String, prestazione: String, importo: Double): Incasso =
            Incasso(
                data = data,
                cliente = cliente,
                prestazione = prestazione,
                importo = importo,
                codiceMese = codiceMeseDi(data)
            )
    }
}
