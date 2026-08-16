package f1sim.datos;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import f1sim.model.Circuito;
import f1sim.model.DatosCondicion;
import f1sim.model.Equipo;
import f1sim.model.Ganador;
import f1sim.model.ModoConduccion;
import f1sim.model.Piloto;
import f1sim.model.RecordVuelta;
import f1sim.model.ResultadoClasificacion;
import f1sim.model.Usuario;
import f1sim.model.Vehiculo;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestorDatos {

    private static final String CARPETA = "data";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static void asegurarCarpeta() {
        try {
            Files.createDirectories(Paths.get(CARPETA));
        } catch (IOException e) {
            System.out.println("No se pudo crear la carpeta de datos: " + e.getMessage());
        }
    }

    private static <T> void guardarLista(String nombreArchivo, List<T> lista) {
        asegurarCarpeta();
        respaldarArchivo(nombreArchivo);
        try (FileWriter escritor = new FileWriter(CARPETA + "/" + nombreArchivo)) {
            gson.toJson(lista, escritor);
        } catch (IOException e) {
            System.out.println("Error al guardar " + nombreArchivo + ": " + e.getMessage());
        }
    }

    private static void respaldarArchivo(String nombreArchivo) {
        java.io.File origen = new java.io.File(CARPETA + "/" + nombreArchivo);
        if (origen.exists()) {
            try {
                Files.copy(origen.toPath(), Paths.get(CARPETA + "/" + nombreArchivo + ".bak"),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ignored) {
                // Silencioso si falla el respaldo
            }
        }
    }

    private static <T> List<T> cargarLista(String nombreArchivo, Type tipo) {
        asegurarCarpeta();
        java.io.File archivo = new java.io.File(CARPETA + "/" + nombreArchivo);
        if (!archivo.exists()) {
            return new ArrayList<>();
        }
        try (FileReader lector = new FileReader(archivo)) {
            List<T> lista = gson.fromJson(lector, tipo);
            return lista != null ? lista : new ArrayList<>();
        } catch (IOException e) {
            System.out.println("Error al leer " + nombreArchivo + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }


    // ---------- PILOTOS ----------
    public static void guardarPilotos(List<Piloto> pilotos) {
        guardarLista("pilotos.json", pilotos);
    }

    public static List<Piloto> cargarPilotos() {
        Type tipo = new TypeToken<List<Piloto>>() {}.getType();
        return cargarLista("pilotos.json", tipo);
    }

    public static Map<Integer, Piloto> cargarPilotosMap() {
        Map<Integer, Piloto> mapa = new HashMap<>();
        for (Piloto p : cargarPilotos()) {
            mapa.put(p.id, p);
        }
        return mapa;
    }

    public static void guardarPilotosMap(Map<Integer, Piloto> mapa) {
        guardarPilotos(new ArrayList<>(mapa.values()));
    }

    // ---------- EQUIPOS ----------
    public static void guardarEquipos(List<Equipo> equipos) {
        guardarLista("equipos.json", equipos);
    }

    public static List<Equipo> cargarEquipos() {
        Type tipo = new TypeToken<List<Equipo>>() {}.getType();
        return cargarLista("equipos.json", tipo);
    }

    public static Map<String, Equipo> cargarEquiposMap() {
        Map<String, Equipo> mapa = new HashMap<>();
        for (Equipo eq : cargarEquipos()) {
            mapa.put(eq.nombre, eq);
        }
        return mapa;
    }

    public static void guardarEquiposMap(Map<String, Equipo> mapa) {
        guardarEquipos(new ArrayList<>(mapa.values()));
    }

    // ---------- CIRCUITOS ----------
    public static void guardarCircuitos(List<Circuito> circuitos) {
        guardarLista("circuitos.json", circuitos);
    }

    public static List<Circuito> cargarCircuitos() {
        Type tipo = new TypeToken<List<Circuito>>() {}.getType();
        return cargarLista("circuitos.json", tipo);
    }

    public static Map<String, Circuito> cargarCircuitosMap() {
        Map<String, Circuito> mapa = new HashMap<>();
        for (Circuito c : cargarCircuitos()) {
            mapa.put(c.nombre, c);
        }
        return mapa;
    }

    public static void guardarCircuitosMap(Map<String, Circuito> mapa) {
        guardarCircuitos(new ArrayList<>(mapa.values()));
    }

    // ---------- VEHICULOS ----------
    public static void guardarVehiculos(List<Vehiculo> vehiculos) {
        guardarLista("vehiculos.json", vehiculos);
    }

    public static List<Vehiculo> cargarVehiculos() {
        Type tipo = new TypeToken<List<Vehiculo>>() {}.getType();
        return cargarLista("vehiculos.json", tipo);
    }

    public static Map<String, Vehiculo> cargarVehiculosMap() {
        Map<String, Vehiculo> mapa = new HashMap<>();
        for (Vehiculo v : cargarVehiculos()) {
            mapa.put(v.equipo + " - " + v.modelo, v);
        }
        return mapa;
    }

    public static void guardarVehiculosMap(Map<String, Vehiculo> mapa) {
        guardarVehiculos(new ArrayList<>(mapa.values()));
    }

    // ---------- RESULTADOS DE CLASIFICACION ----------
    public static void guardarResultados(List<ResultadoClasificacion> resultados) {
        guardarLista("resultados.json", resultados);
    }

    public static List<ResultadoClasificacion> cargarResultados() {
        Type tipo = new TypeToken<List<ResultadoClasificacion>>() {}.getType();
        return cargarLista("resultados.json", tipo);
    }

    // ---------- USUARIOS & ROLES ----------
    public static void guardarUsuarios(List<Usuario> usuarios) {
        guardarLista("usuarios.json", usuarios);
    }

    public static List<Usuario> cargarUsuarios() {
        Type tipo = new TypeToken<List<Usuario>>() {}.getType();
        List<Usuario> lista = cargarLista("usuarios.json", tipo);
        if (lista.isEmpty()) {
            lista.add(new Usuario(1, "admin", "admin123", Usuario.Rol.ADMIN, "Administrador F1"));
            lista.add(new Usuario(2, "usuario", "user123", Usuario.Rol.USUARIO, "Usuario Estandar"));
            guardarUsuarios(lista);
        }
        return lista;
    }

    // ---------- DATOS INICIALES DE LA ESPECIFICACION ----------
    public static void cargarDatosIniciales() {
        // Solo cargar si no existen archivos de datos
        if (cargarPilotos().isEmpty()) {
            guardarPilotos(crearPilotosIniciales());
        }
        if (cargarEquipos().isEmpty()) {
            guardarEquipos(crearEquiposIniciales());
        }
        if (cargarCircuitos().isEmpty()) {
            guardarCircuitos(crearCircuitosIniciales());
        }
        if (cargarVehiculos().isEmpty()) {
            guardarVehiculos(crearVehiculosIniciales());
        }
    }

    private static List<Piloto> crearPilotosIniciales() {
        List<Piloto> pilotos = new ArrayList<>();
        pilotos.add(new Piloto(1, "Max Verstappen", "Red Bull Racing", "Lider", 9, 97, 54, 98, 2586));
        pilotos.add(new Piloto(2, "Sergio Perez", "Red Bull Racing", "Escudero", 13, 82, 6, 35, 1357));
        pilotos.add(new Piloto(3, "Lewis Hamilton", "Mercedes-AMG Petronas", "Lider", 17, 96, 103, 197, 4639));
        pilotos.add(new Piloto(4, "George Russell", "Mercedes-AMG Petronas", "Escudero", 5, 88, 2, 12, 508));
        pilotos.add(new Piloto(5, "Charles Leclerc", "Ferrari", "Lider", 6, 91, 5, 28, 935));
        pilotos.add(new Piloto(6, "Carlos Sainz", "Ferrari", "Escudero", 9, 87, 3, 18, 860));
        pilotos.add(new Piloto(7, "Lando Norris", "McLaren", "Lider", 5, 89, 1, 14, 590));
        pilotos.add(new Piloto(8, "Oscar Piastri", "McLaren", "Escudero", 2, 85, 0, 4, 195));
        pilotos.add(new Piloto(9, "Fernando Alonso", "Aston Martin", "Lider", 22, 90, 32, 106, 2198));
        pilotos.add(new Piloto(10, "Lance Stroll", "Aston Martin", "Escudero", 7, 72, 0, 3, 238));
        pilotos.add(new Piloto(11, "Esteban Ocon", "Alpine", "Lider", 7, 78, 1, 3, 395));
        pilotos.add(new Piloto(12, "Pierre Gasly", "Alpine", "Escudero", 7, 80, 1, 4, 348));
        pilotos.add(new Piloto(13, "Valtteri Bottas", "Alfa Romeo", "Lider", 12, 79, 10, 67, 1792));
        pilotos.add(new Piloto(14, "Zhou Guanyu", "Alfa Romeo", "Escudero", 2, 68, 0, 0, 12));
        pilotos.add(new Piloto(15, "Kevin Magnussen", "Haas", "Lider", 10, 74, 0, 1, 185));
        pilotos.add(new Piloto(16, "Nico Hulkenberg", "Haas", "Escudero", 11, 76, 0, 0, 530));
        pilotos.add(new Piloto(17, "Yuki Tsunoda", "AlphaTauri", "Lider", 3, 77, 0, 1, 46));
        pilotos.add(new Piloto(18, "Daniel Ricciardo", "AlphaTauri", "Escudero", 13, 81, 8, 32, 1311));
        pilotos.add(new Piloto(19, "Alexander Albon", "Williams", "Lider", 5, 79, 0, 2, 220));
        pilotos.add(new Piloto(20, "Logan Sargeant", "Williams", "Escudero", 1, 62, 0, 0, 1));
        return pilotos;
    }

    private static List<Equipo> crearEquiposIniciales() {
        List<Equipo> equipos = new ArrayList<>();
        equipos.add(new Equipo("Red Bull Racing", "Austria", "Honda", Arrays.asList(1, 2), ""));
        equipos.add(new Equipo("Mercedes-AMG Petronas", "Alemania", "Mercedes", Arrays.asList(3, 4), ""));
        equipos.add(new Equipo("Ferrari", "Italia", "Ferrari", Arrays.asList(5, 6), ""));
        equipos.add(new Equipo("McLaren", "Reino Unido", "Mercedes", Arrays.asList(7, 8), ""));
        equipos.add(new Equipo("Aston Martin", "Reino Unido", "Mercedes", Arrays.asList(9, 10), ""));
        equipos.add(new Equipo("Alpine", "Francia", "Renault", Arrays.asList(11, 12), ""));
        equipos.add(new Equipo("Alfa Romeo", "Suiza", "Ferrari", Arrays.asList(13, 14), ""));
        equipos.add(new Equipo("Haas", "Estados Unidos", "Ferrari", Arrays.asList(15, 16), ""));
        equipos.add(new Equipo("AlphaTauri", "Italia", "Honda", Arrays.asList(17, 18), ""));
        equipos.add(new Equipo("Williams", "Reino Unido", "Mercedes", Arrays.asList(19, 20), ""));
        return equipos;
    }

    private static List<Circuito> crearCircuitosIniciales() {
        List<Circuito> circuitos = new ArrayList<>();

        circuitos.add(new Circuito("Circuito de Monaco", "Monaco", 3.34, 78,
                "Uno de los circuitos mas prestigiosos y dificiles del calendario, conocido por sus calles angostas y la falta de zonas de adelantamiento.",
                "seco", new RecordVuelta("1:10.166", "Lewis Hamilton", 2019),
                Arrays.asList(new Ganador(2021, 1), new Ganador(2022, 2), new Ganador(2023, 1)), "", 1.1));

        circuitos.add(new Circuito("Silverstone", "Reino Unido", 5.89, 52,
                "Uno de los circuitos mas rapidos del calendario, con curvas de alta velocidad como Maggotts y Becketts.",
                "lluvioso", new RecordVuelta("1:27.097", "Max Verstappen", 2020),
                Arrays.asList(new Ganador(2021, 3), new Ganador(2022, 5), new Ganador(2023, 1)), "", 0.9));

        circuitos.add(new Circuito("Circuito de Spa-Francorchamps", "Belgica", 7.00, 44,
                "Famoso por la curva Eau Rouge y la larga recta de Kemmel, un circuito donde la potencia del motor es clave.",
                "lluvioso", new RecordVuelta("1:46.286", "Valtteri Bottas", 2018),
                Arrays.asList(new Ganador(2021, 1), new Ganador(2022, 1), new Ganador(2023, 1)), "", 1.0));

        circuitos.add(new Circuito("Circuito de Monza", "Italia", 5.79, 53,
                "Conocido como 'El Templo de la Velocidad', Monza es el circuito mas rapido del calendario con largas rectas y chicanes iconicas.",
                "seco", new RecordVuelta("1:21.046", "Rubens Barrichello", 2004),
                Arrays.asList(new Ganador(2021, 2), new Ganador(2022, 1), new Ganador(2023, 1)), "", 0.8));

        circuitos.add(new Circuito("Interlagos", "Brasil", 4.31, 71,
                "Interlagos es un circuito legendario con cambios de elevacion y un trazado tecnico que ha sido sede de algunas de las carreras mas emocionantes de la historia.",
                "lluvioso", new RecordVuelta("1:10.540", "Valtteri Bottas", 2018),
                Arrays.asList(new Ganador(2021, 3), new Ganador(2022, 1), new Ganador(2023, 1)), "", 1.2));

        circuitos.add(new Circuito("Circuito de Yas Marina", "Emiratos Arabes Unidos", 5.28, 58,
                "Ubicado en Abu Dhabi, es famoso por ser el circuito donde se definen muchos campeonatos, con un diseno moderno y una espectacular carrera nocturna.",
                "seco", new RecordVuelta("1:39.283", "Lewis Hamilton", 2019),
                Arrays.asList(new Ganador(2021, 1), new Ganador(2022, 1), new Ganador(2023, 3)), "", 1.0));

        circuitos.add(new Circuito("Circuito de Suzuka", "Japon", 5.81, 53,
                "Un circuito desafiante con un diseno en forma de ocho, famoso por sus curvas de alta velocidad como 130R y la 'S' de Senna.",
                "lluvioso", new RecordVuelta("1:30.983", "Lewis Hamilton", 2019),
                Arrays.asList(new Ganador(2021, 1), new Ganador(2022, 1), new Ganador(2023, 1)), "", 1.1));

        return circuitos;
    }

    private static List<Vehiculo> crearVehiculosIniciales() {
        List<Vehiculo> vehiculos = new ArrayList<>();

        // Red Bull Racing RB20
        Vehiculo rb20 = new Vehiculo();
        rb20.equipo = "Red Bull Racing";
        rb20.modelo = "RB20";
        rb20.motor = "Honda";
        rb20.velocidadMaximaKmh = 360;
        rb20.aceleracion = 2.5;
        rb20.pilotos = Arrays.asList(1, 2);
        rb20.imagen = "";
        rb20.cargaAerodinamica = "media";
        rb20.presionNeumaticos = "estandar";
        rb20.normal = new ModoConduccion(320,
                new DatosCondicion(1.9, 2.1, 2.4),
                new DatosCondicion(1.5, 0.8, 2.5));
        rb20.agresiva = new ModoConduccion(340,
                new DatosCondicion(2.4, 2.6, 3.0),
                new DatosCondicion(2.2, 1.2, 3.5));
        rb20.ahorro = new ModoConduccion(300,
                new DatosCondicion(1.6, 1.8, 2.1),
                new DatosCondicion(1.0, 0.5, 1.8));
        vehiculos.add(rb20);

        // Mercedes W15
        Vehiculo w15 = new Vehiculo();
        w15.equipo = "Mercedes-AMG Petronas";
        w15.modelo = "W15";
        w15.motor = "Mercedes";
        w15.velocidadMaximaKmh = 355;
        w15.aceleracion = 2.6;
        w15.pilotos = Arrays.asList(3, 4);
        w15.imagen = "";
        w15.cargaAerodinamica = "media";
        w15.presionNeumaticos = "estandar";
        w15.normal = new ModoConduccion(315,
                new DatosCondicion(2.0, 2.2, 2.5),
                new DatosCondicion(1.6, 0.9, 2.6));
        w15.agresiva = new ModoConduccion(335,
                new DatosCondicion(2.6, 2.8, 3.2),
                new DatosCondicion(2.3, 1.4, 3.8));
        w15.ahorro = new ModoConduccion(295,
                new DatosCondicion(1.7, 1.9, 2.2),
                new DatosCondicion(1.1, 0.6, 1.9));
        vehiculos.add(w15);

        // Ferrari SF-24
        Vehiculo sf24 = new Vehiculo();
        sf24.equipo = "Ferrari";
        sf24.modelo = "SF-24";
        sf24.motor = "Ferrari";
        sf24.velocidadMaximaKmh = 358;
        sf24.aceleracion = 2.5;
        sf24.pilotos = Arrays.asList(5, 6);
        sf24.imagen = "";
        sf24.cargaAerodinamica = "alta";
        sf24.presionNeumaticos = "estandar";
        sf24.normal = new ModoConduccion(318,
                new DatosCondicion(2.0, 2.2, 2.5),
                new DatosCondicion(1.5, 0.9, 2.5));
        sf24.agresiva = new ModoConduccion(338,
                new DatosCondicion(2.5, 2.7, 3.1),
                new DatosCondicion(2.1, 1.3, 3.6));
        sf24.ahorro = new ModoConduccion(298,
                new DatosCondicion(1.7, 1.9, 2.2),
                new DatosCondicion(1.0, 0.6, 1.9));
        vehiculos.add(sf24);

        // McLaren MCL38
        Vehiculo mcl38 = new Vehiculo();
        mcl38.equipo = "McLaren";
        mcl38.modelo = "MCL38";
        mcl38.motor = "Mercedes";
        mcl38.velocidadMaximaKmh = 356;
        mcl38.aceleracion = 2.6;
        mcl38.pilotos = Arrays.asList(7, 8);
        mcl38.imagen = "";
        mcl38.cargaAerodinamica = "media";
        mcl38.presionNeumaticos = "estandar";
        mcl38.normal = new ModoConduccion(316,
                new DatosCondicion(1.9, 2.1, 2.4),
                new DatosCondicion(1.5, 0.8, 2.5));
        mcl38.agresiva = new ModoConduccion(336,
                new DatosCondicion(2.5, 2.7, 3.1),
                new DatosCondicion(2.2, 1.2, 3.5));
        mcl38.ahorro = new ModoConduccion(296,
                new DatosCondicion(1.6, 1.8, 2.1),
                new DatosCondicion(1.0, 0.5, 1.8));
        vehiculos.add(mcl38);

        // Aston Martin AMR24
        Vehiculo amr24 = new Vehiculo();
        amr24.equipo = "Aston Martin";
        amr24.modelo = "AMR24";
        amr24.motor = "Mercedes";
        amr24.velocidadMaximaKmh = 352;
        amr24.aceleracion = 2.7;
        amr24.pilotos = Arrays.asList(9, 10);
        amr24.imagen = "";
        amr24.cargaAerodinamica = "media";
        amr24.presionNeumaticos = "estandar";
        amr24.normal = new ModoConduccion(310,
                new DatosCondicion(2.0, 2.2, 2.5),
                new DatosCondicion(1.6, 0.9, 2.6));
        amr24.agresiva = new ModoConduccion(330,
                new DatosCondicion(2.6, 2.8, 3.2),
                new DatosCondicion(2.3, 1.3, 3.7));
        amr24.ahorro = new ModoConduccion(290,
                new DatosCondicion(1.7, 1.9, 2.2),
                new DatosCondicion(1.1, 0.6, 2.0));
        vehiculos.add(amr24);

        // Alpine A524
        Vehiculo a524 = new Vehiculo();
        a524.equipo = "Alpine";
        a524.modelo = "A524";
        a524.motor = "Renault";
        a524.velocidadMaximaKmh = 348;
        a524.aceleracion = 2.8;
        a524.pilotos = Arrays.asList(11, 12);
        a524.imagen = "";
        a524.cargaAerodinamica = "media";
        a524.presionNeumaticos = "estandar";
        a524.normal = new ModoConduccion(305,
                new DatosCondicion(2.1, 2.3, 2.6),
                new DatosCondicion(1.7, 1.0, 2.7));
        a524.agresiva = new ModoConduccion(325,
                new DatosCondicion(2.7, 2.9, 3.3),
                new DatosCondicion(2.4, 1.4, 3.8));
        a524.ahorro = new ModoConduccion(285,
                new DatosCondicion(1.8, 2.0, 2.3),
                new DatosCondicion(1.2, 0.7, 2.1));
        vehiculos.add(a524);

        // Alfa Romeo C44
        Vehiculo c44 = new Vehiculo();
        c44.equipo = "Alfa Romeo";
        c44.modelo = "C44";
        c44.motor = "Ferrari";
        c44.velocidadMaximaKmh = 345;
        c44.aceleracion = 2.8;
        c44.pilotos = Arrays.asList(13, 14);
        c44.imagen = "";
        c44.cargaAerodinamica = "media";
        c44.presionNeumaticos = "estandar";
        c44.normal = new ModoConduccion(303,
                new DatosCondicion(2.1, 2.3, 2.6),
                new DatosCondicion(1.7, 1.0, 2.7));
        c44.agresiva = new ModoConduccion(323,
                new DatosCondicion(2.7, 2.9, 3.3),
                new DatosCondicion(2.4, 1.4, 3.8));
        c44.ahorro = new ModoConduccion(283,
                new DatosCondicion(1.8, 2.0, 2.3),
                new DatosCondicion(1.2, 0.7, 2.1));
        vehiculos.add(c44);

        // Haas VF-24
        Vehiculo vf24 = new Vehiculo();
        vf24.equipo = "Haas";
        vf24.modelo = "VF-24";
        vf24.motor = "Ferrari";
        vf24.velocidadMaximaKmh = 343;
        vf24.aceleracion = 2.9;
        vf24.pilotos = Arrays.asList(15, 16);
        vf24.imagen = "";
        vf24.cargaAerodinamica = "media";
        vf24.presionNeumaticos = "estandar";
        vf24.normal = new ModoConduccion(300,
                new DatosCondicion(2.2, 2.4, 2.7),
                new DatosCondicion(1.8, 1.1, 2.8));
        vf24.agresiva = new ModoConduccion(320,
                new DatosCondicion(2.8, 3.0, 3.4),
                new DatosCondicion(2.5, 1.5, 3.9));
        vf24.ahorro = new ModoConduccion(280,
                new DatosCondicion(1.9, 2.1, 2.4),
                new DatosCondicion(1.3, 0.8, 2.2));
        vehiculos.add(vf24);

        // AlphaTauri AT05
        Vehiculo at05 = new Vehiculo();
        at05.equipo = "AlphaTauri";
        at05.modelo = "AT05";
        at05.motor = "Honda";
        at05.velocidadMaximaKmh = 347;
        at05.aceleracion = 2.8;
        at05.pilotos = Arrays.asList(17, 18);
        at05.imagen = "";
        at05.cargaAerodinamica = "media";
        at05.presionNeumaticos = "estandar";
        at05.normal = new ModoConduccion(306,
                new DatosCondicion(2.0, 2.2, 2.5),
                new DatosCondicion(1.6, 0.9, 2.6));
        at05.agresiva = new ModoConduccion(326,
                new DatosCondicion(2.6, 2.8, 3.2),
                new DatosCondicion(2.3, 1.3, 3.7));
        at05.ahorro = new ModoConduccion(286,
                new DatosCondicion(1.7, 1.9, 2.2),
                new DatosCondicion(1.1, 0.6, 2.0));
        vehiculos.add(at05);

        // Williams FW46
        Vehiculo fw46 = new Vehiculo();
        fw46.equipo = "Williams";
        fw46.modelo = "FW46";
        fw46.motor = "Mercedes";
        fw46.velocidadMaximaKmh = 344;
        fw46.aceleracion = 2.9;
        fw46.pilotos = Arrays.asList(19, 20);
        fw46.imagen = "";
        fw46.cargaAerodinamica = "media";
        fw46.presionNeumaticos = "estandar";
        fw46.normal = new ModoConduccion(302,
                new DatosCondicion(2.1, 2.3, 2.6),
                new DatosCondicion(1.7, 1.0, 2.7));
        fw46.agresiva = new ModoConduccion(322,
                new DatosCondicion(2.7, 2.9, 3.3),
                new DatosCondicion(2.4, 1.4, 3.8));
        fw46.ahorro = new ModoConduccion(282,
                new DatosCondicion(1.8, 2.0, 2.3),
                new DatosCondicion(1.2, 0.7, 2.1));
        vehiculos.add(fw46);

        return vehiculos;
    }
}
