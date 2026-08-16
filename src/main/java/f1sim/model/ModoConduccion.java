package f1sim.model;

// Rendimiento del vehiculo bajo un modo de conduccion (normal, agresiva, ahorro)
public class ModoConduccion {
    public double velocidadPromedioKmh;
    public DatosCondicion consumoCombustible = new DatosCondicion();
    public DatosCondicion desgasteNeumaticos = new DatosCondicion();

    public ModoConduccion() {
    }

    public ModoConduccion(double velocidadPromedioKmh, DatosCondicion consumoCombustible, DatosCondicion desgasteNeumaticos) {
        this.velocidadPromedioKmh = velocidadPromedioKmh;
        this.consumoCombustible = consumoCombustible;
        this.desgasteNeumaticos = desgasteNeumaticos;
    }
}
