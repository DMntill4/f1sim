package f1sim.model;

// Record historico de vuelta rapida en un circuito
public class RecordVuelta {
    public String tiempo;
    public String piloto;
    public int anio;

    public RecordVuelta() {
    }

    public RecordVuelta(String tiempo, String piloto, int anio) {
        this.tiempo = tiempo;
        this.piloto = piloto;
        this.anio = anio;
    }
}
