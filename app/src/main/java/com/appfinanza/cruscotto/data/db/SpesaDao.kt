package com.appfinanza.cruscotto.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.appfinanza.cruscotto.data.model.Spesa
import kotlinx.coroutines.flow.Flow

@Dao
interface SpesaDao {
    @Query("SELECT * FROM spese ORDER BY data DESC, id DESC")
    fun osservaTutte(): Flow<List<Spesa>>

    @Insert
    suspend fun inserisci(spesa: Spesa): Long

    @Update
    suspend fun aggiorna(spesa: Spesa)

    @Delete
    suspend fun elimina(spesa: Spesa)
}
