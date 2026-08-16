package f1sim.ui;

import f1sim.model.Vehiculo;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DialogoCompararVehiculos extends JDialog {

    private JComboBox<Vehiculo> comboVehiculoA;
    private JComboBox<Vehiculo> comboVehiculoB;
    private DefaultTableModel modeloTabla;
    private JTable tablaComparacion;
    private List<Vehiculo> vehiculos;

    public DialogoCompararVehiculos(Frame owner, List<Vehiculo> vehiculos) {
        super(owner, "Comparación de Vehículos F1", true);
        this.vehiculos = vehiculos;

        setSize(780, 520);
        setLocationRelativeTo(owner);
        getContentPane().setBackground(F1Theme.BG_DARK);
        setLayout(new BorderLayout(12, 12));

        add(construirPanelSuperior(), BorderLayout.NORTH);
        add(construirTablaComparacion(), BorderLayout.CENTER);

        if (vehiculos.size() >= 2) {
            comboVehiculoB.setSelectedIndex(1);
        }
        actualizarComparacion();
    }

    private JPanel construirPanelSuperior() {
        JPanel panel = F1Theme.createCardPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 8));

        JLabel lblA = new JLabel("Vehículo 1:");
        lblA.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(lblA);

        comboVehiculoA = new JComboBox<>(vehiculos.toArray(new Vehiculo[0]));
        comboVehiculoA.setPreferredSize(new Dimension(220, 30));
        comboVehiculoA.addActionListener(e -> actualizarComparacion());
        panel.add(comboVehiculoA);

        JLabel lblVs = new JLabel("VS");
        lblVs.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblVs.setForeground(F1Theme.F1_RED);
        panel.add(lblVs);

        JLabel lblB = new JLabel("Vehículo 2:");
        lblB.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(lblB);

        comboVehiculoB = new JComboBox<>(vehiculos.toArray(new Vehiculo[0]));
        comboVehiculoB.setPreferredSize(new Dimension(220, 30));
        comboVehiculoB.addActionListener(e -> actualizarComparacion());
        panel.add(comboVehiculoB);

        return panel;
    }

    private JPanel construirTablaComparacion() {
        JPanel panel = F1Theme.createCardPanel();
        panel.setLayout(new BorderLayout());

        String[] columnas = {"Parámetro de Rendimiento", "Vehículo 1", "Vehículo 2", "Ventaja"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaComparacion = new JTable(modeloTabla);
        F1Theme.styleTable(tablaComparacion);

        tablaComparacion.getColumnModel().getColumn(0).setPreferredWidth(220);
        tablaComparacion.getColumnModel().getColumn(1).setPreferredWidth(180);
        tablaComparacion.getColumnModel().getColumn(2).setPreferredWidth(180);
        tablaComparacion.getColumnModel().getColumn(3).setPreferredWidth(140);

        tablaComparacion.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setFont(new Font("Segoe UI", Font.BOLD, 11));
                String text = value != null ? value.toString() : "";
                if (text.contains("Vehículo 1")) {
                    label.setForeground(F1Theme.COLOR_GREEN);
                } else if (text.contains("Vehículo 2")) {
                    label.setForeground(F1Theme.COLOR_BLUE);
                } else {
                    label.setForeground(F1Theme.TEXT_MUTED);
                }
                return label;
            }
        });

        JScrollPane scroll = new JScrollPane(tablaComparacion);
        F1Theme.styleScrollPane(scroll);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private void actualizarComparacion() {
        Vehiculo v1 = (Vehiculo) comboVehiculoA.getSelectedItem();
        Vehiculo v2 = (Vehiculo) comboVehiculoB.getSelectedItem();

        if (v1 == null || v2 == null) return;

        modeloTabla.setRowCount(0);

        agregarFila("Equipo", v1.equipo, v2.equipo, "-");
        agregarFila("Modelo", v1.modelo, v2.modelo, "-");
        agregarFila("Motor", v1.motor, v2.motor, "-");

        agregarFilaComp("Velocidad Máxima (km/h)", v1.velocidadMaximaKmh, v2.velocidadMaximaKmh, true, " km/h");
        agregarFilaComp("Aceleración 0-100 (s)", v1.aceleracion, v2.aceleracion, false, " s");

        agregarFila("Carga Aerodinámica", v1.cargaAerodinamica.toUpperCase(), v2.cargaAerodinamica.toUpperCase(), "-");
        agregarFila("Presión Neumáticos", v1.presionNeumaticos.toUpperCase(), v2.presionNeumaticos.toUpperCase(), "-");

        if (v1.normal != null && v2.normal != null) {
            agregarFilaComp("Velocidad Prom. (Modo Normal)", v1.normal.velocidadPromedioKmh, v2.normal.velocidadPromedioKmh, true, " km/h");
            if (v1.normal.consumoCombustible != null && v2.normal.consumoCombustible != null) {
                agregarFilaComp("Consumo Combustible (Seco)", v1.normal.consumoCombustible.seco, v2.normal.consumoCombustible.seco, false, " L/v");
            }
            if (v1.normal.desgasteNeumaticos != null && v2.normal.desgasteNeumaticos != null) {
                agregarFilaComp("Desgaste Neumáticos (Seco)", v1.normal.desgasteNeumaticos.seco, v2.normal.desgasteNeumaticos.seco, false, " %/v");
            }
        }

        if (v1.agresiva != null && v2.agresiva != null) {
            agregarFilaComp("Velocidad Prom. (Modo Agresivo)", v1.agresiva.velocidadPromedioKmh, v2.agresiva.velocidadPromedioKmh, true, " km/h");
        }

        if (v1.ahorro != null && v2.ahorro != null) {
            agregarFilaComp("Velocidad Prom. (Modo Ahorro)", v1.ahorro.velocidadPromedioKmh, v2.ahorro.velocidadPromedioKmh, true, " km/h");
        }
    }

    private void agregarFila(String param, String val1, String val2, String ventaja) {
        modeloTabla.addRow(new Object[]{param, val1, val2, ventaja});
    }

    private void agregarFilaComp(String param, double val1, double val2, boolean mayorEsMejor, String unidad) {
        boolean igual = Math.abs(val1 - val2) < 0.001;
        boolean ganaV1 = (mayorEsMejor && val1 > val2) || (!mayorEsMejor && val1 < val2);
        String ventaja = igual ? "Igualdad" : (ganaV1 ? "Gana Vehículo 1" : "Gana Vehículo 2");
        modeloTabla.addRow(new Object[]{param, String.format("%.2f%s", val1, unidad), String.format("%.2f%s", val2, unidad), ventaja});
    }

}
