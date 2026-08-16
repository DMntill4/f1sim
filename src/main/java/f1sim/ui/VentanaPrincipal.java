package f1sim.ui;

import f1sim.datos.GestorDatos;
import f1sim.model.Circuito;
import f1sim.model.Equipo;
import f1sim.model.Piloto;
import f1sim.model.Usuario;
import f1sim.model.Vehiculo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class VentanaPrincipal extends JFrame {

    private Map<Integer, Piloto> pilotos;
    private Map<String, Equipo> equipos;
    private Map<String, Circuito> circuitos;
    private Map<String, Vehiculo> vehiculos;
    private Usuario usuarioActivo;

    public VentanaPrincipal() {
        this(new Usuario(1, "admin", "admin123", Usuario.Rol.ADMIN, "Administrador F1"));
    }

    public VentanaPrincipal(Usuario usuarioActivo) {
        super("F1 World Championship Simulator");
        this.usuarioActivo = usuarioActivo;

        pilotos = GestorDatos.cargarPilotosMap();
        equipos = GestorDatos.cargarEquiposMap();
        circuitos = GestorDatos.cargarCircuitosMap();
        vehiculos = GestorDatos.cargarVehiculosMap();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1240, 780);
        setLocationRelativeTo(null);

        getContentPane().setBackground(F1Theme.BG_DARK);
        setLayout(new BorderLayout());

        add(construirBannerHeader(), BorderLayout.NORTH);

        boolean esAdmin = usuarioActivo != null && usuarioActivo.rol == Usuario.Rol.ADMIN;

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.setFont(new Font("Segoe UI", Font.BOLD, 13));
        pestanas.setBackground(F1Theme.BG_DARK);
        pestanas.setForeground(F1Theme.TEXT_MUTED);

        PanelPilotos panelPilotos = new PanelPilotos(pilotos, equipos, vehiculos, esAdmin);
        PanelEquipos panelEquipos = new PanelEquipos(equipos, pilotos, esAdmin);
        PanelVehiculos panelVehiculos = new PanelVehiculos(vehiculos, esAdmin);
        PanelCircuitos panelCircuitos = new PanelCircuitos(circuitos, pilotos, esAdmin);
        PanelSimulacion panelSimulacion = new PanelSimulacion(pilotos, vehiculos, circuitos);
        PanelCarrera panelCarrera = new PanelCarrera(pilotos, vehiculos, circuitos);
        PanelPerfilUsuario panelPerfil = new PanelPerfilUsuario(usuarioActivo, pilotos);

        pestanas.addTab("Pilotos", panelPilotos);
        pestanas.addTab("Equipos", panelEquipos);
        pestanas.addTab("Vehiculos", panelVehiculos);
        pestanas.addTab("Circuitos", panelCircuitos);
        pestanas.addTab("Clasificacion", panelSimulacion);
        pestanas.addTab("CARRERA EN VIVO", panelCarrera);
        pestanas.addTab("Mi Perfil", panelPerfil);

        pestanas.setSelectedIndex(5);


        add(pestanas, BorderLayout.CENTER);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                GestorDatos.guardarPilotosMap(pilotos);
                GestorDatos.guardarEquiposMap(equipos);
                GestorDatos.guardarCircuitosMap(circuitos);
                GestorDatos.guardarVehiculosMap(vehiculos);
            }
        });
    }

    private JPanel construirBannerHeader() {
        JPanel banner = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(F1Theme.CARD_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        banner.setOpaque(false);
        banner.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel titulo = new JLabel("FORMULA 1   SIMULATOR");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(F1Theme.TEXT_WHITE);


        JLabel subtitulo = new JLabel("Centro de Telemetria y Estrategia de Carrera");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(F1Theme.TEXT_MUTED);

        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        infoPanel.setOpaque(false);
        infoPanel.add(titulo);
        infoPanel.add(subtitulo);

        banner.add(infoPanel, BorderLayout.WEST);

        // Panel de Usuario & Logout
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userPanel.setOpaque(false);

        String rolText = usuarioActivo != null ? usuarioActivo.rol.name() : "GUEST";
        String usrName = usuarioActivo != null ? usuarioActivo.username : "Invitado";

        JLabel userBadge = new JLabel("Sesion: " + usrName + " [" + rolText + "]") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(F1Theme.INPUT_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(F1Theme.BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                g2.setFont(getFont());
                g2.setColor(F1Theme.COLOR_GREEN);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        userBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        userBadge.setPreferredSize(new Dimension(200, 26));
        userPanel.add(userBadge);

        JButton btnLogout = F1Theme.createF1Button("Cerrar Sesion", false);
        btnLogout.addActionListener(e -> cerrarSesion());
        userPanel.add(btnLogout);

        banner.add(userPanel, BorderLayout.EAST);

        return banner;
    }

    private void cerrarSesion() {
        int confirm = JOptionPane.showConfirmDialog(this, "Deseas cerrar la sesion actual?", "Cerrar Sesion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            GestorDatos.guardarPilotosMap(pilotos);
            GestorDatos.guardarEquiposMap(equipos);
            GestorDatos.guardarCircuitosMap(circuitos);
            GestorDatos.guardarVehiculosMap(vehiculos);

            this.dispose();
            SwingUtilities.invokeLater(() -> {
                VentanaLogin login = new VentanaLogin();
                login.setVisible(true);
            });
        }
    }
}
