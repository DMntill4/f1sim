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
            writer.println("Posicion,Piloto,Equipo,TiempoVuelta,ModoConduccion,Clima");
            for (ResultadoClasificacion r : resultados) {
                writer.printf("%d,%s,%s,%s,%s,%s%n",
                        r.posicion,
                        r.nombrePiloto,
                        r.nombreEquipo,
                        r.tiempoFormateado,
                        r.modoConduccion,
                        r.clima);
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error al exportar resultados a CSV: " + e.getMessage());
            return false;
        }
    }
}
