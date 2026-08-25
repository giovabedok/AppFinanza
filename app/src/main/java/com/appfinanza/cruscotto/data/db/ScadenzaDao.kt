package com.appfinanza.cruscotto.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.appfinanza.cruscotto.data.model.Scadenza
import kotlinx.coroutines.flow.Flow

@Dao
interface ScadenzaDao {
    @Query("SELECT * FROM scadenze ORDER BY data ASC")
    fun osservaTutte(): Flow<List<Scadenza>>

    @Insert
    suspend fun inserisci(scadenza: Scadenza): Long

    @Delete
    suspend fun elimina(scadenza: Scadenza)

    @Query("SELECT COUNT(*) FROM scadenze")
    suspend fun conta(): Int
}
