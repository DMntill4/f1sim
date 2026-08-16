package f1sim.ui;

import f1sim.model.Circuito;
import f1sim.model.Ganador;
import f1sim.model.Piloto;
import f1sim.model.RecordVuelta;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PanelCircuitos extends JPanel {

    private Map<String, Circuito> circuitos;
    private Map<Integer, Piloto> pilotos;
    private DefaultTableModel modeloTabla;
    private JTable tabla;
    private JTextField campoBusqueda;
    private JTextArea areaDetalle;
    private boolean esAdmin;

    public PanelCircuitos(Map<String, Circuito> circuitos, Map<Integer, Piloto> pilotos, boolean esAdmin) {
        this.circuitos = circuitos;
        this.pilotos = pilotos;
        this.esAdmin = esAdmin;
        setLayout(new BorderLayout(12, 12));
        setBackground(F1Theme.BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        String[] columnas = {"Nombre", "Pais", "Longitud (km)", "Vueltas", "Clima Promedio"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);
        F1Theme.styleTable(tabla);
        JScrollPane scrollTabla = new JScrollPane(tabla);
        F1Theme.styleScrollPane(scrollTabla);

        areaDetalle = new JTextArea(10, 30);
        areaDetalle.setEditable(false);
        areaDetalle.setLineWrap(true);
        areaDetalle.setWrapStyleWord(true);
        areaDetalle.setBackground(F1Theme.CARD_BG);
        areaDetalle.setForeground(F1Theme.TEXT_WHITE);
        areaDetalle.setCaretColor(F1Theme.TEXT_WHITE);
        areaDetalle.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JScrollPane scrollDetalle = new JScrollPane(areaDetalle);
        F1Theme.styleScrollPane(scrollDetalle);

        JSplitPane divisor = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollTabla, scrollDetalle);
        divisor.setResizeWeight(0.55);
        divisor.setBackground(F1Theme.BG_DARK);
        divisor.setBorder(null);
        divisor.setDividerSize(4);
        add(divisor, BorderLayout.CENTER);

        // ---- Panel de busqueda ----
        JPanel panelBusqueda = F1Theme.createCardPanel();
        panelBusqueda.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JLabel lblBuscar = new JLabel("Buscar por nombre o pais:");
        lblBuscar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panelBusqueda.add(lblBuscar);

        campoBusqueda = new JTextField(20);
        campoBusqueda.setPreferredSize(new Dimension(200, 28));
        panelBusqueda.add(campoBusqueda);

        JButton botonBuscar = F1Theme.createF1Button("Buscar", false);
        panelBusqueda.add(botonBuscar);

        JButton botonMostrarTodos = F1Theme.createF1Button("Mostrar Todos", false);
        panelBusqueda.add(botonMostrarTodos);

        add(panelBusqueda, BorderLayout.NORTH);

        // ---- Panel de botones CRUD (solo visible para admin) ----
        if (esAdmin) {
            JPanel panelBotones = F1Theme.createCardPanel();
            panelBotones.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
            JButton botonAgregar = F1Theme.createF1Button("Agregar Circuito", true);
            JButton botonEditar = F1Theme.createF1Button("Editar Circuito", false);
            JButton botonEliminar = F1Theme.createF1Button("Eliminar Circuito", false);

            panelBotones.add(botonAgregar);
            panelBotones.add(botonEditar);
            panelBotones.add(botonEliminar);

            add(panelBotones, BorderLayout.SOUTH);

            botonAgregar.addActionListener(e -> agregarCircuito());
            botonEditar.addActionListener(e -> editarCircuito());
            botonEliminar.addActionListener(e -> eliminarCircuito());
        }

        botonBuscar.addActionListener(e -> buscarCircuitos());
        botonMostrarTodos.addActionListener(e -> refrescarTabla(new ArrayList<>(circuitos.values())));

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarDetalle();
            }
        });

        refrescarTabla(new ArrayList<>(circuitos.values()));
    }

    private void refrescarTabla(List<Circuito> lista) {
        modeloTabla.setRowCount(0);
        for (Circuito c : lista) {
            modeloTabla.addRow(new Object[]{c.nombre, c.pais, c.longitudKm, c.vueltas, c.climaPromedio});
        }
    }

    private void buscarCircuitos() {
        String texto = campoBusqueda.getText().trim().toLowerCase();
        if (texto.isEmpty()) {
            refrescarTabla(new ArrayList<>(circuitos.values()));
            return;
        }
        List<Circuito> resultado = new ArrayList<>();
        for (Circuito c : circuitos.values()) {
            if (c.nombre.toLowerCase().contains(texto) || c.pais.toLowerCase().contains(texto)) {
                resultado.add(c);
            }
        }
        refrescarTabla(resultado);
    }

    private void mostrarDetalle() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) return;

        String nombre = (String) modeloTabla.getValueAt(fila, 0);
        Circuito c = circuitos.get(nombre);
        if (c == null) return;

        StringBuilder texto = new StringBuilder();
        texto.append(c.nombre).append(" - ").append(c.pais).append("\n\n");
        texto.append(c.descripcion).append("\n\n");
        texto.append("Longitud: ").append(c.longitudKm).append(" km\n");
        texto.append("Vueltas: ").append(c.vueltas).append("\n");
        texto.append("Clima promedio: ").append(c.climaPromedio).append("\n");
        texto.append("Factor abrasividad: ").append(c.factorAbrasividad).append("\n\n");
        if (c.recordVuelta != null && c.recordVuelta.piloto != null) {
            texto.append("Record de vuelta: ").append(c.recordVuelta.tiempo)
                    .append(" - ").append(c.recordVuelta.piloto)
                    .append(" (").append(c.recordVuelta.anio).append(")\n\n");
        }
        texto.append("Ganadores por temporada:\n");
        if (c.ganadores == null || c.ganadores.isEmpty()) {
            texto.append("  Sin registros\n");
        } else {
            for (Ganador g : c.ganadores) {
                Piloto p = pilotos.get(g.piloto);
                String nombrePiloto = p != null ? p.nombre : ("Piloto #" + g.piloto);
                texto.append("  * ").append(g.temporada).append(": ").append(nombrePiloto).append("\n");
            }
        }
        areaDetalle.setText(texto.toString());
        areaDetalle.setCaretPosition(0);
    }

    private void agregarCircuito() {
        if (!esAdmin) return;
        JTextField campoNombre = new JTextField();
        JTextField campoPais = new JTextField();
        JTextField campoLongitud = new JTextField();
        JTextField campoVueltas = new JTextField();
        JComboBox<String> campoClima = new JComboBox<>(new String[]{"seco", "lluvioso", "extremo"});
        JTextField campoAbrasividad = new JTextField("1.0");
        JTextField campoDescripcion = new JTextField();
        JTextField campoImagen = new JTextField();

        Object[] mensaje = {
                "Nombre:", campoNombre,
                "Pais:", campoPais,
                "Longitud (km):", campoLongitud,
                "Numero de vueltas:", campoVueltas,
                "Clima promedio:", campoClima,
                "Factor Abrasividad Asfalto (ej. 1.0):", campoAbrasividad,
                "Descripcion:", campoDescripcion,
                "URL del mapa/imagen:", campoImagen
        };

        int opcion = JOptionPane.showConfirmDialog(this, mensaje, "Agregar Circuito", JOptionPane.OK_CANCEL_OPTION);
        if (opcion != JOptionPane.OK_OPTION) return;

        try {
            String nombre = campoNombre.getText().trim();
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre no puede estar vacio.");
                return;
            }

            if (circuitos.containsKey(nombre)) {
                JOptionPane.showMessageDialog(this, "Ya existe un circuito registrado con el nombre: " + nombre);
                return;
            }

            double longitud = Double.parseDouble(campoLongitud.getText().trim());
            int vueltas = Integer.parseInt(campoVueltas.getText().trim());
            double abrasividad = Double.parseDouble(campoAbrasividad.getText().trim());

            if (longitud <= 0 || vueltas <= 0 || abrasividad <= 0) {
                JOptionPane.showMessageDialog(this, "La longitud, vueltas y factor de abrasividad deben ser mayores a 0.");
                return;
            }

            Circuito c = new Circuito();
            c.nombre = nombre;
            c.pais = campoPais.getText().trim();
            c.longitudKm = longitud;
            c.vueltas = vueltas;
            c.climaPromedio = (String) campoClima.getSelectedItem();
            c.factorAbrasividad = abrasividad;
            c.descripcion = campoDescripcion.getText().trim();
            c.imagen = campoImagen.getText().trim();
            c.recordVuelta = new RecordVuelta("", "", 0);
            c.ganadores = new ArrayList<>();

            circuitos.put(nombre, c);
            refrescarTabla(new ArrayList<>(circuitos.values()));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "La longitud, vueltas y abrasividad deben ser valores numericos validos.");
        }
    }

    private void editarCircuito() {
        if (!esAdmin) return;
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un circuito de la tabla para editar.");
            return;
        }
        String nombreOriginal = (String) modeloTabla.getValueAt(fila, 0);
        Circuito c = circuitos.get(nombreOriginal);
        if (c == null) return;

        JTextField campoNombre = new JTextField(c.nombre);
        JTextField campoPais = new JTextField(c.pais);
        JTextField campoLongitud = new JTextField(String.valueOf(c.longitudKm));
        JTextField campoVueltas = new JTextField(String.valueOf(c.vueltas));
        JComboBox<String> campoClima = new JComboBox<>(new String[]{"seco", "lluvioso", "extremo"});
        campoClima.setSelectedItem(c.climaPromedio);
        JTextField campoAbrasividad = new JTextField(String.valueOf(c.factorAbrasividad));
        JTextField campoDescripcion = new JTextField(c.descripcion);
        JTextField campoImagen = new JTextField(c.imagen);

        Object[] mensaje = {
                "Nombre:", campoNombre,
                "Pais:", campoPais,
                "Longitud (km):", campoLongitud,
                "Numero de vueltas:", campoVueltas,
                "Clima promedio:", campoClima,
                "Factor Abrasividad Asfalto:", campoAbrasividad,
                "Descripcion:", campoDescripcion,
                "URL del mapa/imagen:", campoImagen
        };

        int opcion = JOptionPane.showConfirmDialog(this, mensaje, "Editar Circuito", JOptionPane.OK_CANCEL_OPTION);
        if (opcion != JOptionPane.OK_OPTION) return;

        try {
            String nuevoNombre = campoNombre.getText().trim();
            if (nuevoNombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre no puede estar vacio.");
                return;
            }
            if (!nuevoNombre.equalsIgnoreCase(nombreOriginal) && circuitos.containsKey(nuevoNombre)) {
                JOptionPane.showMessageDialog(this, "Ya existe un circuito registrado con el nombre: " + nuevoNombre);
                return;
            }

            double longitud = Double.parseDouble(campoLongitud.getText().trim());
            int vueltas = Integer.parseInt(campoVueltas.getText().trim());
            double abrasividad = Double.parseDouble(campoAbrasividad.getText().trim());

            if (longitud <= 0 || vueltas <= 0 || abrasividad <= 0) {
                JOptionPane.showMessageDialog(this, "La longitud, vueltas y factor de abrasividad deben ser mayores a 0.");
                return;
            }

            if (!nuevoNombre.equals(nombreOriginal)) {
                circuitos.remove(nombreOriginal);
            }
            c.nombre = nuevoNombre;
            c.pais = campoPais.getText().trim();
            c.longitudKm = longitud;
            c.vueltas = vueltas;
            c.climaPromedio = (String) campoClima.getSelectedItem();
            c.factorAbrasividad = abrasividad;
            c.descripcion = campoDescripcion.getText().trim();
            c.imagen = campoImagen.getText().trim();
            circuitos.put(c.nombre, c);
            refrescarTabla(new ArrayList<>(circuitos.values()));
            mostrarDetalle();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "La longitud, vueltas y abrasividad deben ser valores numericos validos.");
        }
    }

    private void eliminarCircuito() {
        if (!esAdmin) return;
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un circuito de la tabla para eliminar.");
            return;
        }
        String nombre = (String) modeloTabla.getValueAt(fila, 0);
        Circuito c = circuitos.get(nombre);
        if (c == null) return;

        int confirmar = JOptionPane.showConfirmDialog(this, "Eliminar el circuito " + nombre + "?",
                "Confirmar eliminacion", JOptionPane.YES_NO_OPTION);
        if (confirmar == JOptionPane.YES_OPTION) {
            circuitos.remove(nombre);
            refrescarTabla(new ArrayList<>(circuitos.values()));
            areaDetalle.setText("");
        }
    }
}
