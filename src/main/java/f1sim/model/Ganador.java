package f1sim.model;

// Ganador de una temporada en un circuito (referencia al id de piloto)
public class Ganador {
    public int temporada;
    public int piloto;

    public Ganador() {
    }

    public Ganador(int temporada, int piloto) {
        this.temporada = temporada;
        this.piloto = piloto;
    }
}
