package f1sim.ui;

import f1sim.datos.GestorDatos;
import f1sim.model.ResultadoClasificacion;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DialogoHistorialResultados extends JDialog {

    private JComboBox<String> comboFiltroCircuito;
    private DefaultTableModel modeloTabla;
    private JTable tablaHistorial;
    private JLabel etiquetaEstadisticas;
    private List<ResultadoClasificacion> historialCompleto;

    public DialogoHistorialResultados(Frame owner) {
        super(owner, "Historial de Resultados de Clasificación", true);
        this.historialCompleto = GestorDatos.cargarResultados();

        setSize(860, 540);
        setLocationRelativeTo(owner);
        getContentPane().setBackground(F1Theme.BG_DARK);
        setLayout(new BorderLayout(12, 12));

        add(construirPanelFiltros(), BorderLayout.NORTH);
        add(construirTablaResultados(), BorderLayout.CENTER);
        add(construirPanelInferior(), BorderLayout.SOUTH);

        actualizarTabla();
    }

    private JPanel construirPanelFiltros() {
        JPanel panel = F1Theme.createCardPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 6));

        JLabel lblFiltro = new JLabel("Filtrar por Circuito:");
        lblFiltro.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(lblFiltro);

        List<String> circuitosUnicos = new ArrayList<>();
        circuitosUnicos.add("TODOS LOS CIRCUITOS");
        for (ResultadoClasificacion r : historialCompleto) {
            if (r.circuito != null && !circuitosUnicos.contains(r.circuito)) {
                circuitosUnicos.add(r.circuito);
            }
        }

        comboFiltroCircuito = new JComboBox<>(circuitosUnicos.toArray(new String[0]));
        comboFiltroCircuito.setPreferredSize(new Dimension(240, 28));
        comboFiltroCircuito.addActionListener(e -> actualizarTabla());
        panel.add(comboFiltroCircuito);

        JButton btnRefrescar = F1Theme.createF1Button("Actualizar", false);
        btnRefrescar.addActionListener(e -> {
            historialCompleto = GestorDatos.cargarResultados();
            actualizarTabla();
        });
        panel.add(btnRefrescar);

        return panel;
    }

    private JPanel construirTablaResultados() {
        JPanel panel = F1Theme.createCardPanel();
        panel.setLayout(new BorderLayout());

        String[] columnas = {"Fecha / Hora", "Piloto", "Vehículo", "Circuito", "Clima", "Modo", "Aero", "Presión", "Estrategia", "Tiempo de Vuelta"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaHistorial = new JTable(modeloTabla);
        F1Theme.styleTable(tablaHistorial);

        JScrollPane scroll = new JScrollPane(tablaHistorial);
        F1Theme.styleScrollPane(scroll);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel construirPanelInferior() {
        JPanel panel = F1Theme.createCardPanel();
        panel.setLayout(new BorderLayout(10, 0));

        etiquetaEstadisticas = new JLabel("Cargando historial...", SwingConstants.LEFT);
        etiquetaEstadisticas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        etiquetaEstadisticas.setForeground(F1Theme.TEXT_MUTED);
        panel.add(etiquetaEstadisticas, BorderLayout.CENTER);

        JButton btnCerrar = F1Theme.createF1Button("Cerrar", false);
        btnCerrar.addActionListener(e -> this.dispose());
        panel.add(btnCerrar, BorderLayout.EAST);

        return panel;
    }

    private void actualizarTabla() {
        String circuitoSeleccionado = (String) comboFiltroCircuito.getSelectedItem();
        modeloTabla.setRowCount(0);

        int totalRegistros = 0;
        double tiempoMasRapido = Double.MAX_VALUE;
        String pilotoMejor = "-";

        for (ResultadoClasificacion r : historialCompleto) {
            if (circuitoSeleccionado != null && !circuitoSeleccionado.equals("TODOS LOS CIRCUITOS")) {
                if (r.circuito == null || !r.circuito.equalsIgnoreCase(circuitoSeleccionado)) {
                    continue;
                }
            }

            totalRegistros++;
            if (r.tiempoVueltaSegundos < tiempoMasRapido) {
                tiempoMasRapido = r.tiempoVueltaSegundos;
                pilotoMejor = r.piloto;
            }

            modeloTabla.addRow(new Object[]{
                    r.fecha, r.piloto, r.vehiculo, r.circuito, r.clima, r.modo,
                    r.cargaAerodinamica != null ? r.cargaAerodinamica : "media",
                    r.presionNeumaticos != null ? r.presionNeumaticos : "estandar",
                    r.estrategiaCombustible != null ? r.estrategiaCombustible : "balanceada",
                    formatearTiempo(r.tiempoVueltaSegundos)
            });
        }

        if (totalRegistros > 0 && tiempoMasRapido < Double.MAX_VALUE) {
            etiquetaEstadisticas.setText(String.format("Registros guardados: %d  |  Mejor Vuelta: %s por %s",
                    totalRegistros, formatearTiempo(tiempoMasRapido), pilotoMejor));
        } else {
            etiquetaEstadisticas.setText("No hay registros de clasificación guardados para este filtro.");
        }
    }

    private String formatearTiempo(double segs) {
        int mins = (int) (segs / 60);
        double s = segs - (mins * 60);
        return String.format("%d:%06.3f", mins, s);
    }
}
