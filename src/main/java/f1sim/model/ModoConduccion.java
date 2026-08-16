package f1sim.model;

// Rendimiento del vehiculo bajo un modo de conduccion (normal, agresiva, ahorro)
public class ModoConduccion {
    public double velocidadPromedioKmh;
    public DatosCondicion consumoCombustible = new DatosCondicion();
    public DatosCondicion desgasteNeumaticos = new DatosCondicion();

    public enum PerfilEstrategia {
        PUSH("Ataque Máximo", 1.08, 1.35, 1.40),
        BALANCED("Equilibrado", 1.00, 1.00, 1.00),
        SAVE_FUEL("Ahorro de Combustible", 0.94, 0.70, 0.75);

        private final String etiqueta;
        private final double multiplicadorVelocidad;
        private final double multiplicadorConsumo;
        private final double multiplicadorDesgaste;

        PerfilEstrategia(String etiqueta, double multiplicadorVelocidad, double multiplicadorConsumo, double multiplicadorDesgaste) {
            this.etiqueta = etiqueta;
            this.multiplicadorVelocidad = multiplicadorVelocidad;
            this.multiplicadorConsumo = multiplicadorConsumo;
            this.multiplicadorDesgaste = multiplicadorDesgaste;
        }

        public String getEtiqueta() { return etiqueta; }
        public double getMultiplicadorVelocidad() { return multiplicadorVelocidad; }
        public double getMultiplicadorConsumo() { return multiplicadorConsumo; }
        public double getMultiplicadorDesgaste() { return multiplicadorDesgaste; }
    }

    public ModoConduccion() {
    }

    public ModoConduccion(double velocidadPromedioKmh, DatosCondicion consumoCombustible, DatosCondicion desgasteNeumaticos) {
        this.velocidadPromedioKmh = velocidadPromedioKmh;
        this.consumoCombustible = consumoCombustible;
        this.desgasteNeumaticos = desgasteNeumaticos;
    }

    public ModoConduccion aplicarPerfil(PerfilEstrategia perfil) {
        double vel = this.velocidadPromedioKmh * perfil.getMultiplicadorVelocidad();
        DatosCondicion cons = new DatosCondicion(
                this.consumoCombustible.seco * perfil.getMultiplicadorConsumo(),
                this.consumoCombustible.lluvioso * perfil.getMultiplicadorConsumo(),
                this.consumoCombustible.extremo * perfil.getMultiplicadorConsumo()
        );
        DatosCondicion des = new DatosCondicion(
                this.desgasteNeumaticos.seco * perfil.getMultiplicadorDesgaste(),
                this.desgasteNeumaticos.lluvioso * perfil.getMultiplicadorDesgaste(),
                this.desgasteNeumaticos.extremo * perfil.getMultiplicadorDesgaste()
        );
        return new ModoConduccion(vel, cons, des);
    }
}

