package f1sim.ui;

import f1sim.model.Equipo;
import f1sim.model.Piloto;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PanelEquipos extends JPanel {

    private Map<String, Equipo> equipos;
    private Map<Integer, Piloto> pilotos;
    private DefaultTableModel modeloTabla;
    private JTable tabla;
    private JTextField campoBusqueda;
    private boolean esAdmin;

    public PanelEquipos(Map<String, Equipo> equipos, Map<Integer, Piloto> pilotos, boolean esAdmin) {
        this.equipos = equipos;
        this.pilotos = pilotos;
        this.esAdmin = esAdmin;
        setLayout(new BorderLayout(12, 12));
        setBackground(F1Theme.BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        String[] columnas = {"Nombre", "Pais", "Motor", "Pilotos Asignados"};
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

        JScrollPane scroll = new JScrollPane(tabla);
        F1Theme.styleScrollPane(scroll);
        add(scroll, BorderLayout.CENTER);

        // ---- Panel de busqueda ----
        JPanel panelBusqueda = F1Theme.createCardPanel();
        panelBusqueda.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JLabel lblBuscar = new JLabel("Buscar equipo o pais:");
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
            JButton botonAgregar = F1Theme.createF1Button("Agregar Equipo", true);
            JButton botonEditar = F1Theme.createF1Button("Editar Equipo", false);
            JButton botonEliminar = F1Theme.createF1Button("Eliminar Equipo", false);

            panelBotones.add(botonAgregar);
            panelBotones.add(botonEditar);
            panelBotones.add(botonEliminar);

            add(panelBotones, BorderLayout.SOUTH);

            botonAgregar.addActionListener(e -> agregarEquipo());
            botonEditar.addActionListener(e -> editarEquipo());
            botonEliminar.addActionListener(e -> eliminarEquipo());
        }

        botonBuscar.addActionListener(e -> buscarEquipos());
        botonMostrarTodos.addActionListener(e -> refrescarTabla(new ArrayList<>(equipos.values())));

        refrescarTabla(new ArrayList<>(equipos.values()));
    }

    private void refrescarTabla(List<Equipo> lista) {
        modeloTabla.setRowCount(0);
        for (Equipo eq : lista) {
            modeloTabla.addRow(new Object[]{eq.nombre, eq.pais, eq.motor, nombresPilotos(eq)});
        }
    }

    private String nombresPilotos(Equipo eq) {
        List<String> nombres = new ArrayList<>();
        for (Integer id : eq.pilotos) {
            Piloto p = pilotos.get(id);
            if (p != null) nombres.add(p.nombre);
        }
        return String.join(", ", nombres);
    }

    private void buscarEquipos() {
        String texto = campoBusqueda.getText().trim().toLowerCase();
        if (texto.isEmpty()) {
            refrescarTabla(new ArrayList<>(equipos.values()));
            return;
        }
        List<Equipo> resultado = new ArrayList<>();
        for (Equipo eq : equipos.values()) {
            if (eq.nombre.toLowerCase().contains(texto) || eq.pais.toLowerCase().contains(texto)) {
                resultado.add(eq);
            }
        }
        refrescarTabla(resultado);
    }

    private List<Integer> seleccionarPilotos(List<Integer> seleccionActual) {
        List<Piloto> listaPilotosCompleta = new ArrayList<>(pilotos.values());
        DefaultListModel<Piloto> modeloLista = new DefaultListModel<>();
        for (Piloto p : listaPilotosCompleta) modeloLista.addElement(p);

        JList<Piloto> listaPilotos = new JList<>(modeloLista);
        listaPilotos.setBackground(F1Theme.CARD_BG);
        listaPilotos.setForeground(F1Theme.TEXT_WHITE);
        listaPilotos.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < modeloLista.size(); i++) {
            if (seleccionActual.contains(modeloLista.get(i).id)) indices.add(i);
        }
        int[] indicesArray = new int[indices.size()];
        for (int i = 0; i < indices.size(); i++) indicesArray[i] = indices.get(i);
        listaPilotos.setSelectedIndices(indicesArray);

        JScrollPane scrollPilotos = new JScrollPane(listaPilotos);
        F1Theme.styleScrollPane(scrollPilotos);
        scrollPilotos.setPreferredSize(new Dimension(250, 150));

        int opcion = JOptionPane.showConfirmDialog(this, scrollPilotos, "Selecciona los pilotos del equipo", JOptionPane.OK_CANCEL_OPTION);
        if (opcion == JOptionPane.OK_OPTION) {
            List<Integer> seleccionados = new ArrayList<>();
            for (Piloto p : listaPilotos.getSelectedValuesList()) seleccionados.add(p.id);
            return seleccionados;
        }
        return seleccionActual;
    }

    private void agregarEquipo() {
        if (!esAdmin) return;
        JTextField campoNombre = new JTextField();
        JTextField campoPais = new JTextField();
        JTextField campoMotor = new JTextField();
        JTextField campoImagen = new JTextField();

        Object[] mensaje = {
                "Nombre del equipo:", campoNombre,
                "Pais:", campoPais,
                "Motor:", campoMotor,
                "URL del logo (opcional):", campoImagen
        };

        int opcion = JOptionPane.showConfirmDialog(this, mensaje, "Agregar Equipo", JOptionPane.OK_CANCEL_OPTION);
        if (opcion == JOptionPane.OK_OPTION) {
            String nombre = campoNombre.getText().trim();
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre no puede estar vacio.");
                return;
            }
            if (equipos.containsKey(nombre)) {
                JOptionPane.showMessageDialog(this, "Ya existe un equipo con ese nombre.");
                return;
            }
            Equipo nuevo = new Equipo(nombre, campoPais.getText().trim(), campoMotor.getText().trim(),
                    new ArrayList<>(), campoImagen.getText().trim());
            nuevo.pilotos = seleccionarPilotos(new ArrayList<>());
            equipos.put(nombre, nuevo);
            refrescarTabla(new ArrayList<>(equipos.values()));
        }
    }

    private void editarEquipo() {
        if (!esAdmin) return;
        int filaSeleccionada = tabla.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un equipo de la tabla para editar.");
            return;
        }
        String nombreOriginal = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
        Equipo equipo = equipos.get(nombreOriginal);
        if (equipo == null) return;

        JTextField campoNombre = new JTextField(equipo.nombre);
        JTextField campoPais = new JTextField(equipo.pais);
        JTextField campoMotor = new JTextField(equipo.motor);
        JTextField campoImagen = new JTextField(equipo.imagen);

        Object[] mensaje = {
                "Nombre del equipo:", campoNombre,
                "Pais:", campoPais,
                "Motor:", campoMotor,
                "URL del logo:", campoImagen
        };

        int opcion = JOptionPane.showConfirmDialog(this, mensaje, "Editar Equipo", JOptionPane.OK_CANCEL_OPTION);
        if (opcion == JOptionPane.OK_OPTION) {
            String nuevoNombre = campoNombre.getText().trim();
            if (!nuevoNombre.equals(nombreOriginal)) {
                equipos.remove(nombreOriginal);
            }
            equipo.nombre = nuevoNombre;
            equipo.pais = campoPais.getText().trim();
            equipo.motor = campoMotor.getText().trim();
            equipo.imagen = campoImagen.getText().trim();

            int cambiarPilotos = JOptionPane.showConfirmDialog(this, "Deseas actualizar los pilotos asignados?",
                    "Pilotos", JOptionPane.YES_NO_OPTION);
            if (cambiarPilotos == JOptionPane.YES_OPTION) {
                equipo.pilotos = seleccionarPilotos(equipo.pilotos);
            }
            equipos.put(equipo.nombre, equipo);
            refrescarTabla(new ArrayList<>(equipos.values()));
        }
    }

    private void eliminarEquipo() {
        if (!esAdmin) return;
        int filaSeleccionada = tabla.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un equipo de la tabla para eliminar.");
            return;
        }
        String nombre = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
        Equipo equipo = equipos.get(nombre);
        if (equipo == null) return;

        int confirmar = JOptionPane.showConfirmDialog(this, "Eliminar el equipo " + nombre + "?",
                "Confirmar eliminacion", JOptionPane.YES_NO_OPTION);
        if (confirmar == JOptionPane.YES_OPTION) {
            equipos.remove(nombre);
            refrescarTabla(new ArrayList<>(equipos.values()));
        }
    }
}
