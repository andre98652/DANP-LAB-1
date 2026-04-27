package com.example.lab1.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// 1. ENTIDAD (Entity) - Define la tabla en la base de datos
// Cada instancia de esta clase será una fila en la tabla "tareas"
@Entity(tableName = "tareas")
data class Tarea(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // ID autogenerado único para cada tarea
    val titulo: String,
    val completada: Boolean = false
)
