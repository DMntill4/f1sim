# Sistema de Simulación y Telemetría de Fórmula 1

![Java](https://img.shields.io/badge/Java_17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Java Swing](https://img.shields.io/badge/Java_Swing-007396?style=for-the-badge&logo=java&logoColor=white)
![Google Gson](https://img.shields.io/badge/Google_Gson-4285F4?style=for-the-badge&logo=google&logoColor=white)
![JSON](https://img.shields.io/badge/JSON-000000?style=for-the-badge&logo=json&logoColor=white)
![Apache Maven](https://img.shields.io/badge/Apache_Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![Git](https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white)

Sistema de escritorio desarrollado en Java para la gestión integral, simulación física multihilo y análisis de telemetría de la Fórmula 1. La aplicación implementa una arquitectura en capas con persistencia en archivos JSON y control de acceso basado en roles.

---

## Desarrolladores

- Diego Mantilla
- Andres Guerra

---

## Funcionalidades Principales

- Control de Acceso Basado en Roles (RBAC): Separación estricta de permisos entre Administrador (CRUD completo de entidades) y Usuario (Simulación de sesiones y consulta).
- Gestión Completa de Entidades (CRUD): Registro, edición, búsqueda y eliminación de Pilotos, Equipos, Vehículos y Circuitos.
- Asignación Piloto-Vehículo: Selector por escudería para definir qué vehículo conduce cada piloto.
- Simulación de Clasificación: Cálculo numérico de tiempos de vuelta considerando carga aerodinámica, presión de neumáticos, degradación, estrategia de combustible y clima.
- Carrera en Vivo en Tiempo Real: Renderizado 2D de la pista con animación independiente por hilo de piloto, paradas en boxes (Pit Stops), zonas DRS y banderas de carrera (Safety Car, VSC).
- Perfil de Usuario Personal: Visualización de estadísticas individuales, mejores tiempos personales, historial filtrado y exportación de datos a formato CSV.
- Telemetría en Vivo: Gráficos interactivos para monitoreo continuo de deltas de tiempo por vuelta.

---

## Estructura del Proyecto

```
f1sim/
├── data/                            Archivos JSON de persistencia de datos
│   ├── pilotos.json
│   ├── equipos.json
│   ├── circuitos.json
│   ├── vehiculos.json
│   ├── usuarios.json
│   └── resultados.json
├── lib/                             Librerías auxiliares (Gson)
├── pom.xml                          Configuración de dependencias de Maven
└── src/
    ├── main/java/f1sim/
    │   ├── Main.java                Punto de entrada de la aplicación
    │   ├── datos/                   Persistencia de datos y exportador CSV/JSON
    │   ├── model/                   Modelos de dominio POO (Piloto, Vehiculo, Circuito, etc.)
    │   ├── race/                    Motor de simulación multihilo (HiloPiloto, GestorPitStop, etc.)
    │   └── ui/                      Paneles e interfaz gráfica Swing
    └── test/java/f1sim/             Pruebas unitarias del sistema
```

---

## Instrucciones de Compilación y Ejecución

### Opción 1: Ejecución mediante Maven

Para compilar y ejecutar directamente con Maven:

```bash
mvn compile exec:java
```

Para generar un empaquetado ejecutable JAR:

```bash
mvn package
java -jar target/f1sim.jar
```

### Opción 2: Compilación Manual mediante Java CLI

Compilación directa del código fuente:

```bash
javac --release 17 -cp "lib/*" -d target/classes src/main/java/f1sim/*.java src/main/java/f1sim/datos/*.java src/main/java/f1sim/model/*.java src/main/java/f1sim/race/*.java src/main/java/f1sim/ui/*.java
```

Ejecución de la aplicación:

```bash
java -cp "target/classes;lib/*" f1sim.Main
```

---

## Credenciales de Acceso Predeterminadas

- Administrador: usuario `admin`, contraseña `admin123`
- Piloto Usuario (Max Verstappen): usuario `verstappen`, contraseña `123`
- Piloto Usuario (Charles Leclerc): usuario `leclerc`, contraseña `123`
- Usuario Estándar: usuario `usuario`, contraseña `user123`
