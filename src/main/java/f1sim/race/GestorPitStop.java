package f1sim.race;

import f1sim.model.TipoNeumatico;

// Gestor del tiempo y eventos durante una parada en boxes (Pit Stop)
public class GestorPitStop {

    private static final double TIEMPO_PIT_LANE_BASE_SEG = 20.0; // Tiempo recorriendo el pit lane a 80 km/h
    private static final double TIEMPO_CAMBIO_RUEDAS_BASE_SEG = 2.5; // Parada estándar de mecánicos

    public static class ResultadoPitStop {
        public double tiempoTotalSegundos;
        public boolean huboFalloMecanico;
        public double retrasoFalloSegundos;
        public TipoNeumatico nuevoNeumatico;
        public String detalle;

        public ResultadoPitStop(double tiempoTotalSegundos, boolean huboFalloMecanico, double retrasoFalloSegundos, TipoNeumatico nuevoNeumatico, String detalle) {
            this.tiempoTotalSegundos = tiempoTotalSegundos;
            this.huboFalloMecanico = huboFalloMecanico;
            this.retrasoFalloSegundos = retrasoFalloSegundos;
            this.nuevoNeumatico = nuevoNeumatico;
            this.detalle = detalle;
        }
    }

    public static ResultadoPitStop realizarParada(TipoNeumatico nuevoNeumatico, boolean cambiarAleron) {
        double tiempoServicio = TIEMPO_CAMBIO_RUEDAS_BASE_SEG;
        boolean fallo = false;
        double retraso = 0.0;

        // Probabilidad del 10% de fallo humano en el pit stop (tuerca atascada, etc.)
        if (Math.random() < 0.10) {
            fallo = true;
            retraso = 1.5 + (Math.random() * 3.5); // Retraso entre 1.5 y 5.0 segundos
            tiempoServicio += retraso;
        }

        if (cambiarAleron) {
            tiempoServicio += 5.0; // Penalización por cambiar morro/alerón delantero
        }

        double tiempoTotal = TIEMPO_PIT_LANE_BASE_SEG + tiempoServicio;
        String detalle = String.format("Pit Stop: %.2fs en caja (Total: %.2fs)", tiempoServicio, tiempoTotal);
        if (fallo) {
            detalle += String.format(" [⚠️ FALLO EN TUERCA: +%.2fs]", retraso);
        }

        return new ResultadoPitStop(tiempoTotal, fallo, retraso, nuevoNeumatico, detalle);
    }
}
