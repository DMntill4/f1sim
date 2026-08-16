package f1sim.race;

import f1sim.model.Circuito;
import f1sim.model.ModoConduccion;
import f1sim.model.Piloto;
import f1sim.model.Vehiculo;

import java.util.concurrent.BlockingQueue;

// Hilo que simula la carrera de UN piloto con telemetría F1: desgaste de neumáticos, consumo de combustible,
// paradas en boxes, zonas DRS, sectores y cálculo de velocidad instantánea.
public class HiloPiloto implements Runnable {

    private static final int INTERVALO_TICK_MS = 40;
    private static double factorAceleracionGlobal = 15.0;

    private final Piloto piloto;
    private final Vehiculo vehiculo;
    private final Circuito circuito;
    private final String modo;
    private final String clima;
    private final BlockingQueue<EventoProgreso> colaEventos;

    private volatile boolean detener = false;
    private volatile boolean pausado = false;
    private volatile boolean saltarAMeta = false;

    private String compuestoNeumatico = "M"; // Medium por defecto
    private double desgasteNeumatico = 0.0; // 0% a 100%
    private double combustibleRestante = 100.0; // 100% a 0%
    private double tiempoMejorVuelta = Double.MAX_VALUE;
    private double tiempoUltimaVuelta = 0.0;

    public HiloPiloto(Piloto piloto, Vehiculo vehiculo, Circuito circuito, String modo, String clima,
                       BlockingQueue<EventoProgreso> colaEventos) {
        this.piloto = piloto;
        this.vehiculo = vehiculo;
        this.circuito = circuito;
        this.modo = modo;
        this.clima = clima;
        this.colaEventos = colaEventos;

        if ("Lider".equalsIgnoreCase(piloto.rol)) {
            this.compuestoNeumatico = "S";
        } else {
            this.compuestoNeumatico = "M";
        }
    }

    public static void setFactorAceleracionGlobal(double factor) {
        factorAceleracionGlobal = factor;
    }

    public void detener() {
        this.detener = true;
    }

    public void setPausado(boolean pausado) {
        this.pausado = pausado;
    }

    public void saltarAMeta() {
        this.saltarAMeta = true;
    }

