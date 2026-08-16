package f1sim.ui;

// Clase de utilidades simple para formatear tiempos y valores numéricos en la interfaz Swing
public class FormateadorF1 {

    // Convierte segundos totales (ej. 75.321) a formato estándar de F1 (1:15.321)
    public static String formatearTiempoVuelta(double segundosTotales) {
        if (segundosTotales <= 0 || segundosTotales >= 9999) {
            return "--:--";
        }
        int minutos = (int) (segundosTotales / 60);
        double segundos = segundosTotales - (minutos * 60);
        return String.format("%d:%06.3f", minutos, segundos);
    }

    // Convierte valores numéricos a porcentaje formateado sin decimales (ej. 85%)
    public static String formatearPorcentaje(double porcentaje) {
        int valorInt = Math.max(0, (int) porcentaje);
        return valorInt + "%";
    }

    // Formatea velocidades en kilómetros por hora (ej. 320 km/h)
    public static String formatearVelocidad(double kmh) {
        return String.format("%.0f km/h", Math.max(0, kmh));
    }
}
