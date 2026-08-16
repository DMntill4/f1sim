package f1sim.model;

import java.util.ArrayList;
import java.util.List;

// Representa una escuderia de F1
public class Equipo {
    public String nombre;
    public String pais;
    public String motor;
    public List<Integer> pilotos = new ArrayList<>(); // ids de pilotos
    public String imagen; // url del logo

    public Equipo() {
    }

    public Equipo(String nombre, String pais, String motor, List<Integer> pilotos, String imagen) {
        this.nombre = nombre;
        this.pais = pais;
        this.motor = motor;
        this.pilotos = pilotos;
        this.imagen = imagen;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
