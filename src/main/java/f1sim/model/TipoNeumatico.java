package f1sim.model;

// Enum que define los tipos de compuestos de neumáticos de F1 y su comportamiento físico
public enum TipoNeumatico {
    SOFT("Blando", 1.05, 0.08, "Seco"),
    MEDIUM("Medio", 1.00, 0.04, "Seco"),
    HARD("Duro", 0.95, 0.02, "Seco"),
    WET("Lluvia", 0.85, 0.03, "Lluvia");

    private final String nombre;
    private final double factorAgarreBase;  // Multiplicador de velocidad/agarre
    private final double tasaDegradacion;   // Porcentaje de desgaste por vuelta
    private final String condicionOptima;   // "Seco" o "Lluvia"

    TipoNeumatico(String nombre, double factorAgarreBase, double tasaDegradacion, String condicionOptima) {
        this.nombre = nombre;
        this.factorAgarreBase = factorAgarreBase;
        this.tasaDegradacion = tasaDegradacion;
        this.condicionOptima = condicionOptima;
    }

    public String getNombre() {
        return nombre;
    }

    public double getFactorAgarreBase() {
        return factorAgarreBase;
    }

    public double getTasaDegradacion() {
        return tasaDegradacion;
    }

    public String getCondicionOptima() {
        return condicionOptima;
    }

    public double calcularAgarreEfectivo(double porcentajeDesgaste, String condicionClimaticas) {
        double desgasteImpacto = Math.max(0.4, 1.0 - (porcentajeDesgaste * tasaDegradacion));
        double penalizacionClima = 1.0;
        
        if ("Lluvia".equalsIgnoreCase(condicionClimaticas) && !"Lluvia".equalsIgnoreCase(condicionOptima)) {
            penalizacionClima = 0.60; // Gran penalización por usar secos en lluvia
        } else if ("Seco".equalsIgnoreCase(condicionClimaticas) && "Lluvia".equalsIgnoreCase(condicionOptima)) {
            penalizacionClima = 0.70; // Penalización por usar neumáticos de lluvia en seco
        }
        
        return factorAgarreBase * desgasteImpacto * penalizacionClima;
    }
}
