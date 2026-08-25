package com.appfinanza.cruscotto.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Una riga di spesa professionale, equivalente a una riga del foglio "Spese".
 */
@Entity(tableName = "spese")
data class Spesa(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val data: LocalDate,
    val descrizione: String,
    val categoria: String,
    val importo: Double,
    val codiceMese: Int
) {
    companion object {
        fun codiceMeseDi(data: LocalDate): Int = data.year * 100 + data.monthValue

        fun nuova(data: LocalDate, descrizione: String, categoria: String, importo: Double): Spesa =
            Spesa(
                data = data,
                descrizione = descrizione,
                categoria = categoria,
                importo = importo,
                codiceMese = codiceMeseDi(data)
            )
    }
}

/** Categorie di spesa di studio disponibili nel menu a tendina. */
object CategorieSpesa {
    val LISTA = listOf(
        "Studio",
        "Commercialista",
        "ENPAP",
        "Formazione",
        "Supervisione",
        "Assicurazione",
        "Software",
        "Telefono e Internet",
        "Sito e marketing",
        "Trasporti",
        "Altro"
    )
}

/** Tipi di seduta disponibili nel menu a tendina degli incassi. */
object TipiSeduta {
    val LISTA = listOf(
        "Seduta individuale",
        "Coppia",
        "Online",
        "Primo colloquio",
        "Altro"
    )
}
