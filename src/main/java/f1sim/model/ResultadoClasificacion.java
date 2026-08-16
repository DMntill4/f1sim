package f1sim.model;

// Resultado guardado de una sesion de clasificacion (para persistencia e historial)
public class ResultadoClasificacion {
    public String fecha;
    public String piloto;
    public String vehiculo;
    public String circuito;
    public String clima;
    public String modo;
    public double tiempoVueltaSegundos;

    // Campos de usuario y reglajes detallados
    public int usuarioId = 1;
    public String cargaAerodinamica = "media";
    public String presionNeumaticos = "estandar";
    public String estrategiaCombustible = "balanceada";

    public ResultadoClasificacion() {
    }

    public ResultadoClasificacion(String fecha, String piloto, String vehiculo, String circuito,
                                   String clima, String modo, double tiempoVueltaSegundos) {
        this(fecha, piloto, vehiculo, circuito, clima, modo, tiempoVueltaSegundos, 1, "media", "estandar", "balanceada");
    }

    public ResultadoClasificacion(String fecha, String piloto, String vehiculo, String circuito,
                                   String clima, String modo, double tiempoVueltaSegundos,
                                   int usuarioId, String cargaAerodinamica, String presionNeumaticos, String estrategiaCombustible) {
        this.fecha = fecha;
        this.piloto = piloto;
        this.vehiculo = vehiculo;
        this.circuito = circuito;
        this.clima = clima;
        this.modo = modo;
        this.tiempoVueltaSegundos = tiempoVueltaSegundos;
        this.usuarioId = usuarioId;
        this.cargaAerodinamica = cargaAerodinamica;
        this.presionNeumaticos = presionNeumaticos;
        this.estrategiaCombustible = estrategiaCombustible;
    }
}
