package f1sim.model;

// Guarda un valor distinto segun el clima: seco, lluvioso o extremo
public class DatosCondicion {
    public double seco;
    public double lluvioso;
    public double extremo;

    public DatosCondicion() {
    }

    public DatosCondicion(double seco, double lluvioso, double extremo) {
        this.seco = seco;
        this.lluvioso = lluvioso;
        this.extremo = extremo;
    }

    // Devuelve el valor segun el nombre del clima
    public double obtenerPorClima(String clima) {
        if (clima.equalsIgnoreCase("lluvioso") || clima.equalsIgnoreCase("lluvia")) {
            return lluvioso;
        } else if (clima.equalsIgnoreCase("extremo")) {
            return extremo;
        } else {
            return seco;
        }
    }

    public static String simularCambioClima(String climaActual, double probabilidadCambio) {
        if (Math.random() < probabilidadCambio) {
            return "seco".equalsIgnoreCase(climaActual) ? "lluvioso" : "seco";
        }
        return climaActual;
    }
}

