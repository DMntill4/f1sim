package f1sim.race;

// Gestor de eventos dinámicos durante la carrera (Safety Car, Virtual Safety Car, Bandera Roja)
public class GestorEventosCarrera {

    public enum EstadoCarrera {
        NORMAL("Bandera Verde"),
        VSC("Virtual Safety Car (VSC)"),
        SAFETY_CAR("Safety Car (SC)"),
        RED_FLAG("Bandera Roja");

        private final String descripcion;

        EstadoCarrera(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }

    private EstadoCarrera estadoActual = EstadoCarrera.NORMAL;

    public EstadoCarrera evaluarEventoAleatorio() {
        double prob = Math.random();
        if (prob < 0.03) {
            estadoActual = EstadoCarrera.SAFETY_CAR;
        } else if (prob < 0.08) {
            estadoActual = EstadoCarrera.VSC;
        } else {
            estadoActual = EstadoCarrera.NORMAL;
        }
        return estadoActual;
    }

    public double obtenerFactorVelocidad(EstadoCarrera estado) {
        if (estado == EstadoCarrera.SAFETY_CAR) return 0.50;
        if (estado == EstadoCarrera.VSC) return 0.70;
        if (estado == EstadoCarrera.RED_FLAG) return 0.00;
        return 1.00;
    }


    public EstadoCarrera getEstadoActual() {
        return estadoActual;
    }
}
