package f1sim;

import f1sim.ui.F1Theme;
import f1sim.datos.GestorDatos;
import f1sim.ui.VentanaLogin;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            configurarApariencia();
            GestorDatos.cargarDatosIniciales();
            new VentanaLogin().setVisible(true);
        });
    }

    private static void configurarApariencia() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        F1Theme.applyGlobalTheme();
    }
}

