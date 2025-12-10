# 🎯 GUÍA DE ESTUDIO: EXPOSICIÓN INTERFAZ GRÁFICA (JavaFX)

## 📋 ÍNDICE DE TU EXPOSICIÓN

1. **Introducción** (1 minuto)
2. **Arquitectura MVC** (2 minutos)
3. **Las 5 Pestañas del Sistema** (5 minutos) ⭐ PARTE PRINCIPAL
4. **Componentes JavaFX Utilizados** (2 minutos)
5. **Demo en Vivo** (3 minutos)
6. **Conclusiones** (1 minuto)

---

## 1️⃣ INTRODUCCIÓN (1 minuto)

### QUÉ DECIR:
*"Buenos días/tardes. Hoy les voy a presentar la **Interfaz Gráfica** de nuestro Sistema de Gestión de Torneos Deportivos, desarrollada con **JavaFX**."*

*"La interfaz está diseñada para ser intuitiva y fácil de usar, permitiendo gestionar todas las fases del torneo desde una sola ventana con 5 pestañas principales."*

### QUÉ MOSTRAR:
- Captura de la ventana principal con las 5 pestañas visibles
- Mencionar: **900x600 píxeles**, **5 pestañas**, **estilo CSS personalizado**

### PUNTOS CLAVE:
- ✅ Interfaz gráfica con JavaFX 21.0.1
- ✅ No usa consola, 100% visual
- ✅ Organizada en pestañas para fácil navegación
- ✅ Diseño limpio y profesional

---

## 2️⃣ ARQUITECTURA MVC (2 minutos)

### QUÉ DECIR:
*"El sistema implementa el patrón **MVC (Modelo-Vista-Controlador)**, que separa la lógica de negocio de la interfaz gráfica."*

### EXPLICAR LAS 3 CAPAS:

**MODELO (Model):**
- *"Contiene las clases de negocio: Torneo, Equipo, Jugador, Partido, Eliminatoria"*
- *"Maneja toda la lógica: validaciones, cálculos de puntos, clasificación"*

**VISTA (View):**
- *"Son las clases JavaFX que construyen la interfaz gráfica"*
- *"Tenemos 5 vistas principales: VistaTorneo, VistaEquipos, VistaPartidos, VistaTabla, VistaEliminatoria"*
- *"Cada vista hereda de VistaBase para reutilizar código"*

**CONTROLADOR (Controller):**
- *"Actúa como intermediario entre la vista y el modelo"*
- *"Por ejemplo: PartidosController maneja el registro de resultados"*

### BENEFICIOS DEL MVC:
- ✅ Código más organizado y mantenible
- ✅ Separación de responsabilidades
- ✅ Fácil de modificar la interfaz sin tocar la lógica
- ✅ Múltiples vistas pueden usar el mismo modelo

---

## 3️⃣ LAS 5 PESTAÑAS DEL SISTEMA ⭐ (5 minutos)

### 🏆 PESTAÑA 1: TORNEO

**QUÉ DECIR:**
*"La pestaña Torneo es donde se configura y comienza el torneo."*

**ELEMENTOS DE LA INTERFAZ:**
- Etiqueta con el nombre del torneo: "Copa Universitaria"
- Botón **"Sortear Grupos"**
- Botón **"Generar Partidos"**
- Botón **"Clasificar 8 Mejores"**
- Área de texto mostrando los grupos creados

**FUNCIONALIDADES:**
1. **Sortear Grupos:**
   - *"Al hacer clic, el sistema valida que haya mínimo 12 equipos"*
   - *"Si hay menos, muestra error: 'Se necesitan mínimo 12 equipos'"*
   - *"Si está bien, crea 3 grupos (A, B, C) con 4 equipos cada uno de forma aleatoria"*
   - *"Solo se puede hacer una vez, no permite repetir el sorteo"*

2. **Generar Partidos:**
   - *"Crea automáticamente 18 partidos usando sistema round-robin"*
   - *"Cada equipo juega contra todos los de su grupo"*
   - *"6 partidos por grupo"*

3. **Clasificar 8 Mejores:**
   - *"Se habilita cuando todos los partidos de grupos están jugados"*
   - *"Clasifica automáticamente: 2 primeros de cada grupo + 2 mejores terceros"*

**MOSTRAR:** Captura `img/image-1.png`

---

### 👥 PESTAÑA 2: EQUIPOS

**QUÉ DECIR:**
*"Esta pestaña permite gestionar equipos y jugadores."*

**ELEMENTOS DE LA INTERFAZ:**
- **Tabla de equipos** con columnas: Nombre, Grupo, Jugadores, Puntos, PJ, PG, PE, PP, GF, GC, Dif
- Botón **"Agregar Equipo"**
- Botón **"Agregar Jugador a Equipo"**
- Botón **"Ver Jugadores del Equipo"**

**FUNCIONALIDADES:**

1. **Agregar Equipo:**
   - *"Muestra un diálogo con campo de texto"*
   - *"Valida que el nombre sea solo letras"*
   - *"Si tiene números o caracteres especiales: rechaza"*
   - *"Si el nombre ya existe: muestra error 'Ya existe un equipo con ese nombre'"*

2. **Agregar Jugador:**
   - *"Primero seleccionas un equipo de la tabla"*
   - *"Se abre formulario con campos: Nombre, Edad, Posición, Número"*
   - **Validaciones importantes:**
     - Edad entre 10 y 50 años
     - Número entre 1 y 99, único por equipo
     - Posición: Portero, Defensa, Mediocampista, Delantero
   - *"Si falla alguna validación, muestra mensaje de error claro"*

3. **Ver Jugadores:**
   - *"Muestra diálogo con lista de todos los jugadores del equipo"*
   - *"Incluye: nombre, posición, número, goles, tarjetas"*

**MOSTRAR:** Captura `img/image.png` y `img/image_jugadores.png`

---

### ⚽ PESTAÑA 3: PARTIDOS

**QUÉ DECIR:**
*"Aquí se registran los resultados de todos los partidos."*

**ELEMENTOS DE LA INTERFAZ:**
- **ComboBox para filtrar** por fase: Todos, Grupos, Cuartos, Semifinales, Final, Tercer Puesto
- **Tabla de partidos** con: Fase, Local, Visitante, Goles Local, Goles Visitante, Estado, Penales
- Botón **"Registrar Resultado"**

