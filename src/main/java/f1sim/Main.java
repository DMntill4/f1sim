package f1sim;

import f1sim.ui.F1Theme;
import f1sim.datos.GestorDatos;
import f1sim.ui.VentanaLogin;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception e) {
                // Fallback
            }

            // Aplicar tema F1
            F1Theme.applyGlobalTheme();

            // Cargar datos iniciales de la especificacion si no existen
            GestorDatos.cargarDatosIniciales();

            // Iniciar aplicacion desplegando primero la Ventana de Login
            VentanaLogin login = new VentanaLogin();
            login.setVisible(true);
        });
    }
}
