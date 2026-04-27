package com.example.lab1.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// 3. BASE DE DATOS - Punto de acceso principal a la conexión SQLite
@Database(entities = [Tarea::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    
    // Método para obtener el DAO
    abstract fun tareaDao(): TareaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Patrón Singleton para asegurarnos de que solo exista una instancia de la base de datos
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tareas_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