**FUNCIONALIDADES:**

1. **Filtrar Partidos:**
   - *"El ComboBox permite ver solo los partidos de una fase específica"*
   - *"Por defecto muestra todos"*

2. **Registrar Resultado:**
   - *"Seleccionas un partido no jugado"*
   - *"Se abre formulario completo con:"*
     - Campos para goles del local y visitante
     - Botón "Agregar Gol" para especificar qué jugador anotó
     - Botón "Agregar Tarjeta Amarilla"
     - Botón "Agregar Tarjeta Roja"
   
3. **Registro Detallado:**
   - *"Al agregar un gol, seleccionas el jugador que lo hizo"*
   - *"Si el jugador está suspendido, no aparece en la lista"*
   - *"Las tarjetas se registran por jugador individual"*
   - *"Si un jugador acumula 2 amarillas: se marca como suspendido automáticamente"*

4. **Actualización Automática:**
   - *"Al confirmar el resultado, el sistema:"*
     - Marca el partido como jugado
     - Actualiza puntos (3 victoria, 1 empate, 0 derrota)
     - Actualiza goles a favor y en contra
     - Actualiza estadísticas de jugadores
     - Recalcula tabla de posiciones en tiempo real

**MOSTRAR:** Capturas `img/image-2.png` y `img/image_fase.png`

---

### 📊 PESTAÑA 4: TABLA

**QUÉ DECIR:**
*"Esta pestaña muestra todas las estadísticas del torneo."*

**ELEMENTOS DE LA INTERFAZ:**
- **ComboBox** para seleccionar: Tabla General, Grupo A, Grupo B, Grupo C
- **Tabla de posiciones** con: Pos, Equipo, PJ, PG, PE, PP, GF, GC, Dif, Pts
- Sección **"Goleadores del Torneo"**: Jugador, Equipo, Goles
- Sección **"Jugadores Suspendidos"**: Jugador, Equipo, Motivo

**FUNCIONALIDADES:**

1. **Tabla de Posiciones:**
   - *"Se actualiza automáticamente después de cada partido"*
   - *"Ordenada por:"*
     1. Mayor cantidad de puntos
     2. Mayor diferencia de goles
     3. Mayor cantidad de goles a favor
     4. Orden alfabético

2. **Tabla de Goleadores:**
   - *"Lista de jugadores ordenados por cantidad de goles"*
   - *"Muestra el top 10"*
   - *"Se actualiza en tiempo real cuando se registra un gol"*

3. **Jugadores Suspendidos:**
   - *"Muestra quién no puede jugar el siguiente partido"*
   - *"Indica el motivo: 2 amarillas o 1 roja"*

**MOSTRAR:** Captura `img/image_tabla.png`

---

### 🏅 PESTAÑA 5: ELIMINATORIAS

**QUÉ DECIR:**
*"Aquí se visualiza y gestiona toda la fase eliminatoria del torneo."*

**ELEMENTOS DE LA INTERFAZ:**
- Sección **"Cuartos de Final"** con 4 partidos
- Sección **"Semifinales"** con 2 partidos
- Sección **"Final"** con 1 partido
- Sección **"Tercer Puesto"** con 1 partido
- Botón **"Sortear Cuartos"**

**FUNCIONALIDADES:**

1. **Árbol de Llaves:**
   - *"Muestra visualmente todos los enfrentamientos"*
   - *"Cada partido muestra: equipos, resultado, estado"*
   - *"Los ganadores se destacan visualmente"*

2. **Sortear Cuartos:**
   - *"Se habilita cuando todos los partidos de grupos están jugados"*
   - *"Distribuye los 8 clasificados en 4 partidos"*
   - *"Evita que equipos del mismo grupo se enfrenten en cuartos"*

3. **Avance Automático:**
   - *"Cuando los 4 cuartos finalizan: genera semifinales automáticamente"*
   - *"Cuando las 2 semis finalizan: genera final y tercer puesto"*
   - *"El sistema lo hace solo, sin intervención del usuario"*

4. **Penales:**
   - *"En fase eliminatoria NO puede haber empates"*
   - *"Si el marcador está igualado, se solicita obligatoriamente el resultado de penales"*
   - *"El ganador por penales avanza a la siguiente fase"*

**MOSTRAR:** Captura `img/image_eliminatorias.png`

---

## 4️⃣ COMPONENTES JAVAFX UTILIZADOS (2 minutos)

### QUÉ DECIR:
*"Para construir toda esta interfaz, utilizamos diversos componentes de JavaFX:"*

### LISTA DE COMPONENTES:

**CONTENEDORES:**
- **TabPane:** Para organizar las 5 pestañas principales
- **VBox / HBox:** Para organizar elementos vertical y horizontalmente
- **BorderPane:** Para la estructura general de cada vista
- **GridPane:** Para formularios con campos alineados

**CONTROLES:**
- **Button:** Todos los botones de acción (Sortear, Generar, Agregar, etc.)
- **Label:** Textos y etiquetas
- **TextField:** Campos de entrada de texto
- **TextArea:** Áreas de texto grandes (info de grupos)
- **ComboBox:** Listas desplegables (filtros, posiciones)
- **TableView:** Todas las tablas (equipos, partidos, posiciones, goleadores)
- **ListView:** Listas simples (suspendidos, eventos)

**DIÁLOGOS:**
- **TextInputDialog:** Para entrada simple (nombre de equipo)
- **Dialog personalizado:** Para formularios complejos (agregar jugador, registrar resultado)
- **Alert:** Mensajes de error, confirmación y éxito

**ESTILO:**
- **CSS personalizado:** En `src/main/resources/com/ejemplo/view/style.css`
- Colores, espaciado, fuentes personalizadas

### CÓDIGO EJEMPLO A MENCIONAR:
```java
// Creación de la estructura principal
TabPane tabPane = new TabPane();
tabPane.getTabs().addAll(
    new Tab("Torneo", new VistaTorneo(torneo)),
    new Tab("Equipos", new VistaEquipos(torneo)),
    new Tab("Partidos", new VistaPartidos(torneo)),
    new Tab("Tabla", new VistaTabla(torneo)),
    new Tab("Eliminatorias", new VistaEliminatoria(torneo))
);
```

