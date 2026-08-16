package f1sim.model;

import java.util.ArrayList;
import java.util.List;

// Representa un vehículo de F1 con configuración aerodinámica y presión de neumáticos
public class Vehiculo {
    public String equipo;
    public String modelo;
    public String motor;
    public double velocidadMaximaKmh;
    public double aceleracion; // 0 a 100 km/h en segundos
    public List<Integer> pilotos = new ArrayList<>();
    public String imagen; // url de la imagen del auto

    // Ajustes de configuración del vehículo
    public String cargaAerodinamica = "media"; // baja, media, alta
    public String presionNeumaticos = "estandar"; // baja, estandar, alta

    public ModoConduccion normal = new ModoConduccion();
    public ModoConduccion agresiva = new ModoConduccion();
    public ModoConduccion ahorro = new ModoConduccion();

    public Vehiculo() {
    }

    @Override
    public String toString() {
        return equipo + " - " + modelo;
    }

    public ModoConduccion obtenerModo(String nombreModo) {
        if ("agresiva".equalsIgnoreCase(nombreModo)) {
            return agresiva;
        } else if ("ahorro".equalsIgnoreCase(nombreModo)) {
            return ahorro;
        } else {
            return normal;
        }
    }
}