    @Override
    public void run() {
        double tiempoBaseVuelta = calcularTiempoBaseVuelta();
        double tiempoTotalSegundos = 0;
        int totalVueltas = circuito.vueltas > 0 ? circuito.vueltas : 1;

        // Emitir evento inicial inmediato al arrancar (Vuelta 1, 0% recorrido)
        try {
            colaEventos.put(new EventoProgreso(
                    piloto.nombre, 0.0, 1, false, 0.0,
                    220.0, 1, 0.0, Double.MAX_VALUE,
                    compuestoNeumatico, 0.0, 100.0, false, false, "Salida a pista"
            ));
        } catch (InterruptedException ignored) {}

        boolean yaHizoPitStop = false;

        for (int vuelta = 1; vuelta <= totalVueltas && !detener; vuelta++) {
            if (saltarAMeta) {
                // Simulación instantánea hasta el final
                int vueltasRestantes = totalVueltas - vuelta + 1;
                double tiempoExtra = vueltasRestantes * tiempoBaseVuelta * (0.97 + (Math.random() * 0.06));
                tiempoTotalSegundos += tiempoExtra;
                tiempoUltimaVuelta = tiempoBaseVuelta * (0.97 + (Math.random() * 0.05));
                if (tiempoUltimaVuelta < tiempoMejorVuelta) tiempoMejorVuelta = tiempoUltimaVuelta;

                try {
                    colaEventos.put(new EventoProgreso(
                            piloto.nombre, 1.0, totalVueltas, true, tiempoTotalSegundos,
                            0.0, 3, tiempoUltimaVuelta, tiempoMejorVuelta,
                            compuestoNeumatico, 55.0, 15.0, false, false, "¡Simulación rápida completada!"
                    ));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return;
            }

            double progresoVuelta = 0.0;
            double tiempoSimuladoVuelta = 0.0;
            String eventoMensaje = null;

            boolean hacerPitStopEnEstaVuelta = (!yaHizoPitStop && totalVueltas > 2 && vuelta == Math.max(2, totalVueltas / 2))
                    || desgasteNeumatico > 75.0 || combustibleRestante < 20.0;

            while (progresoVuelta < 1.0 && !detener) {
                if (saltarAMeta) break;

                while (pausado && !detener && !saltarAMeta) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }

                try {
                    Thread.sleep(INTERVALO_TICK_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }

                double tiempoSimuladoTick = (INTERVALO_TICK_MS * factorAceleracionGlobal) / 1000.0;
                tiempoTotalSegundos += tiempoSimuladoTick;
                tiempoSimuladoVuelta += tiempoSimuladoTick;

                // Desgaste de neumáticos y consumo de combustible en vivo (ajustado por abrasividad del circuito)
                double factorAbrasivo = circuito != null && circuito.factorAbrasividad > 0 ? circuito.factorAbrasividad : 1.0;
                desgasteNeumatico = Math.min(100.0, desgasteNeumatico + (0.35 * factorAbrasivo * (tiempoSimuladoTick / 2.0)));
                double tasaConsumoGasolina = (85.0 / totalVueltas) * (tiempoSimuladoTick / tiempoBaseVuelta) * factorAbrasivo;
                combustibleRestante = Math.max(5.0, combustibleRestante - tasaConsumoGasolina);

                double factorRendimientoNeumatico = 1.0 + (desgasteNeumatico * 0.001);

                double variacion = (0.95 + (Math.random() * 0.10)) / factorRendimientoNeumatico;
                double incremento = (tiempoSimuladoTick / tiempoBaseVuelta) * variacion;

                progresoVuelta += incremento;
                progresoVuelta = Math.min(progresoVuelta, 1.0);

                int sector = 1;
                if (progresoVuelta > 0.66) sector = 3;
                else if (progresoVuelta > 0.33) sector = 2;

                boolean drsActivo = (sector == 2 && progresoVuelta >= 0.40 && progresoVuelta <= 0.60);

                ModoConduccion modoObj = vehiculo.obtenerModo(modo);
                double velBase = modoObj.velocidadPromedioKmh > 0 ? modoObj.velocidadPromedioKmh : 240.0;
                double velocidadKmh = velBase * (drsActivo ? 1.12 : 0.98) + ((Math.random() - 0.5) * 15.0);

                boolean enPitLane = false;
                if (hacerPitStopEnEstaVuelta && progresoVuelta >= 0.85 && progresoVuelta <= 0.95) {
                    enPitLane = true;
                    velocidadKmh = 80.0;
                    desgasteNeumatico = 0.0; // Neumáticos nuevos
                    combustibleRestante = Math.min(100.0, combustibleRestante + 50.0); // Recarga de combustible
                    compuestoNeumatico = "H";
                    yaHizoPitStop = true;
                    hacerPitStopEnEstaVuelta = false;
                    eventoMensaje = "BOXES: Parada en Pits (2.4s) - Cambio a " + compuestoNeumatico + " y Reabastecimiento";
                }

                try {
                    colaEventos.put(new EventoProgreso(
                            piloto.nombre, progresoVuelta, vuelta, false, tiempoTotalSegundos,
                            velocidadKmh, sector, tiempoUltimaVuelta, tiempoMejorVuelta,
                            compuestoNeumatico, desgasteNeumatico, combustibleRestante, enPitLane, drsActivo, eventoMensaje
                    ));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                eventoMensaje = null;
            }

            tiempoUltimaVuelta = tiempoSimuladoVuelta;
            if (tiempoUltimaVuelta < tiempoMejorVuelta) {
                tiempoMejorVuelta = tiempoUltimaVuelta;
            }
        }

        if (!detener && !saltarAMeta) {
            try {
                colaEventos.put(new EventoProgreso(
                        piloto.nombre, 1.0, totalVueltas, true, tiempoTotalSegundos,
                        0.0, 3, tiempoUltimaVuelta, tiempoMejorVuelta,
                        compuestoNeumatico, desgasteNeumatico, combustibleRestante, false, false, "¡BANDERA A CUADROS! Finalizó la carrera."
                ));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private double calcularTiempoBaseVuelta() {
        ModoConduccion modoConduccion = vehiculo.obtenerModo(modo);
        double velocidad = modoConduccion.velocidadPromedioKmh > 0 ? modoConduccion.velocidadPromedioKmh : 250;
        double tiempoBaseSegundos = (circuito.longitudKm / velocidad) * 3600.0;

        double factorClima = 1.0;
        if ("lluvioso".equalsIgnoreCase(clima)) factorClima = 1.08;
        else if ("extremo".equalsIgnoreCase(clima)) factorClima = 1.18;

        int experiencia = Math.min(piloto.experiencia, 15);
        double factorExperiencia = 1.0 - (experiencia * 0.002);

        return tiempoBaseSegundos * factorClima * factorExperiencia;
    }
}
