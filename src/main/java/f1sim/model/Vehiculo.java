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

    public TipoNeumatico tipoNeumatico = TipoNeumatico.MEDIUM;
    public double porcentajeDesgasteNeumatico = 0.0; // 0.0% a 100.0%
    public double temperaturaNeumaticosC = 90.0;    // Grados Celsius (óptimo 90-105°C)

    public Vehiculo() {
    }

    public double calcularRendimientoTermico(String modoConduccion, String clima) {
        // Ajuste de temperatura según agresividad
        if ("agresiva".equalsIgnoreCase(modoConduccion)) {
            temperaturaNeumaticosC = Math.min(130.0, temperaturaNeumaticosC + 2.5);
        } else if ("ahorro".equalsIgnoreCase(modoConduccion)) {
            temperaturaNeumaticosC = Math.max(70.0, temperaturaNeumaticosC - 1.5);
        }

        // Penalización si los neumáticos están sobrecalentados (> 110°C) o muy fríos (< 80°C)
        double factorTemperatura = 1.0;
        if (temperaturaNeumaticosC > 110.0) {
            factorTemperatura = 0.90; // Degrada agarre por sobrecalentamiento
        } else if (temperaturaNeumaticosC < 80.0) {
            factorTemperatura = 0.92; // Falta de temperatura
        }

        double agarreBase = tipoNeumatico.calcularAgarreEfectivo(porcentajeDesgasteNeumatico, clima);
        return agarreBase * factorTemperatura;
    }


    @Override
    public String toString() {
        return equipo + " - " + modelo;
    }

    public ModoConduccion obtenerModo(String nombreModo) {
        if ("agresiva".equalsIgnoreCase(nombreModo)) {
            return agresiva != null ? agresiva : new ModoConduccion();
        } else if ("ahorro".equalsIgnoreCase(nombreModo)) {
            return ahorro != null ? ahorro : new ModoConduccion();
        } else {
            return normal != null ? normal : new ModoConduccion();
        }
    }

    public boolean esValido() {
        return equipo != null && !equipo.isEmpty() && modelo != null && !modelo.isEmpty() && velocidadMaximaKmh > 0;
    }
}

