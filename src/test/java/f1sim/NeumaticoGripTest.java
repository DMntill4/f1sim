package f1sim;

import f1sim.model.TipoNeumatico;
import f1sim.model.Vehiculo;

public class NeumaticoGripTest {

    public static void main(String[] args) {
        System.out.println("=== Ejecutando pruebas unitarias de Neumáticos y Clima ===");

        Vehiculo testCar = new Vehiculo();
        testCar.tipoNeumatico = TipoNeumatico.SOFT;
        testCar.porcentajeDesgasteNeumatico = 10.0;

        double gripSeco = testCar.calcularRendimientoTermico("agresiva", "seco");
        System.out.println("Agarre Neumático SOFT en Seco: " + gripSeco);

        double gripLluvia = testCar.calcularRendimientoTermico("agresiva", "lluvia");
        System.out.println("Agarre Neumático SOFT en Lluvia (Penalizado): " + gripLluvia);

        if (gripSeco > gripLluvia) {
            System.out.println("✅ PRUEBA PASADA: El neumático de seco pierde agarre adecuadamente en lluvia.");
        } else {
            System.err.println("❌ PRUEBA FALLIDA: El cálculo de agarre en clima adverso no es correcto.");
            System.exit(1);
        }
    }
}
