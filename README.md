# Simulación de Fórmula 1 (Java Swing + Gson)

Proyecto de escritorio en Java que administra pilotos, equipos, vehículos y circuitos
de F1, con una simulación de clasificación. Los datos se guardan en archivos JSON
usando la librería **Gson**, y la interfaz gráfica está hecha con **Swing**.

## Requisitos

- **JDK 17 o superior** instalado (`java -version` para comprobarlo).
- **Maven** (recomendado, para descargar Gson automáticamente) — o, si prefieres
  no usar Maven, puedes descargar manualmente el jar de Gson (ver más abajo).

## Estructura del proyecto

```
f1sim/
├── pom.xml                        <- configuración de Maven (dependencia de Gson)
├── data/                          <- archivos JSON con los datos (persistencia)
│   ├── pilotos.json
│   ├── equipos.json
│   ├── circuitos.json
│   └── vehiculos.json
└── src/main/java/f1sim/
    ├── Main.java                  <- punto de entrada del programa
    ├── model/                     <- clases de datos (Piloto, Equipo, Vehiculo, Circuito, etc.)
    ├── datos/GestorDatos.java     <- lectura/escritura de JSON con Gson
    └── ui/                        <- pantallas Swing (una por pestaña)
        ├── VentanaPrincipal.java
        ├── PanelPilotos.java
        ├── PanelEquipos.java
        ├── PanelVehiculos.java    <- aquí está la vista visual de los autos
        ├── PanelCircuitos.java
        └── PanelSimulacion.java
```

## Cómo ejecutarlo

### Opción A: con Maven (más fácil)

Abre una terminal dentro de la carpeta `f1sim` y ejecuta:

```bash
mvn compile exec:java
```

Esto descarga Gson automáticamente y abre la ventana de la aplicación.

También puedes generar un `.jar` ejecutable con todo incluido:

```bash
mvn package
java -jar target/f1sim.jar
```

### Opción B: con un IDE (IntelliJ IDEA, Eclipse, VS Code)

1. Abre la carpeta `f1sim` como un **proyecto Maven** (la mayoría de los IDE lo
   detectan automáticamente al ver el `pom.xml`).
2. Deja que el IDE descargue la dependencia de Gson.
3. Ejecuta la clase `f1sim.Main`.

### Opción C: sin Maven (compilación manual)

1. Descarga el jar de Gson (por ejemplo `gson-2.11.0.jar`) desde Maven Central y
   colócalo en una carpeta `lib/` dentro del proyecto.
2. Compila:
   ```bash
   javac -cp lib/gson-2.11.0.jar -d out $(find src/main/java -name "*.java")
   ```
3. Ejecuta:
   ```bash
   java -cp "out:lib/gson-2.11.0.jar" f1sim.Main
   ```
   (en Windows usa `;` en vez de `:` para separar las rutas del classpath)

## Qué hace cada pestaña

- **Pilotos**: agregar, editar, eliminar y buscar pilotos.
- **Equipos**: agregar, editar, eliminar y buscar equipos, y asignarles pilotos.
- **Vehículos**: agregar, editar, eliminar y buscar autos. Al seleccionar un auto
  en la tabla, se muestra **su imagen y sus especificaciones** (velocidad,
  aceleración, consumo de combustible y desgaste de neumáticos por modo de
  conducción y clima) — esta es la interfaz visual de los autos de F1.
- **Circuitos**: agregar, editar, eliminar y buscar circuitos, con su historial
  de ganadores y récord de vuelta.
- **Simulación de Clasificación**: elige un circuito, un modo de conducción
  (normal / agresiva / ahorro) y un clima (o "aleatorio"), y genera la tabla de
  posiciones calculando el tiempo de vuelta de cada piloto según el vehículo de
  su equipo. Los resultados se pueden guardar para consultarlos después.

## Persistencia de datos

Todos los datos (pilotos, equipos, circuitos y vehículos) se guardan
automáticamente en la carpeta `data/` en formato JSON cuando cierras la
ventana. Los resultados de las simulaciones se guardan en
`data/resultados.json` al presionar "Guardar resultados".

## Nota sobre las imágenes de los autos

Las imágenes se cargan desde URLs (por ejemplo de Wikimedia Commons) cuando
seleccionas un vehículo. Se necesita conexión a internet para verlas; si una
URL falla o no hay conexión, se muestra un mensaje de "imagen no disponible"
en vez de bloquear el programa.
