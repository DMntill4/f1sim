package f1sim.datos;

import f1sim.model.ResultadoClasificacion;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

// Utilidad para exportar resultados finales de la simulación a archivos CSV y JSON
public class ExportadorDatos {

    public static boolean exportarACSV(String rutaArchivo, List<ResultadoClasificacion> resultados) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(rutaArchivo))) {
            writer.println("Fecha,Piloto,Vehiculo,Circuito,TiempoSegundos,ModoConduccion,Clima");
            for (ResultadoClasificacion r : resultados) {
                writer.printf("%s,%s,%s,%s,%.3f,%s,%s%n",
                        r.fecha != null ? r.fecha : "",
                        r.piloto != null ? r.piloto : "",
                        r.vehiculo != null ? r.vehiculo : "",
                        r.circuito != null ? r.circuito : "",
                        r.tiempoVueltaSegundos,
                        r.modo != null ? r.modo : "",
                        r.clima != null ? r.clima : "");
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error al exportar resultados a CSV: " + e.getMessage());
            return false;
        }
    }

}
