package com.appfinanza.cruscotto.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.appfinanza.cruscotto.data.model.Incasso
import kotlinx.coroutines.flow.Flow

@Dao
interface IncassoDao {
    @Query("SELECT * FROM incassi ORDER BY data DESC, id DESC")
    fun osservaTutti(): Flow<List<Incasso>>

    @Insert
    suspend fun inserisci(incasso: Incasso): Long

    @Update
    suspend fun aggiorna(incasso: Incasso)

    @Delete
    suspend fun elimina(incasso: Incasso)
}
