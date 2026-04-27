package com.example.lab1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lab1.data.AppDatabase
import com.example.lab1.data.Tarea
import com.example.lab1.ui.TareasViewModel

class MainActivity : ComponentActivity() {

    // 1. INICIALIZACIÓN DEL VIEWMODEL
    // Usamos un Factory para pasarle el DAO al ViewModel al momento de crearlo
    private val viewModel by viewModels<TareasViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val database = AppDatabase.getDatabase(applicationContext)
                return TareasViewModel(database.tareaDao()) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Estado para controlar el tema manualmente
            var isDarkThemeManual by remember { mutableStateOf<Boolean?>(null) }
            val isSystemDark = isSystemInDarkTheme()
            val isDarkTheme = isDarkThemeManual ?: isSystemDark

            val colorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme()

            MaterialTheme(colorScheme = colorScheme) {
                // Surface se adapta al color de fondo del tema
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppTareas(
                        viewModel = viewModel,
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = { isDarkThemeManual = !isDarkTheme }
                    )
                }
            }
        }
    }
}

// 3. COMPONENTE PRINCIPAL
@Composable
fun AppTareas(
    viewModel: TareasViewModel,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    // Recolectamos el estado desde el ViewModel usando collectAsState()
    val tareas by viewModel.tareas.collectAsState()
    val filtroSeleccionado by viewModel.filtroSeleccionado.collectAsState()
    
    var textoNuevaTarea by remember { mutableStateOf("") }
    var tareaAEditar by remember { mutableStateOf<Tarea?>(null) } // Guarda la tarea que se está editando

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        TituloApp(isDarkTheme, onThemeToggle)
        Spacer(modifier = Modifier.height(16.dp))

        // 4. CAMPO DE TEXTO Y BOTÓN PARA AGREGAR
        CampoTexto(
            valor = textoNuevaTarea,
            onValorChange = { textoNuevaTarea = it },
            label = "Nueva tarea"
        )
        Spacer(modifier = Modifier.height(8.dp))
        BotonPrimario("Agregar tarea") {
            if (textoNuevaTarea.isNotBlank()) {
                viewModel.agregarTarea(textoNuevaTarea)
                textoNuevaTarea = ""
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // 5. BARRA DE FILTROS (Todas, Pendientes, Completadas)
        FiltrosRow(
            filtroSeleccionado = filtroSeleccionado,
            onFiltroSeleccionado = { viewModel.cambiarFiltro(it) }
        )
        Spacer(modifier = Modifier.height(16.dp))

        // 6. LISTA DE TAREAS
        ListaTareas(
            tareas = tareas,
            onToggle = { viewModel.alternarCompletada(it) },
            onDelete = { viewModel.eliminarTarea(it) },
            onEdit = { tareaAEditar = it } // Al tocar editar, guardamos la tarea en el estado
        )
    }

    // 7. DIÁLOGO DE EDICIÓN
    // Si hay una tarea guardada en "tareaAEditar", mostramos el diálogo
    tareaAEditar?.let { tarea ->
        DialogoEdicion(
            tarea = tarea,
            onDismiss = { tareaAEditar = null }, // Cierra el diálogo
            onConfirm = { nuevoTitulo ->
                viewModel.actualizarTarea(tarea.copy(titulo = nuevoTitulo))
                tareaAEditar = null // Cierra el diálogo tras guardar
            }
        )
    }
}

@Composable
fun TituloApp(isDarkTheme: Boolean, onThemeToggle: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Gestor de Tareas",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        // Botón para cambiar el tema
        IconButton(onClick = onThemeToggle) {
            Icon(
                imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                contentDescription = "Cambiar Tema",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun BotonPrimario(texto: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp) // Botón con bordes redondeados
    ) {
        Text(texto)
    }
}

@Composable
fun CampoTexto(valor: String, onValorChange: (String) -> Unit, label: String, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp) // Campo de texto más moderno
    )
}

@Composable
fun FiltrosRow(filtroSeleccionado: Int, onFiltroSeleccionado: (Int) -> Unit) {
    val filtros = listOf("Todas", "Pendientes", "Completadas")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        filtros.forEachIndexed { index, texto ->
            FilterChip(
                selected = filtroSeleccionado == index,
                onClick = { onFiltroSeleccionado(index) },
                label = { Text(texto) }
            )
        }
    }
}

@Composable
fun ListaTareas(
    tareas: List<Tarea>,
    onToggle: (Tarea) -> Unit,
    onDelete: (Tarea) -> Unit,
    onEdit: (Tarea) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(tareas, key = { it.id }) { tarea -> // key optimiza el rendimiento
            ItemTarea(
                tarea = tarea,
                onToggle = { onToggle(tarea) },
                onDelete = { onDelete(tarea) },
                onEdit = { onEdit(tarea) }
            )
        }
    }
}

@Composable
fun ItemTarea(tarea: Tarea, onToggle: () -> Unit, onDelete: () -> Unit, onEdit: () -> Unit) {
    // Tarjeta con diseño mejorado, sombras y bordes redondeados
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (tarea.completada) 
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) 
            else 
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = tarea.completada,
                onCheckedChange = { onToggle() }
            )
            Spacer(modifier = Modifier.width(8.dp))
            
            // Texto tachado si está completada
            Text(
                text = tarea.titulo,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = if (tarea.completada) TextDecoration.LineThrough else TextDecoration.None,
                color = if (tarea.completada) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // Botón Editar
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
            }
            // Botón Eliminar
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun DialogoEdicion(tarea: Tarea, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var textoEditado by remember { mutableStateOf(tarea.titulo) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Tarea") },
        text = {
            OutlinedTextField(
                value = textoEditado,
                onValueChange = { textoEditado = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = { 
                    if (textoEditado.isNotBlank()) onConfirm(textoEditado) 
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