---

## 5️⃣ DEMO EN VIVO (3 minutos)

### QUÉ HACER:

**PREPARACIÓN:**
- Tener la aplicación abierta antes de empezar
- Tener datos precargados (12 equipos con jugadores)

**SECUENCIA DE LA DEMO:**

1. **Mostrar Pestaña Torneo:**
   - Hacer clic en "Sortear Grupos"
   - Mostrar que se crean 3 grupos
   - Hacer clic en "Generar Partidos"
   - Mencionar que se crearon 18 partidos

2. **Ir a Pestaña Equipos:**
   - Seleccionar un equipo
   - Hacer clic en "Ver Jugadores"
   - Mostrar la lista de jugadores

3. **Ir a Pestaña Partidos:**
   - Seleccionar un partido
   - Hacer clic en "Registrar Resultado"
   - Registrar un resultado rápido (ej: 2-1)
   - Mostrar que se actualiza el estado a "Jugado"

4. **Ir a Pestaña Tabla:**
   - Mostrar que la tabla se actualizó automáticamente
   - Cambiar el filtro a "Grupo A"
   - Mostrar los goleadores

5. **Ir a Pestaña Eliminatorias:**
   - Mostrar la estructura del árbol vacía o con cuartos

### FRASES PARA LA DEMO:
- *"Como pueden ver, la interfaz es muy intuitiva..."*
- *"Aquí hacemos clic y el sistema automáticamente..."*
- *"Observen cómo se actualiza en tiempo real..."*
- *"El usuario no necesita programar nada, todo es visual..."*

---

## 🔧 MODIFICACIONES EN VIVO (SI EL PROFESOR PIDE)

### ESCENARIO 1: Cambiar el Título de la Ventana

**SI PIDEN:** *"Cambia el título de la ventana principal"*

**ARCHIVO:** `src/main/java/com/ejemplo/view/Main.java`

**LÍNEA 19:** 
```java
stage.setTitle("⚽ Gestor de Torneos - JavaFX");
```

**CAMBIAR A:**
```java
stage.setTitle("🏆 Sistema de Torneos Deportivos");
```

**QUÉ DECIR:**
*"Voy a modificar la clase Main.java donde se configura la ventana principal. En la línea 19, cambio el método setTitle con el nuevo texto. Guardo, recompilo y ejecuto."*

**COMANDO:**
```bash
mvn clean javafx:run
```

---

### ESCENARIO 2: Cambiar el Tamaño de la Ventana

**SI PIDEN:** *"Haz la ventana más grande"*

**ARCHIVO:** `src/main/java/com/ejemplo/view/Main.java`

**LÍNEA 17:**
```java
Scene scene = new Scene(panelPrincipal, 900, 600);
```

**CAMBIAR A:**
```java
Scene scene = new Scene(panelPrincipal, 1200, 800);
```

**QUÉ DECIR:**
*"En la clase Main, línea 17, modifico los parámetros del constructor Scene. El primer número es el ancho (900 a 1200 píxeles) y el segundo es el alto (600 a 800 píxeles)."*

---

### ESCENARIO 3: Agregar un Nuevo Botón a una Pestaña

**SI PIDEN:** *"Agrega un botón en la pestaña Torneo"*

**ARCHIVO:** `src/main/java/com/ejemplo/view/VistaTorneo.java`

**BUSCAR la sección donde están los botones (alrededor de línea 30-50):**
```java
Button btnSortear = new Button("Sortear Grupos");
Button btnGenerar = new Button("Generar Partidos");
```

**AGREGAR DESPUÉS:**
```java
Button btnNuevo = new Button("Reiniciar Torneo");
btnNuevo.setOnAction(e -> {
    DialogosFX.mostrarInformacion("Acción", "Funcionalidad en desarrollo");
});
```

**Y AGREGAR AL LAYOUT:**
```java
// Buscar donde se agregan los botones al HBox o VBox
botonesBox.getChildren().add(btnNuevo);
```

**QUÉ DECIR:**
*"Voy a la clase VistaTorneo. Creo un nuevo Button con el texto deseado, le asigno un manejador de eventos con setOnAction, y lo agrego al contenedor de botones. Por ahora solo muestra un mensaje."*

---

### ESCENARIO 4: Modificar una Validación

**SI PIDEN:** *"Cambia la edad mínima de jugadores a 15 años"*

**ARCHIVO:** `src/main/java/com/ejemplo/model/Jugador.java`

**BUSCAR el método setEdad (alrededor de línea 40-50):**
```java
public void setEdad(int edad) {
    if (edad < 10 || edad > 50) {
        throw new IllegalArgumentException("La edad debe estar entre 10 y 50 años.");
    }
    this.edad = edad;
}
```

**CAMBIAR A:**
```java
public void setEdad(int edad) {
    if (edad < 15 || edad > 50) {
        throw new IllegalArgumentException("La edad debe estar entre 15 y 50 años.");
    }
    this.edad = edad;
}
```

**QUÉ DECIR:**
*"La validación está en la clase del modelo Jugador.java, en el método setEdad. Cambio la condición de 10 a 15 en la línea XX. También actualizo el mensaje de error para que sea consistente."*

---

### ESCENARIO 5: Cambiar Puntos por Victoria

**SI PIDEN:** *"Cambia los puntos por victoria de 3 a 5"*

**ARCHIVO:** `src/main/java/com/ejemplo/model/Partido.java`

**BUSCAR el método registrarResultado (alrededor de línea 100-150):**
```java
if (golesLocal > golesVisitante) {
    equipoLocal.setPuntos(equipoLocal.getPuntos() + 3);
    equipoLocal.setGanados(equipoLocal.getGanados() + 1);
} else if (golesVisitante > golesLocal) {
    equipoVisitante.setPuntos(equipoVisitante.getPuntos() + 3);
    equipoVisitante.setGanados(equipoVisitante.getGanados() + 1);
}
```

**CAMBIAR LOS +3 A +5:**
```java
if (golesLocal > golesVisitante) {
    equipoLocal.setPuntos(equipoLocal.getPuntos() + 5);
    equipoLocal.setGanados(equipoLocal.getGanados() + 1);
} else if (golesVisitante > golesLocal) {
    equipoVisitante.setPuntos(equipoVisitante.getPuntos() + 5);
    equipoVisitante.setGanados(equipoVisitante.getGanados() + 1);
}
```

