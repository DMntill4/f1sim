package f1sim.ui;

import f1sim.model.Equipo;
import f1sim.model.Piloto;
import f1sim.model.Vehiculo;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PanelPilotos extends JPanel {

    private Map<Integer, Piloto> pilotos;
    private Map<String, Equipo> equipos;
    private Map<String, Vehiculo> vehiculos;
    private DefaultTableModel modeloTabla;
    private JTable tabla;
    private JTextField campoBusqueda;
    private boolean esAdmin;

    public PanelPilotos(Map<Integer, Piloto> pilotos, Map<String, Equipo> equipos, Map<String, Vehiculo> vehiculos, boolean esAdmin) {
        this.pilotos = pilotos;
        this.equipos = equipos;
        this.vehiculos = vehiculos;
        this.esAdmin = esAdmin;
        setLayout(new BorderLayout(12, 12));
        setBackground(F1Theme.BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        String[] columnas = {"ID", "Nombre", "Equipo", "Vehiculo", "Rol", "Exp. (anos)", "Habilidad (0-100)", "Victorias", "Podios", "Puntos"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);
        F1Theme.styleTable(tabla);

        tabla.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String equipo = value != null ? value.toString() : "";
                Color colorEquipo = F1Theme.getTeamColor(equipo, row);
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 4, 0, 0, colorEquipo),
                        BorderFactory.createEmptyBorder(0, 6, 0, 0)
                ));
                return label;
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tabla);
        F1Theme.styleScrollPane(scrollTabla);
        add(scrollTabla, BorderLayout.CENTER);

        // ---- Panel de busqueda ----
        JPanel panelBusqueda = F1Theme.createCardPanel();
        panelBusqueda.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JLabel lblBuscar = new JLabel("Buscar piloto o equipo:");
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
            JButton botonAgregar = F1Theme.createF1Button("Agregar Piloto", true);
            JButton botonEditar = F1Theme.createF1Button("Editar Piloto", false);
            JButton botonEliminar = F1Theme.createF1Button("Eliminar Piloto", false);

            panelBotones.add(botonAgregar);
            panelBotones.add(botonEditar);
            panelBotones.add(botonEliminar);

            add(panelBotones, BorderLayout.SOUTH);

            botonAgregar.addActionListener(e -> agregarPiloto());
            botonEditar.addActionListener(e -> editarPiloto());
            botonEliminar.addActionListener(e -> eliminarPiloto());
        }

        botonBuscar.addActionListener(e -> buscarPilotos());
        botonMostrarTodos.addActionListener(e -> refrescarTabla(new ArrayList<>(pilotos.values())));

        refrescarTabla(new ArrayList<>(pilotos.values()));
    }

    private void refrescarTabla(List<Piloto> lista) {
        modeloTabla.setRowCount(0);
        for (Piloto p : lista) {
            String vehiculoNombre = p.vehiculoAsignado != null && !p.vehiculoAsignado.trim().isEmpty() ? p.vehiculoAsignado : "Sin Asignar";
            modeloTabla.addRow(new Object[]{p.id, p.nombre, p.equipo, vehiculoNombre, p.rol, p.experiencia, p.nivelHabilidad, p.victorias, p.podios, p.puntos});
        }
    }


    private void buscarPilotos() {
        String texto = campoBusqueda.getText().trim().toLowerCase();
        if (texto.isEmpty()) {
            refrescarTabla(new ArrayList<>(pilotos.values()));
            return;
        }
        List<Piloto> resultado = new ArrayList<>();
        for (Piloto p : pilotos.values()) {
            if (p.nombre.toLowerCase().contains(texto) || p.equipo.toLowerCase().contains(texto)) {
                resultado.add(p);
            }
        }
        refrescarTabla(resultado);
    }

    private int generarNuevoId() {
        int maximo = 0;
        for (Integer id : pilotos.keySet()) {
            if (id > maximo) maximo = id;
        }
        return maximo + 1;
    }

    private void agregarPiloto() {
        if (!esAdmin) return;
        JTextField campoNombre = new JTextField();
        JTextField campoEquipo = new JTextField();
        JComboBox<String> campoRol = new JComboBox<>(new String[]{"Lider", "Escudero"});
        JTextField campoExperiencia = new JTextField("3");
        JTextField campoHabilidad = new JTextField("85");
        JTextField campoVictorias = new JTextField("0");
        JTextField campoPodios = new JTextField("0");
        JTextField campoPuntos = new JTextField("0");

        Object[] mensaje = {
                "Nombre:", campoNombre,
                "Equipo:", campoEquipo,
                "Rol:", campoRol,
                "Experiencia (anos):", campoExperiencia,
                "Nivel Habilidad (0-100):", campoHabilidad,
                "Victorias:", campoVictorias,
                "Podios:", campoPodios,
                "Puntos acumulados:", campoPuntos
        };

        int opcion = JOptionPane.showConfirmDialog(this, mensaje, "Agregar Piloto", JOptionPane.OK_CANCEL_OPTION);
        if (opcion == JOptionPane.OK_OPTION) {
            try {
                String nombre = campoNombre.getText().trim();
                String equipo = campoEquipo.getText().trim();
                String rol = (String) campoRol.getSelectedItem();
                int experiencia = Integer.parseInt(campoExperiencia.getText().trim());
                int habilidad = Math.max(0, Math.min(100, Integer.parseInt(campoHabilidad.getText().trim())));
                int vics = Integer.parseInt(campoVictorias.getText().trim());
                int pods = Integer.parseInt(campoPodios.getText().trim());
                int pts = Integer.parseInt(campoPuntos.getText().trim());

                if (nombre.isEmpty() || equipo.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "El nombre y el equipo no pueden estar vacios.");
                    return;
                }

                int nuevoId = generarNuevoId();
                Piloto nuevo = new Piloto(nuevoId, nombre, equipo, rol, experiencia, habilidad, vics, pods, pts);
                pilotos.put(nuevoId, nuevo);

                // Asignar al vehículo del mismo equipo
                if (vehiculos != null) {
                    for (Vehiculo v : vehiculos.values()) {
                        if (v.equipo != null && v.equipo.equalsIgnoreCase(equipo)) {
                            if (v.pilotos == null) v.pilotos = new ArrayList<>();
                            if (!v.pilotos.contains(nuevoId)) v.pilotos.add(nuevoId);
                        }
                    }
                }

                refrescarTabla(new ArrayList<>(pilotos.values()));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Los campos numericos deben contener valores validos.");
            }
        }
    }

    private void editarPiloto() {
        if (!esAdmin) return;
        int filaSeleccionada = tabla.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un piloto de la tabla para editar.");
            return;
        }
        int id = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        Piloto piloto = pilotos.get(id);
        if (piloto == null) return;

        JTextField campoNombre = new JTextField(piloto.nombre);
        JTextField campoEquipo = new JTextField(piloto.equipo);
        JComboBox<String> campoRol = new JComboBox<>(new String[]{"Lider", "Escudero"});
        campoRol.setSelectedItem(piloto.rol);
        JTextField campoExperiencia = new JTextField(String.valueOf(piloto.experiencia));
        JTextField campoHabilidad = new JTextField(String.valueOf(piloto.nivelHabilidad));
        JTextField campoVictorias = new JTextField(String.valueOf(piloto.victorias));
        JTextField campoPodios = new JTextField(String.valueOf(piloto.podios));
        JTextField campoPuntos = new JTextField(String.valueOf(piloto.puntos));

        // Obtener vehículos registrados para el equipo del piloto
        List<String> opcionesVehiculos = new ArrayList<>();
        opcionesVehiculos.add("Todos los del equipo");
        String vehiculoActual = "Todos los del equipo";
        if (vehiculos != null) {
            for (Vehiculo v : vehiculos.values()) {
                if (v.equipo != null && v.equipo.equalsIgnoreCase(piloto.equipo)) {
                    opcionesVehiculos.add(v.modelo);
                    if (v.pilotos != null && v.pilotos.contains(piloto.id)) {
                        vehiculoActual = v.modelo;
                    }
                }
            }
        }
        JComboBox<String> campoVehiculo = new JComboBox<>(opcionesVehiculos.toArray(new String[0]));
        campoVehiculo.setSelectedItem(vehiculoActual);

        Object[] mensaje = {
                "Nombre:", campoNombre,
                "Equipo:", campoEquipo,
                "Vehiculo del Equipo:", campoVehiculo,
                "Rol:", campoRol,
                "Experiencia (anos):", campoExperiencia,
                "Nivel Habilidad (0-100):", campoHabilidad,
                "Victorias:", campoVictorias,
                "Podios:", campoPodios,
                "Puntos acumulados:", campoPuntos
        };

        int opcion = JOptionPane.showConfirmDialog(this, mensaje, "Editar Piloto", JOptionPane.OK_CANCEL_OPTION);
        if (opcion == JOptionPane.OK_OPTION) {
            try {
                piloto.nombre = campoNombre.getText().trim();
                piloto.equipo = campoEquipo.getText().trim();
                piloto.rol = (String) campoRol.getSelectedItem();
                piloto.experiencia = Integer.parseInt(campoExperiencia.getText().trim());
                piloto.nivelHabilidad = Math.max(0, Math.min(100, Integer.parseInt(campoHabilidad.getText().trim())));
                piloto.victorias = Integer.parseInt(campoVictorias.getText().trim());
                piloto.podios = Integer.parseInt(campoPodios.getText().trim());
                piloto.puntos = Integer.parseInt(campoPuntos.getText().trim());

                // Actualizar la lista de asignación del vehículo según el equipo
                String vehiculoSeleccionado = (String) campoVehiculo.getSelectedItem();
                piloto.vehiculoAsignado = "Todos los del equipo".equals(vehiculoSeleccionado) ? "" : vehiculoSeleccionado;

                if (vehiculos != null) {
                    for (Vehiculo v : vehiculos.values()) {
                        if (v.equipo != null && v.equipo.equalsIgnoreCase(piloto.equipo)) {
                            if (v.pilotos == null) v.pilotos = new ArrayList<>();
                            if ("Todos los del equipo".equals(vehiculoSeleccionado) || v.modelo.equalsIgnoreCase(vehiculoSeleccionado)) {
                                if (!v.pilotos.contains(piloto.id)) v.pilotos.add(piloto.id);
                            } else {
                                v.pilotos.remove(Integer.valueOf(piloto.id));
                            }
                        }
                    }
                }


                refrescarTabla(new ArrayList<>(pilotos.values()));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Los campos numericos deben contener valores validos.");
            }
        }
    }


    private void eliminarPiloto() {
        if (!esAdmin) return;
        int filaSeleccionada = tabla.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un piloto de la tabla para eliminar.");
            return;
        }
        int id = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        Piloto piloto = pilotos.get(id);
        if (piloto == null) return;

        int confirmar = JOptionPane.showConfirmDialog(this, "Eliminar a " + piloto.nombre + "?",
                "Confirmar eliminacion", JOptionPane.YES_NO_OPTION);
        if (confirmar == JOptionPane.YES_OPTION) {
            pilotos.remove(id);

            // Cascada: eliminar id de equipos y vehiculos
            if (equipos != null) {
                for (Equipo eq : equipos.values()) {
                    if (eq.pilotos != null) {
                        eq.pilotos.remove(Integer.valueOf(id));
                    }
                }
            }
            if (vehiculos != null) {
                for (Vehiculo v : vehiculos.values()) {
                    if (v.pilotos != null) {
                        v.pilotos.remove(Integer.valueOf(id));
                    }
                }
            }

            refrescarTabla(new ArrayList<>(pilotos.values()));
        }
    }
}
