package com.appfinanza.cruscotto.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.appfinanza.cruscotto.data.model.Incasso
import com.appfinanza.cruscotto.data.model.Scadenza
import com.appfinanza.cruscotto.data.model.Spesa

@Database(entities = [Incasso::class, Spesa::class, Scadenza::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun incassoDao(): IncassoDao
    abstract fun spesaDao(): SpesaDao
    abstract fun scadenzaDao(): ScadenzaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "appfinanza.db"
                )
                    .fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
            }
    }
}
