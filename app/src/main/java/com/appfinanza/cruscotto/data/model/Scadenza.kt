package com.appfinanza.cruscotto.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/** Una scadenza da tenere d'occhio (acconto imposte, contributi, rinnovi). */
@Entity(tableName = "scadenze")
data class Scadenza(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titolo: String,
    val data: LocalDate,
    val ricorrente: Boolean = false
)
