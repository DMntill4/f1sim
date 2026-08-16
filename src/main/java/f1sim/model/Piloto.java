package f1sim.model;

// Representa un piloto de F1
public class Piloto {
    public int id;
    public String nombre;
    public String equipo;
    public String rol; // "Lider" o "Escudero"
    public int experiencia; // años de experiencia
    public int nivelHabilidad = 85; // Puntaje de habilidad de 0 a 100
    public int victorias = 0;
    public int podios = 0;
    public int puntos = 0;
    public String vehiculoAsignado = ""; // Modelo de vehículo asignado al piloto


    public Piloto() {
    }

    public Piloto(int id, String nombre, String equipo, String rol, int experiencia) {
        this(id, nombre, equipo, rol, experiencia, 85, 0, 0, 0);
    }

    public Piloto(int id, String nombre, String equipo, String rol, int experiencia, int nivelHabilidad, int victorias, int podios, int puntos) {
        this.id = id;
        this.nombre = nombre != null ? nombre.trim() : "Piloto Desconocido";
        this.equipo = equipo != null ? equipo.trim() : "Sin Equipo";
        this.rol = (rol != null && (rol.equalsIgnoreCase("Lider") || rol.equalsIgnoreCase("Escudero"))) ? rol : "Lider";
        this.experiencia = Math.max(0, experiencia);
        this.nivelHabilidad = Math.max(1, Math.min(100, nivelHabilidad));
        this.victorias = Math.max(0, victorias);
        this.podios = Math.max(0, podios);
        this.puntos = Math.max(0, puntos);
    }

    public boolean esValido() {
        return id > 0 && nombre != null && !nombre.isEmpty() && nivelHabilidad >= 1 && nivelHabilidad <= 100;
    }

    @Override
    public String toString() {
        return nombre + " (" + equipo + ")";
    }
}