**QUÉ DECIR:**
*"La lógica de puntos está en la clase Partido, método registrarResultado. Busco donde se asignan los 3 puntos por victoria y los cambio a 5. Hay dos lugares: cuando gana el local y cuando gana el visitante."*

---

### ESCENARIO 6: Agregar una Nueva Columna a la Tabla

**SI PIDEN:** *"Agrega una columna de 'Amarillas' en la tabla de equipos"*

**ARCHIVO:** `src/main/java/com/ejemplo/view/VistaEquipos.java`

**BUSCAR donde se crean las columnas (alrededor de línea 40-80):**
```java
TableColumn<Equipo, String> colNombre = new TableColumn<>("Nombre");
TableColumn<Equipo, Integer> colPuntos = new TableColumn<>("Puntos");
// ... más columnas
```

**AGREGAR NUEVA COLUMNA:**
```java
TableColumn<Equipo, Integer> colAmarillas = new TableColumn<>("Amarillas");
colAmarillas.setCellValueFactory(cellData -> {
    int totalAmarillas = cellData.getValue().getJugadores().stream()
        .mapToInt(j -> j.getTarjetasAmarillasAcumuladas())
        .sum();
    return new SimpleIntegerProperty(totalAmarillas).asObject();
});
```

**Y AGREGARLA A LA TABLA:**
```java
tablaEquipos.getColumns().add(colAmarillas);
```

**QUÉ DECIR:**
*"Voy a VistaEquipos donde se define la tabla. Creo una nueva TableColumn llamada 'Amarillas'. Uso setCellValueFactory para calcular la suma de tarjetas amarillas de todos los jugadores del equipo. Finalmente la agrego a la tabla."*

---

### ESCENARIO 7: Corregir un Error de NullPointerException

**SI APARECE ERROR:** *"NullPointerException al registrar resultado"*

**QUÉ HACER:**

1. **Leer el stack trace en la consola** para identificar la línea exacta

2. **ARCHIVO PROBABLE:** `src/main/java/com/ejemplo/view/VistaPartidos.java`

3. **BUSCAR código como:**
```java
Partido partido = tablaPartidos.getSelectionModel().getSelectedItem();
partido.registrarResultado(golesLocal, golesVisitante);
```

4. **AGREGAR VALIDACIÓN:**
```java
Partido partido = tablaPartidos.getSelectionModel().getSelectedItem();
if (partido == null) {
    DialogosFX.mostrarError("Error", "Debe seleccionar un partido");
    return;
}
partido.registrarResultado(golesLocal, golesVisitante);
```

**QUÉ DECIR:**
*"El error NullPointerException indica que estamos intentando usar un objeto que es null. Veo en el stack trace que está en la línea XX. Agrego una validación para verificar que el partido seleccionado no sea null antes de usarlo, y muestro un mensaje de error al usuario."*

---

### ESCENARIO 8: Cambiar Color de un Botón

**SI PIDEN:** *"Cambia el color del botón Sortear Grupos a verde"*

**OPCIÓN 1 - Inline CSS (Más Rápido):**

**ARCHIVO:** `src/main/java/com/ejemplo/view/VistaTorneo.java`

**BUSCAR:**
```java
Button btnSortear = new Button("Sortear Grupos");
```

**AGREGAR:**
```java
Button btnSortear = new Button("Sortear Grupos");
btnSortear.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
```

**OPCIÓN 2 - CSS Externo:**

**ARCHIVO:** `src/main/resources/com/ejemplo/view/style.css`

**AGREGAR:**
```css
.boton-sortear {
    -fx-background-color: #4CAF50;
    -fx-text-fill: white;
}
```

**Y EN VistaTorneo.java:**
```java
btnSortear.getStyleClass().add("boton-sortear");
```

**QUÉ DECIR:**
*"Hay dos formas: usar CSS inline con setStyle directamente en el código Java, o definir una clase CSS en el archivo style.css. Voy a usar inline porque es más rápido. Seteo el background-color a verde (#4CAF50) y el texto a blanco."*

---

### ESCENARIO 9: Agregar un MessageBox/Alert

**SI PIDEN:** *"Muestra un mensaje de confirmación antes de sortear grupos"*

**ARCHIVO:** `src/main/java/com/ejemplo/view/VistaTorneo.java`

**BUSCAR el evento del botón Sortear:**
```java
btnSortear.setOnAction(e -> {
    try {
        torneo.crearGrupos();
        actualizarVista();
    } catch (Exception ex) {
        DialogosFX.mostrarError("Error", ex.getMessage());
    }
});
```

**MODIFICAR A:**
```java
btnSortear.setOnAction(e -> {
    boolean confirmado = DialogosFX.mostrarConfirmacion(
        "Confirmar Sorteo",
        "¿Está seguro de realizar el sorteo de grupos? Esta acción no se puede deshacer."
    );
    
    if (!confirmado) {
        return;
    }
    
    try {
        torneo.crearGrupos();
        actualizarVista();
        DialogosFX.mostrarInformacion("Éxito", "Grupos sorteados correctamente");
    } catch (Exception ex) {
        DialogosFX.mostrarError("Error", ex.getMessage());
    }
});
```

**QUÉ DECIR:**
*"Antes de ejecutar el sorteo, llamo al método mostrarConfirmacion de DialogosFX que retorna true o false. Si el usuario cancela, hago return. Si confirma, ejecuto el sorteo y muestro un mensaje de éxito."*

---

## 🎯 ESTRATEGIA PARA MODIFICACIONES EN VIVO

### PASOS A SEGUIR:

1. **ESCUCHAR BIEN** lo que pide el profesor
2. **IDENTIFICAR** qué clase y método afectar
3. **EXPLICAR** en voz alta qué vas a hacer ANTES de hacerlo
4. **ABRIR** el archivo correcto en VS Code
5. **BUSCAR** la línea específica (Ctrl+G para ir a línea)
6. **MODIFICAR** con confianza
7. **GUARDAR** (Ctrl+S)
8. **COMPILAR Y EJECUTAR:** `mvn clean javafx:run`
9. **DEMOSTRAR** que funcionó

