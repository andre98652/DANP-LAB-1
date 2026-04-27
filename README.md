# Aplicación de Gestor de Tareas (Mejorada)

Este proyecto es una aplicación de Android construida con **Jetpack Compose** que te permite gestionar una lista de tareas. Recientemente ha sido actualizada para incluir persistencia de datos, filtros, edición y soporte para temas.

## Nuevas Funcionalidades 🚀

1. **Persistencia con Room Database**
   - **¿Qué es?** Las tareas ahora se guardan en el almacenamiento interno del dispositivo usando SQLite, mediante la librería oficial de Android llamada Room. Si cierras la aplicación, tus tareas seguirán ahí cuando vuelvas.
   - **¿Cómo se hizo?** Se crearon 3 componentes principales:
     - `@Entity data class Tarea`: Representa la tabla "tareas" en la base de datos.
     - `@Dao interface TareaDao`: Define las operaciones SQL (Insertar, Actualizar, Borrar y Obtener).
     - `@Database class AppDatabase`: Es el conector principal de SQLite que nos permite crear la instancia de la base de datos usando el patrón Singleton.

2. **Arquitectura con ViewModel**
   - **¿Qué es?** Se separó la lógica de la aplicación de la interfaz gráfica.
   - **¿Cómo se hizo?** Se creó la clase `TareasViewModel`. Esta clase se comunica con la base de datos a través de *Corrutinas* (operaciones en segundo plano) y expone la lista de tareas a la interfaz de usuario mediante `StateFlow`. Esto hace que la UI sea "reactiva", es decir, se actualiza automáticamente cuando la base de datos cambia.

3. **Edición de Tareas**
   - **¿Qué es?** Ahora puedes modificar el texto de una tarea que ya habías creado.
   - **¿Cómo se hizo?** Se agregó un ícono de lápiz en cada tarjeta. Al tocarlo, se guarda la tarea en una variable de estado (`tareaAEditar`) y se muestra un componente `AlertDialog`. Al guardar, el `ViewModel` llama a la función `actualizar` del DAO.

4. **Filtros de Búsqueda**
   - **¿Qué es?** Puedes filtrar la vista para ver "Todas", solo las "Pendientes" o solo las "Completadas".
   - **¿Cómo se hizo?** Se usaron componentes `FilterChip` de Material 3. El `ViewModel` almacena un estado (`filtroSeleccionado`). Usando la función `combine` de Flow, mezclamos la lista total de tareas de la base de datos con el filtro actual, devolviendo a la UI únicamente la lista ya filtrada.

5. **Tema Claro / Oscuro Automático**
   - **¿Qué es?** La app adapta sus colores dependiendo de si tu celular está en modo claro o modo oscuro.
   - **¿Cómo se hizo?** Se utilizó la función `isSystemInDarkTheme()` nativa de Compose, que lee la configuración del dispositivo y aplica `darkColorScheme()` o `lightColorScheme()` a nuestro `MaterialTheme`. Todos los componentes ahora leen de `MaterialTheme.colorScheme` para pintarse correctamente.

6. **Diseño de UI Moderno (Material 3)**
   - **¿Qué es?** Se mejoró el aspecto visual de los componentes, añadiendo sombras y bordes curvos.
   - **¿Cómo se hizo?** A la tarjeta (`Card`) de cada tarea se le agregó un `elevation` sutil y bordes redondeados (`RoundedCornerShape(16.dp)`). Además, se añadió opacidad y un texto tachado (`TextDecoration.LineThrough`) para diferenciar visualmente las tareas que ya están completadas.

## Configuración Técnica

Se añadieron las siguientes dependencias al proyecto en el archivo `libs.versions.toml`:
- **Room** (`room-runtime`, `room-ktx`, `room-compiler`) para la base de datos.
- **KSP** (Kotlin Symbol Processing) para procesar las anotaciones de Room rápidamente.
- **ViewModel Compose** para instanciar el ViewModel desde la jerarquía de vistas de Compose.

---
¡Sincroniza el proyecto en Android Studio y disfruta de tu nueva aplicación!
