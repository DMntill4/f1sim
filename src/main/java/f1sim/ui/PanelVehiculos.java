package f1sim.ui;

import f1sim.model.DatosCondicion;
import f1sim.model.ModoConduccion;
import f1sim.model.Vehiculo;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PanelVehiculos extends JPanel {

    private Map<String, Vehiculo> vehiculos;
    private DefaultTableModel modeloTabla;
    private JTable tabla;
    private JTextField campoBusqueda;
    private boolean esAdmin;

    private JLabel etiquetaImagen;
    private JTextArea areaEspecificaciones;

    public PanelVehiculos(Map<String, Vehiculo> vehiculos, boolean esAdmin) {
        this.vehiculos = vehiculos;
        this.esAdmin = esAdmin;
        setLayout(new BorderLayout(12, 12));
        setBackground(F1Theme.BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // ---- Busqueda y Comparacion ----
        JPanel panelBusqueda = F1Theme.createCardPanel();
        panelBusqueda.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JLabel lblBuscar = new JLabel("Buscar por equipo o modelo:");
        lblBuscar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panelBusqueda.add(lblBuscar);

        campoBusqueda = new JTextField(18);
        campoBusqueda.setPreferredSize(new Dimension(180, 28));
        panelBusqueda.add(campoBusqueda);

        JButton botonBuscar = F1Theme.createF1Button("Buscar", false);
        panelBusqueda.add(botonBuscar);

        JButton botonMostrarTodos = F1Theme.createF1Button("Mostrar Todos", false);
        panelBusqueda.add(botonMostrarTodos);

        JButton botonComparar = F1Theme.createF1Button("Comparar Vehiculos", true);
        botonComparar.addActionListener(e -> abrirComparador());
        panelBusqueda.add(botonComparar);

        add(panelBusqueda, BorderLayout.NORTH);

        // ---- Tabla (izquierda) ----
        String[] columnas = {"Equipo", "Modelo", "Motor", "Vel. Maxima (km/h)", "Aceleracion 0-100 (s)"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);
        F1Theme.styleTable(tabla);

        tabla.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String eqNombre = value != null ? value.toString() : "";
                Color colorEquipo = F1Theme.getTeamColor(eqNombre, row);
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 4, 0, 0, colorEquipo),
                        BorderFactory.createEmptyBorder(0, 6, 0, 0)
                ));
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                return label;
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tabla);
        F1Theme.styleScrollPane(scrollTabla);
        scrollTabla.setPreferredSize(new Dimension(550, 400));

        // ---- Panel visual (derecha) ----
        JPanel panelVisual = F1Theme.createCardPanel();
        panelVisual.setLayout(new BorderLayout(5, 5));

        etiquetaImagen = new JLabel("Selecciona un vehiculo de la lista", SwingConstants.CENTER);
        etiquetaImagen.setPreferredSize(new Dimension(320, 190));
        etiquetaImagen.setOpaque(true);
        etiquetaImagen.setBackground(F1Theme.INPUT_BG);
        etiquetaImagen.setForeground(F1Theme.TEXT_MUTED);
        panelVisual.add(etiquetaImagen, BorderLayout.NORTH);

        areaEspecificaciones = new JTextArea(14, 28);
        areaEspecificaciones.setEditable(false);
        areaEspecificaciones.setLineWrap(true);
        areaEspecificaciones.setWrapStyleWord(true);
        areaEspecificaciones.setBackground(F1Theme.CARD_BG);
        areaEspecificaciones.setForeground(F1Theme.TEXT_WHITE);
        areaEspecificaciones.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JScrollPane scrollArea = new JScrollPane(areaEspecificaciones);
        F1Theme.styleScrollPane(scrollArea);
        panelVisual.add(scrollArea, BorderLayout.CENTER);

        JSplitPane divisor = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollTabla, panelVisual);
        divisor.setResizeWeight(0.6);
        divisor.setBackground(F1Theme.BG_DARK);
        divisor.setBorder(null);
        divisor.setDividerSize(4);
        add(divisor, BorderLayout.CENTER);

        // ---- Botones CRUD (solo visible para admin) ----
        if (esAdmin) {
            JPanel panelBotones = F1Theme.createCardPanel();
            panelBotones.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
            JButton botonAgregar = F1Theme.createF1Button("Agregar Vehiculo", true);
            JButton botonEditar = F1Theme.createF1Button("Editar Vehiculo", false);
            JButton botonEliminar = F1Theme.createF1Button("Eliminar Vehiculo", false);

            panelBotones.add(botonAgregar);
            panelBotones.add(botonEditar);
            panelBotones.add(botonEliminar);

            add(panelBotones, BorderLayout.SOUTH);

            botonAgregar.addActionListener(e -> agregarVehiculo());
            botonEditar.addActionListener(e -> editarVehiculo());
            botonEliminar.addActionListener(e -> eliminarVehiculo());
        }

        botonBuscar.addActionListener(e -> buscarVehiculos());
        botonMostrarTodos.addActionListener(e -> refrescarTabla(new ArrayList<>(vehiculos.values())));

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarVehiculoSeleccionado();
            }
        });

        refrescarTabla(new ArrayList<>(vehiculos.values()));
    }

    private void abrirComparador() {
        if (vehiculos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay vehiculos registrados para comparar.");
            return;
        }
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        DialogoCompararVehiculos dialogo = new DialogoCompararVehiculos(parentFrame, new ArrayList<>(vehiculos.values()));
        dialogo.setVisible(true);
    }

    private void refrescarTabla(List<Vehiculo> lista) {
        modeloTabla.setRowCount(0);
        for (Vehiculo v : lista) {
            modeloTabla.addRow(new Object[]{v.equipo, v.modelo, v.motor, v.velocidadMaximaKmh, v.aceleracion});
        }
    }

    private void buscarVehiculos() {
        String texto = campoBusqueda.getText().trim().toLowerCase();
        if (texto.isEmpty()) {
            refrescarTabla(new ArrayList<>(vehiculos.values()));
            return;
        }
        List<Vehiculo> resultado = new ArrayList<>();
        for (Vehiculo v : vehiculos.values()) {
            if (v.equipo.toLowerCase().contains(texto) || v.modelo.toLowerCase().contains(texto)) {
                resultado.add(v);
            }
        }
        refrescarTabla(resultado);
    }

    private void mostrarVehiculoSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) return;

        String equipo = (String) modeloTabla.getValueAt(fila, 0);
        String modelo = (String) modeloTabla.getValueAt(fila, 1);
        String clave = equipo + " - " + modelo;
        Vehiculo v = vehiculos.get(clave);
        if (v == null) return;

        cargarImagen(v.imagen);

        StringBuilder texto = new StringBuilder();
        texto.append("Equipo: ").append(v.equipo).append("\n");
        texto.append("Modelo: ").append(v.modelo).append("\n");
        texto.append("Motor: ").append(v.motor).append("\n");
        texto.append("Vel. Maxima: ").append(v.velocidadMaximaKmh).append(" km/h\n");
        texto.append("Aceleracion 0-100: ").append(v.aceleracion).append(" s\n");
        texto.append("Carga Aerodinamica: ").append(v.cargaAerodinamica.toUpperCase()).append("\n");
        texto.append("Presion Neumaticos: ").append(v.presionNeumaticos.toUpperCase()).append("\n\n");

        texto.append(resumenModo("Conduccion Normal", v.normal));
        texto.append(resumenModo("Conduccion Agresiva", v.agresiva));
        texto.append(resumenModo("Ahorro de Combustible", v.ahorro));

        areaEspecificaciones.setText(texto.toString());
        areaEspecificaciones.setCaretPosition(0);
    }

    private String resumenModo(String titulo, ModoConduccion modo) {
        if (modo == null) return "";
        StringBuilder sb = new StringBuilder();
        sb.append("-- ").append(titulo).append(" --\n");
        sb.append("   Vel. promedio: ").append(modo.velocidadPromedioKmh).append(" km/h\n");
        if (modo.consumoCombustible != null) {
            sb.append("   Consumo (seco/lluvia/ext): ").append(modo.consumoCombustible.seco)
                    .append(" / ").append(modo.consumoCombustible.lluvioso)
                    .append(" / ").append(modo.consumoCombustible.extremo).append("\n");
        }
        if (modo.desgasteNeumaticos != null) {
            sb.append("   Desgaste (seco/lluvia/ext): ").append(modo.desgasteNeumaticos.seco)
                    .append(" / ").append(modo.desgasteNeumaticos.lluvioso)
                    .append(" / ").append(modo.desgasteNeumaticos.extremo).append("\n");
        }
        sb.append("\n");
        return sb.toString();
    }

    private void cargarImagen(String urlImagen) {
        if (urlImagen == null || urlImagen.trim().isEmpty()) {
            etiquetaImagen.setIcon(null);
            etiquetaImagen.setText("Sin imagen disponible");
            return;
        }
        try {
            URL url = java.net.URI.create(urlImagen).toURL();
            ImageIcon iconoOriginal = new ImageIcon(url);
            Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(300, 180, Image.SCALE_SMOOTH);
            etiquetaImagen.setIcon(new ImageIcon(imagenEscalada));
            etiquetaImagen.setText("");
        } catch (Exception ex) {
            etiquetaImagen.setIcon(null);
            etiquetaImagen.setText("No se pudo cargar la imagen");
        }
    }

    private ModoConduccion pedirModoConduccion(String titulo, ModoConduccion base) {
        JTextField campoVelocidad = new JTextField(String.valueOf(base != null ? base.velocidadPromedioKmh : 250));

        JTextField consumoSeco = new JTextField(String.valueOf(base != null && base.consumoCombustible != null ? base.consumoCombustible.seco : 1.0));
        JTextField consumoLluvia = new JTextField(String.valueOf(base != null && base.consumoCombustible != null ? base.consumoCombustible.lluvioso : 1.1));
        JTextField consumoExtremo = new JTextField(String.valueOf(base != null && base.consumoCombustible != null ? base.consumoCombustible.extremo : 1.2));

        JTextField desgasteSeco = new JTextField(String.valueOf(base != null && base.desgasteNeumaticos != null ? base.desgasteNeumaticos.seco : 1.0));
        JTextField desgasteLluvia = new JTextField(String.valueOf(base != null && base.desgasteNeumaticos != null ? base.desgasteNeumaticos.lluvioso : 1.2));
        JTextField desgasteExtremo = new JTextField(String.valueOf(base != null && base.desgasteNeumaticos != null ? base.desgasteNeumaticos.extremo : 1.5));

        Object[] mensaje = {
                "Velocidad promedio (km/h):", campoVelocidad,
                "Consumo combustible - seco:", consumoSeco,
                "Consumo combustible - lluvioso:", consumoLluvia,
                "Consumo combustible - extremo:", consumoExtremo,
                "Desgaste neumaticos - seco:", desgasteSeco,
                "Desgaste neumaticos - lluvioso:", desgasteLluvia,
                "Desgaste neumaticos - extremo:", desgasteExtremo
        };

        int opcion = JOptionPane.showConfirmDialog(this, mensaje, titulo, JOptionPane.OK_CANCEL_OPTION);
        if (opcion != JOptionPane.OK_OPTION) return base;

        try {
            ModoConduccion nuevo = new ModoConduccion();
            nuevo.velocidadPromedioKmh = Double.parseDouble(campoVelocidad.getText().trim());
            nuevo.consumoCombustible = new DatosCondicion(
                    Double.parseDouble(consumoSeco.getText().trim()),
                    Double.parseDouble(consumoLluvia.getText().trim()),
                    Double.parseDouble(consumoExtremo.getText().trim()));
            nuevo.desgasteNeumaticos = new DatosCondicion(
                    Double.parseDouble(desgasteSeco.getText().trim()),
                    Double.parseDouble(desgasteLluvia.getText().trim()),
                    Double.parseDouble(desgasteExtremo.getText().trim()));
            return nuevo;
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Todos los valores deben ser numeros.");
            return base;
        }
    }

    private void agregarVehiculo() {
        if (!esAdmin) return;
        JTextField campoEquipo = new JTextField();
        JTextField campoModelo = new JTextField();
        JTextField campoMotor = new JTextField();
        JTextField campoVelMax = new JTextField();
        JTextField campoAceleracion = new JTextField();
        JComboBox<String> campoAero = new JComboBox<>(new String[]{"baja", "media", "alta"});
        campoAero.setSelectedItem("media");
        JComboBox<String> campoPresion = new JComboBox<>(new String[]{"baja", "estandar", "alta"});
        campoPresion.setSelectedItem("estandar");
        JTextField campoImagen = new JTextField();

        Object[] mensaje = {
                "Equipo:", campoEquipo,
                "Modelo:", campoModelo,
                "Motor:", campoMotor,
                "Velocidad maxima (km/h):", campoVelMax,
                "Aceleracion 0-100 km/h (s):", campoAceleracion,
                "Carga Aerodinamica:", campoAero,
                "Presion de Neumaticos:", campoPresion,
                "URL de la imagen del auto:", campoImagen
        };

        int opcion = JOptionPane.showConfirmDialog(this, mensaje, "Agregar Vehiculo", JOptionPane.OK_CANCEL_OPTION);
        if (opcion != JOptionPane.OK_OPTION) return;

        try {
            Vehiculo v = new Vehiculo();
            v.equipo = campoEquipo.getText().trim();
            v.modelo = campoModelo.getText().trim();
            v.motor = campoMotor.getText().trim();
            v.velocidadMaximaKmh = Double.parseDouble(campoVelMax.getText().trim());
            v.aceleracion = Double.parseDouble(campoAceleracion.getText().trim());
            v.cargaAerodinamica = (String) campoAero.getSelectedItem();
            v.presionNeumaticos = (String) campoPresion.getSelectedItem();
            v.imagen = campoImagen.getText().trim();

            if (v.equipo.isEmpty() || v.modelo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El equipo y el modelo no pueden estar vacios.");
                return;
            }

            v.normal = pedirModoConduccion("Rendimiento - Conduccion Normal", new ModoConduccion());
            v.agresiva = pedirModoConduccion("Rendimiento - Conduccion Agresiva", new ModoConduccion());
            v.ahorro = pedirModoConduccion("Rendimiento - Ahorro de Combustible", new ModoConduccion());

            String clave = v.equipo + " - " + v.modelo;
            vehiculos.put(clave, v);
            refrescarTabla(new ArrayList<>(vehiculos.values()));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "La velocidad y la aceleracion deben ser numeros.");
        }
    }

    private void editarVehiculo() {
        if (!esAdmin) return;
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un vehiculo de la tabla para editar.");
            return;
        }
        String equipo = (String) modeloTabla.getValueAt(fila, 0);
        String modelo = (String) modeloTabla.getValueAt(fila, 1);
        String claveOriginal = equipo + " - " + modelo;
        Vehiculo v = vehiculos.get(claveOriginal);
        if (v == null) return;

        JTextField campoEquipo = new JTextField(v.equipo);
        JTextField campoModelo = new JTextField(v.modelo);
        JTextField campoMotor = new JTextField(v.motor);
        JTextField campoVelMax = new JTextField(String.valueOf(v.velocidadMaximaKmh));
        JTextField campoAceleracion = new JTextField(String.valueOf(v.aceleracion));
        JComboBox<String> campoAero = new JComboBox<>(new String[]{"baja", "media", "alta"});
        campoAero.setSelectedItem(v.cargaAerodinamica != null ? v.cargaAerodinamica : "media");
        JComboBox<String> campoPresion = new JComboBox<>(new String[]{"baja", "estandar", "alta"});
        campoPresion.setSelectedItem(v.presionNeumaticos != null ? v.presionNeumaticos : "estandar");
        JTextField campoImagen = new JTextField(v.imagen);

        Object[] mensaje = {
                "Equipo:", campoEquipo,
                "Modelo:", campoModelo,
                "Motor:", campoMotor,
                "Velocidad maxima (km/h):", campoVelMax,
                "Aceleracion 0-100 km/h (s):", campoAceleracion,
                "Carga Aerodinamica:", campoAero,
                "Presion de Neumaticos:", campoPresion,
                "URL de la imagen del auto:", campoImagen
        };

        int opcion = JOptionPane.showConfirmDialog(this, mensaje, "Editar Vehiculo", JOptionPane.OK_CANCEL_OPTION);
        if (opcion != JOptionPane.OK_OPTION) return;

        try {
            v.equipo = campoEquipo.getText().trim();
            v.modelo = campoModelo.getText().trim();
            v.motor = campoMotor.getText().trim();
            v.velocidadMaximaKmh = Double.parseDouble(campoVelMax.getText().trim());
            v.aceleracion = Double.parseDouble(campoAceleracion.getText().trim());
            v.cargaAerodinamica = (String) campoAero.getSelectedItem();
            v.presionNeumaticos = (String) campoPresion.getSelectedItem();
            v.imagen = campoImagen.getText().trim();

            int actualizarRendimiento = JOptionPane.showConfirmDialog(this,
                    "Deseas actualizar los datos de rendimiento (consumo/desgaste)?", "Rendimiento",
                    JOptionPane.YES_NO_OPTION);
            if (actualizarRendimiento == JOptionPane.YES_OPTION) {
                v.normal = pedirModoConduccion("Rendimiento - Conduccion Normal", v.normal);
                v.agresiva = pedirModoConduccion("Rendimiento - Conduccion Agresiva", v.agresiva);
                v.ahorro = pedirModoConduccion("Rendimiento - Ahorro de Combustible", v.ahorro);
            }

            String nuevaClave = v.equipo + " - " + v.modelo;
            if (!nuevaClave.equals(claveOriginal)) {
                vehiculos.remove(claveOriginal);
            }
            vehiculos.put(nuevaClave, v);
            refrescarTabla(new ArrayList<>(vehiculos.values()));
            mostrarVehiculoSeleccionado();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "La velocidad y la aceleracion deben ser numeros.");
        }
    }

    private void eliminarVehiculo() {
        if (!esAdmin) return;
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un vehiculo de la tabla para eliminar.");
            return;
        }
        String equipo = (String) modeloTabla.getValueAt(fila, 0);
        String modelo = (String) modeloTabla.getValueAt(fila, 1);
        String clave = equipo + " - " + modelo;
        Vehiculo v = vehiculos.get(clave);
        if (v == null) return;

        int confirmar = JOptionPane.showConfirmDialog(this, "Eliminar el vehiculo " + equipo + " " + modelo + "?",
                "Confirmar eliminacion", JOptionPane.YES_NO_OPTION);
        if (confirmar == JOptionPane.YES_OPTION) {
            vehiculos.remove(clave);
            refrescarTabla(new ArrayList<>(vehiculos.values()));
            etiquetaImagen.setIcon(null);
            etiquetaImagen.setText("Selecciona un vehiculo de la lista");
            areaEspecificaciones.setText("");
        }
    }
}