### FRASES CLAVE:

- *"Perfecto, voy a modificar la clase [NOMBRE] en la línea [XX]"*
- *"Esta validación/funcionalidad está en el modelo/vista [NOMBRE]"*
- *"Voy a cambiar este valor de [X] a [Y]"*
- *"Guardo, recompilo con Maven y ejecuto"*
- *"Como pueden ver, el cambio se aplicó correctamente"*

### ARCHIVOS CLAVE A TENER ABIERTOS:

✅ `Main.java` - Configuración inicial
✅ `VistaTorneo.java` - Pestaña principal
✅ `VistaEquipos.java` - Gestión de equipos
✅ `Jugador.java` - Validaciones de jugadores
✅ `Partido.java` - Lógica de partidos y puntos

### SI NO SABES DÓNDE ESTÁ ALGO:

1. **Usar Ctrl+P** en VS Code y buscar el nombre de la clase
2. **Usar Ctrl+F** para buscar dentro del archivo
3. **Decir:** *"Voy a buscar esa funcionalidad en el código..."* (mientras buscas)
4. **NO ENTRAR EN PÁNICO**, tómate 10 segundos para buscar

---

## 6️⃣ CONCLUSIONES (1 minuto)

### QUÉ DECIR:
*"Para finalizar, quiero destacar los puntos clave de nuestra interfaz gráfica:"*

**VENTAJAS:**
1. **Intuitiva:** Cualquier persona puede usarla sin manual
2. **Completa:** Cubre todas las funcionalidades del sistema
3. **Visual:** No requiere conocimientos de programación
4. **Actualización en tiempo real:** Los cambios se reflejan inmediatamente
5. **Validaciones claras:** Mensajes de error comprensibles
6. **Organizada:** Las 5 pestañas separan funcionalidades lógicamente

*"La interfaz JavaFX nos permite tener un sistema profesional, fácil de usar y visualmente atractivo."*

*"¿Tienen alguna pregunta?"*

---

## 🎓 PREGUNTAS FRECUENTES QUE PUEDEN HACERTE

### P1: ¿Por qué eligieron JavaFX en lugar de Swing?
**RESPONDER:**
- JavaFX es más moderno (Swing es muy antiguo)
- Mejor soporte para CSS y estilos
- Componentes más elegantes y profesionales
- Mejor rendimiento gráfico
- Es el estándar actual para aplicaciones de escritorio en Java

### P2: ¿Cómo se comunica la vista con el modelo?
**RESPONDER:**
- Usamos el patrón MVC
- Cada vista recibe una instancia del modelo (Torneo)
- Cuando el usuario hace clic, la vista llama métodos del modelo
- Ejemplo: `torneo.crearGrupos()` se llama desde `VistaTorneo`
- El modelo hace la lógica y retorna resultados
- La vista actualiza la interfaz con esos resultados

### P3: ¿Cómo manejan las validaciones?
**RESPONDER:**
- Validaciones en dos niveles:
  1. **En la interfaz:** No permite ingresar texto en campos numéricos
  2. **En el modelo:** Valida datos antes de guardar
- Si falla, el modelo lanza excepción o retorna false
- La vista captura el error y muestra Alert con mensaje claro

### P4: ¿La interfaz guarda datos en base de datos?
**RESPONDER:**
- No, los datos se mantienen en memoria (RAM)
- Al cerrar la aplicación, se pierden
- Fue decisión de diseño para simplificar
- En producción se podría agregar persistencia

### P5: ¿Cuántas líneas de código tiene la interfaz?
**RESPONDER:**
- Aproximadamente 2,500-3,000 líneas en las 5 vistas
- VistaTorneo: ~200 líneas
- VistaEquipos: ~300 líneas
- VistaPartidos: ~600 líneas (la más compleja)
- VistaTabla: ~400 líneas
- VistaEliminatoria: ~450 líneas
- Más clases auxiliares como DialogosFX

---

## 📝 TIPS PARA LA EXPOSICIÓN

### ANTES DE EXPONER:
✅ Practica varias veces frente al espejo
✅ Ejecuta la aplicación 2-3 veces para familiarizarte
✅ Ten todas las capturas abiertas y ordenadas
✅ Ensaya la demo en vivo al menos 3 veces
✅ Prepara la aplicación corriendo antes de tu turno

### DURANTE LA EXPOSICIÓN:
✅ Habla con confianza, es TU proyecto
✅ Mira al público, no solo a la pantalla
✅ Usa las manos para señalar elementos en pantalla
✅ No te apures, habla tranquilo y claro
✅ Si te quedas en blanco, ve a la demo en vivo

### LENGUAJE CORPORAL:
✅ Párate derecho, no te encorves
✅ Sonríe, muestra que te gusta tu proyecto
✅ Haz contacto visual con todos
✅ Muévete, no te quedes estático

### FRASES COMODÍN SI TE TRABAS:
- *"Como pueden ver aquí..."* (mientras señalas)
- *"Un punto importante es que..."*
- *"Lo interesante de esto es..."*
- *"Permítanme mostrarles..."* (haces demo)

---

## ⏱️ CRONOGRAMA DE 15 MINUTOS

| Minuto | Sección | Qué hacer |
|--------|---------|-----------|
| 0-1 | Introducción | Presentación general, mencionar JavaFX |
| 1-3 | MVC | Explicar arquitectura, 3 capas |
| 3-4 | Pestaña Torneo | Sortear grupos, generar partidos |
| 4-5 | Pestaña Equipos | Agregar equipo, validaciones |
| 5-6 | Pestaña Partidos | Registrar resultado, tarjetas |
| 6-7 | Pestaña Tabla | Posiciones, goleadores, suspendidos |
| 7-8 | Pestaña Eliminatorias | Árbol, penales, avance automático |
| 8-10 | Componentes JavaFX | Listar componentes usados |
| 10-13 | Demo en Vivo | Ejecutar operaciones reales |
| 13-14 | Conclusiones | Resumir ventajas |
| 14-15 | Preguntas | Responder dudas |

---

## 💡 CONCEPTOS TÉCNICOS IMPORTANTES

### ¿Qué es JavaFX?
JavaFX es un framework moderno de Java para crear interfaces gráficas de usuario (GUI). Es el sucesor de Swing y permite crear aplicaciones de escritorio con diseños profesionales y modernos.

