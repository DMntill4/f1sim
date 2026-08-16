package f1sim.model;

import java.util.ArrayList;
import java.util.List;

// Representa un circuito de carreras
public class Circuito {
    public String nombre;
    public String pais;
    public double longitudKm;
    public int vueltas;
    public String descripcion;
    public String climaPromedio; // seco, lluvioso, extremo
    public RecordVuelta recordVuelta = new RecordVuelta();
    public List<Ganador> ganadores = new ArrayList<>();
    public String imagen;
    public double factorAbrasividad = 1.0; // Multiplicador de desgaste y consumo (0.8 suave, 1.0 medio, 1.3 muy abrasivo)

    public Circuito() {
    }

    public Circuito(String nombre, String pais, double longitudKm, int vueltas, String descripcion,
                     String climaPromedio, RecordVuelta recordVuelta, List<Ganador> ganadores, String imagen) {
        this(nombre, pais, longitudKm, vueltas, descripcion, climaPromedio, recordVuelta, ganadores, imagen, 1.0);
    }

    public Circuito(String nombre, String pais, double longitudKm, int vueltas, String descripcion,
                     String climaPromedio, RecordVuelta recordVuelta, List<Ganador> ganadores, String imagen, double factorAbrasividad) {
        this.nombre = nombre;
        this.pais = pais;
        this.longitudKm = longitudKm;
        this.vueltas = vueltas;
        this.descripcion = descripcion;
        this.climaPromedio = climaPromedio;
        this.recordVuelta = recordVuelta;
        this.ganadores = ganadores;
        this.imagen = imagen;
        this.factorAbrasividad = factorAbrasividad;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
