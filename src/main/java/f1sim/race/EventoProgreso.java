package f1sim.race;

// Representa el avance de UN piloto en un instante de la carrera.
// Cada hilo de piloto (productor) va creando estos eventos y los deja en la cola.
// El panel de la interfaz (consumidor) los va leyendo para actualizar telemetría y pantalla.
public class EventoProgreso {

    public final String piloto;
    public final double progresoVueltaActual; // valor entre 0.0 y 1.0 dentro de la vuelta
    public final int vueltaActual;
    public final boolean terminado;
    public final double tiempoTotalSegundos;

    // Telemetría adicional F1
    public final double velocidadKmh;
    public final int sectorActual; // 1, 2, 3
    public final double tiempoUltimaVuelta;
    public final double tiempoMejorVuelta;
    public final String compuestoNeumatico; // S, M, H
    public final double desgasteNeumatico; // 0.0 - 100.0%
    public final double combustibleRestante; // 0.0 - 100.0%
    public final boolean enPitLane;
    public final boolean drsActivo;
    public final String mensajeEvento;

    public EventoProgreso(String piloto, double progresoVueltaActual, int vueltaActual,
                           boolean terminado, double tiempoTotalSegundos,
                           double velocidadKmh, int sectorActual,
                           double tiempoUltimaVuelta, double tiempoMejorVuelta,
                           String compuestoNeumatico, double desgasteNeumatico, double combustibleRestante,
                           boolean enPitLane, boolean drsActivo, String mensajeEvento) {
        this.piloto = piloto;
        this.progresoVueltaActual = progresoVueltaActual;
        this.vueltaActual = vueltaActual;
        this.terminado = terminado;
        this.tiempoTotalSegundos = tiempoTotalSegundos;
        this.velocidadKmh = velocidadKmh;
        this.sectorActual = sectorActual;
        this.tiempoUltimaVuelta = tiempoUltimaVuelta;
        this.tiempoMejorVuelta = tiempoMejorVuelta;
        this.compuestoNeumatico = compuestoNeumatico;
        this.desgasteNeumatico = desgasteNeumatico;
        this.combustibleRestante = combustibleRestante;
        this.enPitLane = enPitLane;
        this.drsActivo = drsActivo;
        this.mensajeEvento = mensajeEvento;
    }
}