**Características principales:**
- Soporte para CSS (como en páginas web)
- Binding de propiedades (actualización automática)
- Animaciones y efectos visuales
- Arquitectura basada en Scene Graph
- Multiplataforma (Windows, Mac, Linux)

### ¿Qué es el Patrón MVC?

**MVC = Model-View-Controller**

Es un patrón de diseño que separa la aplicación en 3 capas:

```
    USUARIO
       ↓
    VISTA (View)
       ↕ 
  CONTROLADOR (Controller)
       ↕
    MODELO (Model)
```

**VENTAJAS:**
- ✅ Código más organizado y mantenible
- ✅ Facilita el trabajo en equipo (cada uno en una capa)
- ✅ Permite cambiar la interfaz sin tocar la lógica
- ✅ Facilita las pruebas unitarias

### Scene Graph en JavaFX

JavaFX organiza los elementos visuales en un árbol jerárquico llamado Scene Graph:

```
Stage (Ventana)
  └─ Scene (Escena)
      └─ TabPane (Contenedor de pestañas)
          ├─ Tab "Torneo"
          │   └─ VistaTorneo (VBox)
          │       ├─ Label
          │       ├─ Button
          │       └─ TextArea
          ├─ Tab "Equipos"
          │   └─ VistaEquipos (BorderPane)
          │       ├─ TableView
          │       └─ HBox (botones)
          └─ ... (más tabs)
```

**IMPORTANTE:** Cada nodo tiene un solo padre, pero puede tener múltiples hijos.

---

## 🎨 COMPONENTES JAVAFX EN DETALLE

### 1. CONTENEDORES (Layouts)

**VBox - Vertical Box**
```java
VBox vbox = new VBox(10); // 10 = espaciado entre elementos
vbox.getChildren().addAll(label, button, textField);
// Apila elementos verticalmente
```

**HBox - Horizontal Box**
```java
HBox hbox = new HBox(5);
hbox.getChildren().addAll(btn1, btn2, btn3);
// Apila elementos horizontalmente
```

**BorderPane**
```java
BorderPane border = new BorderPane();
border.setTop(menuBar);
border.setCenter(contenido);
border.setBottom(statusBar);
// Divide en 5 regiones: Top, Bottom, Left, Right, Center
```

**GridPane - Para Formularios**
```java
GridPane grid = new GridPane();
grid.add(labelNombre, 0, 0);  // columna 0, fila 0
grid.add(textNombre, 1, 0);   // columna 1, fila 0
grid.add(labelEdad, 0, 1);    // columna 0, fila 1
grid.add(textEdad, 1, 1);     // columna 1, fila 1
// Organiza en cuadrícula como tabla
```

### 2. CONTROLES BÁSICOS

**Button**
```java
Button btn = new Button("Guardar");
btn.setOnAction(e -> {
    // Código al hacer clic
});
```

**TextField - Entrada de Texto**
```java
TextField nombre = new TextField();
nombre.setPromptText("Ingrese el nombre"); // placeholder
String texto = nombre.getText(); // obtener valor
```

**ComboBox - Lista Desplegable**
```java
ComboBox<String> combo = new ComboBox<>();
combo.getItems().addAll("Opción 1", "Opción 2", "Opción 3");
combo.setValue("Opción 1"); // selección inicial
String seleccion = combo.getValue(); // obtener selección
```

**TableView - Tablas**
```java
TableView<Equipo> tabla = new TableView<>();

TableColumn<Equipo, String> colNombre = new TableColumn<>("Nombre");
colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

tabla.getColumns().add(colNombre);
tabla.setItems(listaEquipos); // ObservableList
```

### 3. DIÁLOGOS

**Alert - Mensajes Simples**
```java
Alert alert = new Alert(Alert.AlertType.INFORMATION);
alert.setTitle("Éxito");
alert.setContentText("Operación completada");
alert.showAndWait();
```

**TextInputDialog - Entrada Simple**
```java
TextInputDialog dialog = new TextInputDialog("Valor inicial");
dialog.setHeaderText("Ingrese el nombre del equipo:");
Optional<String> resultado = dialog.showAndWait();
if (resultado.isPresent()) {
    String nombre = resultado.get();
}
```

---

## 📊 FLUJO DE DATOS EN EL SISTEMA

### Ejemplo: Registrar un Partido

```
1. USUARIO hace clic en "Registrar Resultado"
   ↓
2. VISTA (VistaPartidos) abre diálogo
   ↓
3. USUARIO ingresa goles: Local=2, Visitante=1
   ↓
4. VISTA obtiene el partido seleccionado
   ↓
5. VISTA llama: partido.registrarResultado(2, 1)
   ↓
6. MODELO (Partido) ejecuta lógica:
   - Valida que goles >= 0
   - Actualiza golesLocal = 2
   - Actualiza golesVisitante = 1
   - Marca jugado = true
   - Llama a equipoLocal.setPuntos(puntos + 3)
   - Llama a equipoVisitante (no suma puntos)
   ↓
7. MODELO (Equipo) actualiza:
   - puntos += 3
   - ganados += 1
   - golesFavor += 2
   - golesContra += 1
   ↓
8. VISTA detecta cambio (ObservableList)
   ↓
9. VISTA actualiza tabla automáticamente
   ↓
10. USUARIO ve resultado en pantalla
```

**CLAVE:** La vista NUNCA modifica datos directamente, siempre llama al modelo.

---

## 🔍 VALIDACIONES EN EL SISTEMA

### Nivel 1: Validaciones de Interfaz (JavaFX)

**Ejemplo: Campo numérico**
```java
TextField textEdad = new TextField();
textEdad.textProperty().addListener((obs, oldVal, newVal) -> {
    if (!newVal.matches("\\d*")) {
        textEdad.setText(oldVal); // Rechaza si no es número
    }
});
```

### Nivel 2: Validaciones de Modelo (Lógica)

**Ejemplo: Jugador.java**
```java
public void setEdad(int edad) {
    if (edad < 10 || edad > 50) {
        throw new IllegalArgumentException("Edad debe estar entre 10 y 50");
    }
    this.edad = edad;
}
```

### Nivel 3: Validaciones de Negocio (Reglas)

