package com.example.lab1.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab1.data.Tarea
import com.example.lab1.data.TareaDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// 4. VIEWMODEL - Separa la lógica de la UI
// Permite que la UI solo "escuche" y se actualice automáticamente cuando los datos cambien
class TareasViewModel(private val dao: TareaDao) : ViewModel() {

    // Estado para saber qué filtro está activo (0 = Todas, 1 = Pendientes, 2 = Completadas)
    private val _filtroSeleccionado = MutableStateFlow(0)
    val filtroSeleccionado: StateFlow<Int> = _filtroSeleccionado

    // Combina el flujo de la base de datos con el estado del filtro
    val tareas: StateFlow<List<Tarea>> = combine(
        dao.obtenerTodas(),
        _filtroSeleccionado
    ) { listaTareas, filtro ->
        when (filtro) {
            1 -> listaTareas.filter { !it.completada } // Pendientes
            2 -> listaTareas.filter { it.completada }  // Completadas
            else -> listaTareas                        // Todas
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Funciones para interactuar con la Base de Datos (CRUD)

    fun agregarTarea(titulo: String) {
        viewModelScope.launch {
            dao.insertar(Tarea(titulo = titulo))
        }
    }

    fun actualizarTarea(tarea: Tarea) {
        viewModelScope.launch {
            dao.actualizar(tarea)
        }
    }

    fun eliminarTarea(tarea: Tarea) {
        viewModelScope.launch {
            dao.eliminar(tarea)
        }
    }

    fun alternarCompletada(tarea: Tarea) {
        viewModelScope.launch {
            dao.actualizar(tarea.copy(completada = !tarea.completada))
        }
    }

    fun cambiarFiltro(indice: Int) {
        _filtroSeleccionado.value = indice
    }
}
