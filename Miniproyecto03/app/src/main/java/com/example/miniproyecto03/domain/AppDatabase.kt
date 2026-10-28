package com.example.miniproyecto03.domain
import android.content.Context
import androidx.room.*
import com.example.miniproyecto03.data.Publicacion
import com.example.miniproyecto03.data.PublicacionDao

@Database(entities = [Publicacion::class], version = 1)

abstract class AppDatabase : RoomDatabase() {
    abstract fun publicacionDao(): PublicacionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "RedSocialCURLS"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}