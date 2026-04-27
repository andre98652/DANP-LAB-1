package com.example.lab1.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// 2. DAO (Data Access Object) - Define las operaciones en la base de datos
// Room generará el código SQL para estas funciones automáticamente
@Dao
interface TareaDao {
    
    // Obtener todas las tareas y devolverlas como un "Flow". 
    // Flow es reactivo: si la base de datos cambia, la UI se actualizará automáticamente.
    @Query("SELECT * FROM tareas ORDER BY id DESC")
    fun obtenerTodas(): Flow<List<Tarea>>

    // Insertar una tarea nueva. Si ya existe (por ID), la reemplaza.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(tarea: Tarea)

    // Actualizar una tarea existente (ej. marcar como completada o cambiar título)
    @Update
    suspend fun actualizar(tarea: Tarea)

    // Eliminar una tarea
    @Delete
    suspend fun eliminar(tarea: Tarea)
}
