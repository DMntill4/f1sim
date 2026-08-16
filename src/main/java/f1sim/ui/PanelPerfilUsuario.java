package f1sim.ui;

import f1sim.datos.ExportadorDatos;
import f1sim.datos.GestorDatos;
import f1sim.model.Piloto;
import f1sim.model.ResultadoClasificacion;
import f1sim.model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PanelPerfilUsuario extends JPanel {

    private final Usuario usuario;
    private final Map<Integer, Piloto> pilotos;
    private final DefaultTableModel modeloTabla;
    private final List<ResultadoClasificacion> misResultados = new ArrayList<>();

    private JLabel lblTotalSimulaciones;
    private JLabel lblMejorTiempo;
    private JLabel lblCircuitoFavorito;

    public PanelPerfilUsuario(Usuario usuario, Map<Integer, Piloto> pilotos) {
        this.usuario = usuario;
        this.pilotos = pilotos;

        setLayout(new BorderLayout(14, 14));
        setBackground(F1Theme.BG_DARK);
        setBorder(new EmptyBorder(14, 14, 14, 14));

        add(construirPanelPerfilHeader(), BorderLayout.NORTH);

        // Tabla de resultados del usuario
        String[] columnas = {"Fecha", "Circuito", "Vehiculo", "Clima", "Modo", "Carga Aero", "Presion Neum.", "Tiempo de Vuelta"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tabla = new JTable(modeloTabla);
        F1Theme.styleTable(tabla);

        tabla.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                label.setForeground(F1Theme.COLOR_YELLOW);

                return label;
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tabla);
        F1Theme.styleScrollPane(scrollTabla);

        JPanel panelCentro = F1Theme.createCardPanel();
        panelCentro.setLayout(new BorderLayout(10, 10));

        JLabel tituloTabla = new JLabel(" Historial Exclusivo de Mis Sesiones y Tiempos ");
        tituloTabla.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tituloTabla.setForeground(F1Theme.TEXT_WHITE);
        panelCentro.add(tituloTabla, BorderLayout.NORTH);
        panelCentro.add(scrollTabla, BorderLayout.CENTER);

        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        panelAcciones.setOpaque(false);

        JButton btnRefrescar = F1Theme.createF1Button("Refrescar Historial", false);
        btnRefrescar.addActionListener(e -> cargarMisDatos());
        panelAcciones.add(btnRefrescar);

        JButton btnExportar = F1Theme.createF1Button("Exportar Mis Resultados (CSV)", true);
        btnExportar.addActionListener(e -> exportarMisResultados());
        panelAcciones.add(btnExportar);

        panelCentro.add(panelAcciones, BorderLayout.SOUTH);

        add(panelCentro, BorderLayout.CENTER);

        cargarMisDatos();
    }

    private JPanel construirPanelPerfilHeader() {
        JPanel header = F1Theme.createCardPanel();
        header.setLayout(new BorderLayout(16, 10));
        header.setBorder(new EmptyBorder(12, 16, 12, 16));

        // Panel Izquierdo: Info del Usuario
        JPanel panelInfo = new JPanel(new GridLayout(3, 1, 0, 4));
        panelInfo.setOpaque(false);

        String nombreMostrar = usuario != null && usuario.nombreCompleto != null ? usuario.nombreCompleto : "Usuario F1";
        String username = usuario != null ? usuario.username : "usuario";
        String rol = usuario != null && usuario.rol != null ? usuario.rol.name() : "USUARIO";

        JLabel lblNombre = new JLabel("👤 " + nombreMostrar.toUpperCase());
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblNombre.setForeground(F1Theme.TEXT_WHITE);

        JLabel lblNick = new JLabel("Usuario: @" + username + "  |  Rol: " + rol);
        lblNick.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblNick.setForeground(F1Theme.TEXT_MUTED);

        String escuderia = obtenerEquipoDelUsuario();
        JLabel lblEscuderia = new JLabel("Escuderia Asociada: " + escuderia);
        lblEscuderia.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblEscuderia.setForeground(F1Theme.F1_RED);

        panelInfo.add(lblNombre);
        panelInfo.add(lblNick);
        panelInfo.add(lblEscuderia);

        header.add(panelInfo, BorderLayout.WEST);

        // Panel Derecho: Tarjetas de Métricas Estadísticas
        JPanel panelMetricas = new JPanel(new GridLayout(1, 3, 10, 0));
        panelMetricas.setOpaque(false);

        lblTotalSimulaciones = crearTarjetaMetrica("Total Sesiones", "0", F1Theme.TEXT_WHITE);
        lblMejorTiempo = crearTarjetaMetrica("Mejor Tiempo", "--:--.---", F1Theme.COLOR_YELLOW);

        lblCircuitoFavorito = crearTarjetaMetrica("Circuito Principal", "N/A", F1Theme.COLOR_GREEN);

        panelMetricas.add(lblTotalSimulaciones.getParent());
        panelMetricas.add(lblMejorTiempo.getParent());
        panelMetricas.add(lblCircuitoFavorito.getParent());

        header.add(panelMetricas, BorderLayout.EAST);

        return header;
    }

    private JLabel crearTarjetaMetrica(String titulo, String valorInicial, Color colorValor) {
        JPanel card = new JPanel(new GridLayout(2, 1, 0, 2)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(F1Theme.INPUT_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(F1Theme.BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(8, 12, 8, 12));
        card.setPreferredSize(new Dimension(160, 60));

        JLabel lblTitle = new JLabel(titulo, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTitle.setForeground(F1Theme.TEXT_MUTED);

        JLabel lblVal = new JLabel(valorInicial, SwingConstants.CENTER);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblVal.setForeground(colorValor);

        card.add(lblTitle);
        card.add(lblVal);

        return lblVal;
    }

    private String obtenerEquipoDelUsuario() {
        if (usuario == null) return "Libre";
        String uName = usuario.username.toLowerCase();
        for (Piloto p : pilotos.values()) {
            if (p.nombre != null && p.nombre.toLowerCase().contains(uName)) {
                return p.equipo;
            }
        }
        return "Escuderia Oficial F1";
    }

    public void cargarMisDatos() {
        misResultados.clear();
        modeloTabla.setRowCount(0);

        double mejorTiempo = Double.MAX_VALUE;
        String circuitoMejor = "N/A";

        for (ResultadoClasificacion r : GestorDatos.cargarResultados()) {
            if (esResultadoDeUsuario(r)) {
                misResultados.add(r);
                modeloTabla.addRow(new Object[]{
                        r.fecha, r.circuito, r.vehiculo, r.clima, r.modo,
                        r.cargaAerodinamica, r.presionNeumaticos,
                        FormateadorF1.formatearTiempoVuelta(r.tiempoVueltaSegundos)
                });

                if (r.tiempoVueltaSegundos < mejorTiempo) {
                    mejorTiempo = r.tiempoVueltaSegundos;
                    circuitoMejor = r.circuito;
                }
            }
        }

        lblTotalSimulaciones.setText(String.valueOf(misResultados.size()));
        lblMejorTiempo.setText(mejorTiempo != Double.MAX_VALUE ? FormateadorF1.formatearTiempoVuelta(mejorTiempo) : "--:--.---");
        lblCircuitoFavorito.setText(mejorTiempo != Double.MAX_VALUE ? circuitoMejor : "Sin registros");
    }

    private boolean esResultadoDeUsuario(ResultadoClasificacion r) {
        if (usuario != null && usuario.rol == Usuario.Rol.ADMIN) return true;
        if (r.piloto == null || usuario == null) return false;
        String pilotoLower = r.piloto.toLowerCase();
        String usrName = usuario.username.toLowerCase();
        String nombreComp = usuario.nombreCompleto != null ? usuario.nombreCompleto.toLowerCase() : "";
        return pilotoLower.contains(usrName) || (!nombreComp.isEmpty() && pilotoLower.contains(nombreComp));
    }


    private void exportarMisResultados() {
        if (misResultados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No tienes resultados guardados para exportar.");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("MisResultados_" + (usuario != null ? usuario.username : "F1") + ".csv"));
        int res = chooser.showSaveDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File archivo = chooser.getSelectedFile();
            boolean ok = ExportadorDatos.exportarACSV(archivo.getAbsolutePath(), misResultados);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Resultados personales exportados con exito a:\n" + archivo.getAbsolutePath());
            } else {
                JOptionPane.showMessageDialog(this, "Error al exportar los datos.");
            }
        }
    }
}
