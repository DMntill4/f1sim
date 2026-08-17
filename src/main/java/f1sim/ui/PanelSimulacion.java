package f1sim.ui;

import f1sim.datos.GestorDatos;
import f1sim.model.Circuito;
import f1sim.model.ModoConduccion;
import f1sim.model.Piloto;
import f1sim.model.ResultadoClasificacion;
import f1sim.model.Vehiculo;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class PanelSimulacion extends JPanel {

    private Map<Integer, Piloto> pilotos;
    private Map<String, Vehiculo> vehiculos;
    private Map<String, Circuito> circuitos;

    private JComboBox<Circuito> comboCircuito;
    private JComboBox<String> comboVehiculo;
    private JComboBox<String> comboModo;
    private JComboBox<String> comboClima;
    private JComboBox<String> comboAero;
    private JComboBox<String> comboPresion;
    private JComboBox<String> comboEstrategia;

    private DefaultTableModel modeloResultados;
    private JTable tablaResultados;

    private List<ResultadoClasificacion> historialResultados;

    public PanelSimulacion(Map<Integer, Piloto> pilotos, Map<String, Vehiculo> vehiculos, Map<String, Circuito> circuitos) {
        this.pilotos = pilotos;
        this.vehiculos = vehiculos;
        this.circuitos = circuitos;
        this.historialResultados = GestorDatos.cargarResultados();

        setLayout(new BorderLayout(12, 12));
        setBackground(F1Theme.BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // ---- Panel de configuracion ----
        JPanel panelConfiguracion = F1Theme.createCardPanel();
        panelConfiguracion.setLayout(new GridLayout(3, 1, 0, 6));
        panelConfiguracion.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        // Fila 1: Selección Principal (Circuito, Vehículo, Modo, Clima)
        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        fila1.setOpaque(false);

        JLabel lblCircuito = new JLabel("Circuito:");
        lblCircuito.setFont(new Font("Segoe UI", Font.BOLD, 12));
        fila1.add(lblCircuito);

        comboCircuito = new JComboBox<>(circuitos.values().toArray(new Circuito[0]));
        comboCircuito.setPreferredSize(new Dimension(140, 28));
        fila1.add(comboCircuito);

        fila1.add(new JLabel("Vehiculo:"));
        List<String> opcionesVehiculo = new ArrayList<>();
        opcionesVehiculo.add("Auto del Equipo");
        for (Vehiculo v : vehiculos.values()) {
            String clave = v.equipo + " - " + v.modelo;
            opcionesVehiculo.add(clave);
        }
        comboVehiculo = new JComboBox<>(opcionesVehiculo.toArray(new String[0]));
        comboVehiculo.setPreferredSize(new Dimension(140, 28));
        fila1.add(comboVehiculo);

        fila1.add(new JLabel("Modo:"));
        comboModo = new JComboBox<>(new String[]{"normal", "agresiva", "ahorro"});
        comboModo.setPreferredSize(new Dimension(90, 28));
        fila1.add(comboModo);

        fila1.add(new JLabel("Clima:"));
        comboClima = new JComboBox<>(new String[]{"aleatorio", "seco", "lluvioso", "extremo"});
        comboClima.setPreferredSize(new Dimension(95, 28));
        fila1.add(comboClima);

        // Fila 2: Ajustes Técnicos del Vehículo e Impacto de Abrasividad
        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        fila2.setOpaque(false);

        fila2.add(new JLabel("Carga Aero:"));
        comboAero = new JComboBox<>(new String[]{"baja", "media", "alta"});
        comboAero.setSelectedItem("media");
        comboAero.setPreferredSize(new Dimension(75, 28));
        fila2.add(comboAero);

        fila2.add(new JLabel("Presion Neum.:"));
        comboPresion = new JComboBox<>(new String[]{"baja", "estandar", "alta"});
        comboPresion.setSelectedItem("estandar");
        comboPresion.setPreferredSize(new Dimension(85, 28));
        fila2.add(comboPresion);

        fila2.add(new JLabel("Estrategia Comb.:"));
        comboEstrategia = new JComboBox<>(new String[]{"agresiva", "balanceada", "ahorro"});
        comboEstrategia.setSelectedItem("balanceada");
        comboEstrategia.setPreferredSize(new Dimension(95, 28));
        fila2.add(comboEstrategia);

        JLabel lblImpactoCircuito = new JLabel(" Impacto Abrasividad: Normal");
        lblImpactoCircuito.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblImpactoCircuito.setForeground(F1Theme.TEXT_MUTED);
        fila2.add(lblImpactoCircuito);

        Runnable actualizarImpactoAbrasivo = () -> {
            Circuito c = (Circuito) comboCircuito.getSelectedItem();
            if (c != null) {
                double ab = c.factorAbrasividad > 0 ? c.factorAbrasividad : 1.0;
                int porc = (int) Math.round((ab - 1.0) * 100);
                String signo = porc >= 0 ? "+" : "";
                lblImpactoCircuito.setText(" Desgaste y Consumo por Pista: " + signo + porc + "% (" + c.nombre + ")");
            }
        };
        comboCircuito.addActionListener(e -> actualizarImpactoAbrasivo.run());
        actualizarImpactoAbrasivo.run();

        // Fila 3: Botones de Acción
        JPanel fila3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        fila3.setOpaque(false);

        JButton botonSimular = F1Theme.createF1Button("Simular Clasificacion", true);
        fila3.add(botonSimular);

        JButton botonGuardar = F1Theme.createF1Button("Guardar Resultados", false);
        fila3.add(botonGuardar);

        JButton botonHistorial = F1Theme.createF1Button("Historial de Tiempos", false);
        botonHistorial.addActionListener(e -> abrirHistorial());
        fila3.add(botonHistorial);

        panelConfiguracion.add(fila1);
        panelConfiguracion.add(fila2);
        panelConfiguracion.add(fila3);

        add(panelConfiguracion, BorderLayout.NORTH);



        // ---- Tabla de resultados ----
        String[] columnas = {"Posicion", "Piloto", "Equipo", "Vehiculo", "Clima", "Tiempo de Vuelta"};
        modeloResultados = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tablaResultados = new JTable(modeloResultados);
        F1Theme.styleTable(tablaResultados);

        tablaResultados.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                if (row == 0) {
                    label.setForeground(F1Theme.COLOR_PURPLE);
                } else {
                    label.setForeground(F1Theme.TEXT_WHITE);
                }
                return label;
            }
        });

        JScrollPane scroll = new JScrollPane(tablaResultados);
        F1Theme.styleScrollPane(scroll);
        add(scroll, BorderLayout.CENTER);

        botonSimular.addActionListener(e -> simularClasificacion());
        botonGuardar.addActionListener(e -> guardarResultados());
    }

    private void abrirHistorial() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        DialogoHistorialResultados dialogo = new DialogoHistorialResultados(parentFrame);
        dialogo.setVisible(true);
    }

    private double calcularTiempoVuelta(Piloto piloto, Vehiculo vehiculo, Circuito circuito, String modoNombre, String clima, String aero, String presion, String estrategia) {
        ModoConduccion modo = vehiculo.obtenerModo(modoNombre);
        double velocidad = modo.velocidadPromedioKmh > 0 ? modo.velocidadPromedioKmh : 250.0;

        double consumoClima = modo.consumoCombustible != null ? modo.consumoCombustible.obtenerPorClima(clima) : 1.0;
        double desgasteClima = modo.desgasteNeumaticos != null ? modo.desgasteNeumaticos.obtenerPorClima(clima) : 1.0;
        double factorAbrasivo = circuito.factorAbrasividad > 0 ? circuito.factorAbrasividad : 1.0;

        double factorRendimiento = 1.0 + ((consumoClima * 0.002) + (desgasteClima * 0.003)) * factorAbrasivo;
        double tiempoBaseSegundos = (circuito.longitudKm / velocidad) * 3600.0 * factorRendimiento;

        double factorClima = 1.0;
        if ("lluvioso".equalsIgnoreCase(clima)) {
            factorClima = 1.08;
        } else if ("extremo".equalsIgnoreCase(clima)) {
            factorClima = 1.18;
        }

        double factorAero = 1.0;
        if ("alta".equalsIgnoreCase(aero)) {
            factorAero = "lluvioso".equalsIgnoreCase(clima) ? 0.985 : 0.993;
        } else if ("baja".equalsIgnoreCase(aero)) {
            factorAero = 0.990;
        }

        double factorPresion = 1.0;
        if ("alta".equalsIgnoreCase(presion)) {
            factorPresion = 0.995;
        } else if ("baja".equalsIgnoreCase(presion)) {
            factorPresion = "lluvioso".equalsIgnoreCase(clima) ? 0.988 : 1.002;
        }

        double factorEstrategia = 1.0;
        if ("agresiva".equalsIgnoreCase(estrategia)) {
            factorEstrategia = 0.992;
        } else if ("ahorro".equalsIgnoreCase(estrategia)) {
            factorEstrategia = 1.008;
        }

        int exp = Math.min(piloto.experiencia, 15);
        int hab = piloto.nivelHabilidad > 0 ? piloto.nivelHabilidad : 85;
        double factorPiloto = 1.0 - (exp * 0.002) - ((hab - 50) * 0.0004);
        double factorVariacion = 1.0 + ((Math.random() - 0.5) * 0.03);

        return tiempoBaseSegundos * factorClima * factorAero * factorPresion * factorEstrategia * factorPiloto * factorVariacion;
    }


    private String formatearTiempo(double segundosTotales) {
        int minutos = (int) (segundosTotales / 60);
        double segundos = segundosTotales - (minutos * 60);
        return String.format("%d:%06.3f", minutos, segundos);
    }

    private Vehiculo buscarVehiculoDeEquipo(String nombreEquipo) {
        for (Vehiculo v : vehiculos.values()) {
            if (v.equipo.equalsIgnoreCase(nombreEquipo)) return v;
        }
        return null;
    }

    private Vehiculo resolverVehiculoParaPiloto(Piloto piloto, String vehiculoSeleccionado) {
        if (vehiculoSeleccionado != null && !"Auto del Equipo".equals(vehiculoSeleccionado) && vehiculos.containsKey(vehiculoSeleccionado)) {
            return vehiculos.get(vehiculoSeleccionado);
        }
        if (piloto.vehiculoAsignado != null && !piloto.vehiculoAsignado.trim().isEmpty()) {
            String clave = piloto.equipo + " - " + piloto.vehiculoAsignado;
            if (vehiculos.containsKey(clave)) return vehiculos.get(clave);
        }
        return buscarVehiculoDeEquipo(piloto.equipo);
    }


    private void simularClasificacion() {
        Circuito circuito = (Circuito) comboCircuito.getSelectedItem();
        String modo = (String) comboModo.getSelectedItem();
        String climaSeleccionado = (String) comboClima.getSelectedItem();
        String aero = (String) comboAero.getSelectedItem();
        String presion = (String) comboPresion.getSelectedItem();
        String estrategia = (String) comboEstrategia.getSelectedItem();

        if (circuito == null) {
            JOptionPane.showMessageDialog(this, "Primero agrega al menos un circuito.");
            return;
        }
        if (pilotos.isEmpty() || vehiculos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debes tener al menos un piloto y un vehiculo registrado.");
            return;
        }

        String clima = climaSeleccionado;
        if (clima.equalsIgnoreCase("aleatorio")) {
            String[] opciones = {"seco", "lluvioso", "extremo"};
            clima = opciones[(int) (Math.random() * opciones.length)];
        }

        List<Object[]> filas = new ArrayList<>();
        List<ResultadoClasificacion> nuevosResultados = new ArrayList<>();
        String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        String vehiculoSeleccionado = (String) comboVehiculo.getSelectedItem();

        for (Piloto piloto : pilotos.values()) {
            Vehiculo vehiculo = resolverVehiculoParaPiloto(piloto, vehiculoSeleccionado);
            if (vehiculo == null) continue;

            double tiempo = calcularTiempoVuelta(piloto, vehiculo, circuito, modo, clima, aero, presion, estrategia);
            filas.add(new Object[]{piloto.nombre, piloto.equipo, vehiculo.modelo, clima, tiempo});
            nuevosResultados.add(new ResultadoClasificacion(fecha, piloto.nombre, vehiculo.modelo,
                    circuito.nombre, clima, modo, tiempo, 1, aero, presion, estrategia));
        }

        if (filas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ningun piloto tiene un vehiculo asignado a su equipo.");
            return;
        }

        filas.sort((o1, o2) -> Double.compare((Double) o1[4], (Double) o2[4]));


        modeloResultados.setRowCount(0);
        int posicion = 1;
        for (Object[] fila : filas) {
            double tiempoSegundos = (Double) fila[4];
            modeloResultados.addRow(new Object[]{
                    (posicion == 1 ? "P1 (POLE)" : "P" + posicion),
                    fila[0], fila[1], fila[2], fila[3], FormateadorF1.formatearTiempoVuelta(tiempoSegundos)
            });
            posicion++;
        }

        historialResultados.addAll(nuevosResultados);
    }


    private void guardarResultados() {
        if (modeloResultados.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Primero ejecuta una simulacion.");
            return;
        }
        GestorDatos.guardarResultados(historialResultados);
        JOptionPane.showMessageDialog(this, "Resultados guardados en data/resultados.json");
    }
}