**Ejemplo: Torneo.java**
```java
public void crearGrupos() {
    if (equipos.size() < 12) {
        throw new IllegalStateException("Se necesitan mínimo 12 equipos");
    }
    if (sorteoRealizado) {
        throw new IllegalStateException("El sorteo ya fue realizado");
    }
    // ... lógica del sorteo
}
```

---

## 🎬 EVENTOS EN JAVAFX

### ¿Qué es un Evento?

Un evento es una acción del usuario: clic en botón, escribir en campo, seleccionar en tabla, etc.

### Tipos de Eventos Usados

**1. ActionEvent (Botones)**
```java
button.setOnAction(e -> {
    // Código cuando se hace clic
});
```

**2. SelectionEvent (Tablas/ComboBox)**
```java
tabla.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
    if (newVal != null) {
        // Se seleccionó un elemento
    }
});
```

**3. TextChangeEvent (TextFields)**
```java
textField.textProperty().addListener((obs, oldVal, newVal) -> {
    // El texto cambió
});
```

---

## 🛠️ COMANDOS MAVEN IMPORTANTES

```bash
# Compilar el proyecto
mvn compile

# Limpiar y compilar
mvn clean compile

# Ejecutar la aplicación JavaFX
mvn javafx:run

# Limpiar, compilar y ejecutar (TODO EN UNO)
mvn clean javafx:run

# Ver dependencias del proyecto
mvn dependency:tree

# Empaquetar en JAR
mvn package
```

---

## 📁 ESTRUCTURA DEL PROYECTO EXPLICADA

```
examen-final/
├── pom.xml                          # Configuración Maven + dependencias
│
├── src/
│   ├── main/
│   │   ├── java/com/ejemplo/
│   │   │   ├── DatosIniciales.java       # Carga 12 equipos con jugadores
│   │   │   │
│   │   │   ├── model/                    # CAPA DE NEGOCIO
│   │   │   │   ├── Torneo.java           # Coordinador principal (429 líneas)
│   │   │   │   ├── Equipo.java           # Entidad equipo (170 líneas)
│   │   │   │   ├── Jugador.java          # Entidad jugador (231 líneas)
│   │   │   │   ├── Partido.java          # Entidad partido (324 líneas)
│   │   │   │   ├── Eliminatoria.java     # Fase knockout (273 líneas)
│   │   │   │   ├── Posicion.java         # Enum: PORTERO, DEFENSA, etc.
│   │   │   │   └── PosicionTabla.java    # DTO para mostrar tabla
│   │   │   │
│   │   │   ├── view/                     # CAPA DE PRESENTACIÓN
│   │   │   │   ├── Main.java             # Punto de entrada, crea Stage
│   │   │   │   ├── PanelPrincipal.java   # TabPane con 5 pestañas
│   │   │   │   ├── VistaBase.java        # Clase base (herencia)
│   │   │   │   ├── VistaTorneo.java      # Pestaña 1: Sorteo/Generar
│   │   │   │   ├── VistaEquipos.java     # Pestaña 2: Gestión equipos
│   │   │   │   ├── VistaPartidos.java    # Pestaña 3: Registrar resultados
│   │   │   │   ├── VistaTabla.java       # Pestaña 4: Posiciones/Goleadores
│   │   │   │   ├── VistaEliminatoria.java# Pestaña 5: Árbol knockout
│   │   │   │   └── DialogosFX.java       # Utilidad para diálogos
│   │   │   │
│   │   │   └── controller/               # CAPA DE CONTROL
│   │   │       └── PartidosController.java # Coordina registro resultados
│   │   │
│   │   └── resources/
│   │       └── com/ejemplo/view/
│   │           └── style.css             # Estilos CSS personalizados
│   │
│   └── test/                             # Pruebas unitarias (vacío)
│
└── target/                                # Archivos compilados (.class)
```

---

## 🧩 RELACIÓN ENTRE CLASES

```
Main.java
  └─ crea → Torneo ("Copa Universitaria")
  └─ carga → DatosIniciales.cargarEquiposYJugadores(torneo)
  └─ crea → PanelPrincipal(torneo)
      └─ crea 5 pestañas:
          ├─ VistaTorneo(torneo)
          ├─ VistaEquipos(torneo)
          ├─ VistaPartidos(torneo)
          ├─ VistaTabla(torneo)
          └─ VistaEliminatoria(torneo)

Torneo
  ├─ tiene → List<Equipo> equipos
  ├─ tiene → List<Partido> partidos
  ├─ tiene → Map<String, List<Equipo>> grupos
  └─ tiene → Eliminatoria eliminatoria

Equipo
  ├─ tiene → List<Jugador> jugadores
  └─ tiene → String nombreGrupo

Jugador
  ├─ pertenece a → Equipo equipo
  └─ tiene → Posicion posicion (enum)

Partido
  ├─ tiene → Equipo equipoLocal
  ├─ tiene → Equipo equipoVisitante
  └─ registra → Map<String, Integer> golesPorJugador

Eliminatoria
  ├─ tiene → List<Partido> partidosCuartos (4)
  ├─ tiene → List<Partido> partidosSemifinal (2)
  ├─ tiene → Partido partidoFinal (1)
  └─ tiene → Partido partidoTercerPuesto (1)
```

---

## 💻 CÓDIGO IMPORTANTE A CONOCER

### Main.java - Arranque de la Aplicación

```java
@Override
public void start(Stage stage) {
    // 1. Crear el modelo (torneo)
    Torneo torneo = new Torneo("Copa Universitaria");
    DatosIniciales.cargarEquiposYJugadores(torneo);

    // 2. Crear la vista principal
    PanelPrincipal panelPrincipal = new PanelPrincipal(torneo);

    // 3. Crear la escena con tamaño
    Scene scene = new Scene(panelPrincipal, 900, 600);
    scene.getStylesheets().add(getClass().getResource("/com/ejemplo/view/style.css").toExternalForm());

    // 4. Configurar y mostrar la ventana
    stage.setTitle("⚽ Gestor de Torneos - JavaFX");
    stage.setScene(scene);
    stage.show();
}
```

### PanelPrincipal.java - Las 5 Pestañas

