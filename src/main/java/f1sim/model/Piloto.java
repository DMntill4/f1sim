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

    public Piloto() {
    }

    public Piloto(int id, String nombre, String equipo, String rol, int experiencia) {
        this(id, nombre, equipo, rol, experiencia, 85, 0, 0, 0);
    }

    public Piloto(int id, String nombre, String equipo, String rol, int experiencia, int nivelHabilidad, int victorias, int podios, int puntos) {
        this.id = id;
        this.nombre = nombre;
        this.equipo = equipo;
        this.rol = rol;
        this.experiencia = experiencia;
        this.nivelHabilidad = nivelHabilidad;
        this.victorias = victorias;
        this.podios = podios;
        this.puntos = puntos;
    }

    @Override
    public String toString() {
        return nombre + " (" + equipo + ")";
    }
}