```java
public PanelPrincipal(Torneo torneo) {
    TabPane tabPane = new TabPane();
    
    Tab tabTorneo = new Tab("Torneo", new VistaTorneo(torneo));
    Tab tabEquipos = new Tab("Equipos", new VistaEquipos(torneo));
    Tab tabPartidos = new Tab("Partidos", new VistaPartidos(torneo));
    Tab tabTabla = new Tab("Tabla", new VistaTabla(torneo));
    Tab tabEliminatoria = new Tab("Eliminatorias", new VistaEliminatoria(torneo));
    
    // Evitar que se cierren las pestañas
    tabTorneo.setClosable(false);
    tabEquipos.setClosable(false);
    // ... etc
    
    tabPane.getTabs().addAll(tabTorneo, tabEquipos, tabPartidos, tabTabla, tabEliminatoria);
    
    getChildren().add(tabPane);
}
```

### VistaTorneo.java - Botón con Acción

```java
Button btnSortear = new Button("Sortear Grupos");
btnSortear.setOnAction(e -> {
    try {
        torneo.crearGrupos();
        actualizarVista();
        DialogosFX.mostrarInformacion("Éxito", "Grupos creados correctamente");
    } catch (Exception ex) {
        DialogosFX.mostrarError("Error", ex.getMessage());
    }
});
```

---

## 🎯 PREGUNTAS TÉCNICAS ADICIONALES

### P: ¿Qué es ObservableList?
**R:** Es una lista especial de JavaFX que notifica automáticamente a la interfaz cuando cambia (se agrega, elimina o modifica un elemento). Por eso las tablas se actualizan solas.

```java
ObservableList<Equipo> listaEquipos = FXCollections.observableArrayList();
tablaEquipos.setItems(listaEquipos);
// Cuando agregas un equipo a listaEquipos, la tabla se actualiza sola
```

### P: ¿Qué es PropertyValueFactory?
**R:** Es una clase que conecta automáticamente una columna de tabla con un atributo de la clase, usando el nombre del getter.

```java
TableColumn<Equipo, String> colNombre = new TableColumn<>("Nombre");
colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
// Busca automáticamente el método getNombre() en la clase Equipo
```

### P: ¿Por qué usan lambda expressions (e ->)?
**R:** Es sintaxis moderna de Java para escribir código más corto. En lugar de crear una clase anónima completa, escribimos directamente el código.

```java
// Forma antigua (verbose)
button.setOnAction(new EventHandler<ActionEvent>() {
    @Override
    public void handle(ActionEvent e) {
        System.out.println("Clic");
    }
});

// Forma moderna (lambda)
button.setOnAction(e -> System.out.println("Clic"));
```

### P: ¿Qué es el binding en JavaFX?
**R:** Es vincular propiedades para que se actualicen automáticamente. Por ejemplo, deshabilitar un botón si un campo está vacío:

```java
btnGuardar.disableProperty().bind(textNombre.textProperty().isEmpty());
// El botón se habilita/deshabilita solo según el campo
```

### P: ¿Cómo funciona CSS en JavaFX?
**R:** Similar a CSS web. Defines clases en style.css y las aplicas con getStyleClass():

```css
/* style.css */
.boton-principal {
    -fx-background-color: #2196F3;
    -fx-text-fill: white;
    -fx-font-size: 14px;
}
```

```java
// Java
Button btn = new Button("Guardar");
btn.getStyleClass().add("boton-principal");
```

---

## 🚨 ERRORES COMUNES Y SOLUCIONES

### Error 1: NullPointerException
**Causa:** Intentar usar un objeto que es null.
**Solución:** Siempre validar antes de usar:
```java
Equipo equipo = tabla.getSelectionModel().getSelectedItem();
if (equipo == null) {
    DialogosFX.mostrarError("Error", "Seleccione un equipo");
    return;
}
equipo.setNombre("Nuevo nombre"); // Ahora es seguro
```

### Error 2: IllegalArgumentException
**Causa:** Pasar un valor inválido a un método.
**Solución:** Validar antes de llamar al método:
```java
try {
    int edad = Integer.parseInt(textEdad.getText());
    jugador.setEdad(edad); // Puede lanzar excepción si edad < 10
} catch (IllegalArgumentException e) {
    DialogosFX.mostrarError("Error", e.getMessage());
}
```

### Error 3: IndexOutOfBoundsException
**Causa:** Acceder a un índice que no existe en una lista.
**Solución:** Verificar tamaño antes:
```java
if (lista.size() > 0) {
    Equipo primero = lista.get(0);
}
```

### Error 4: Scene Graph Modification Exception
**Causa:** Modificar la interfaz desde otro thread.
**Solución:** Usar Platform.runLater:
```java
Platform.runLater(() -> {
    tabla.getItems().add(nuevoEquipo);
});
```

---

## 📚 GLOSARIO DE TÉRMINOS

**Stage:** La ventana principal de la aplicación.

**Scene:** El contenido dentro de una ventana (Stage).

**Node:** Cualquier elemento visual (botón, label, imagen, etc.).

**Parent:** Un nodo que puede contener otros nodos (VBox, HBox, etc.).

**Leaf:** Un nodo que no puede contener otros (Button, Label, TextField).

**Layout:** Contenedor que organiza automáticamente sus hijos.

**Property:** Valor observable que notifica cuando cambia.

**Binding:** Vinculación automática entre propiedades.

**FXML:** Formato XML para definir interfaces (no lo usamos).

**Controller:** Clase que maneja la lógica de una vista.

**DTO:** Data Transfer Object, objeto simple para transferir datos.

**Observable:** Objeto que notifica cuando cambia.

**Listener:** Función que se ejecuta cuando ocurre un evento.

---

## 🚀 MENSAJE FINAL

**¡Tú puedes hacerlo!** Ya tienes todo el conocimiento. Solo necesitas:
1. Leer esta guía 2-3 veces
2. Practicar la exposición en voz alta
3. Ejecutar la aplicación varias veces
4. Hablar con confianza

**Recuerda:** Conoces el proyecto mejor que nadie. Si te hacen una pregunta que no sabes, está bien decir *"Esa es una buena pregunta, tendría que revisarlo con más detalle"*.

**RESPIRA, SONRÍE Y DEMUESTRA LO QUE SABES** 💪

**¡MUCHA SUERTE! 🍀**
