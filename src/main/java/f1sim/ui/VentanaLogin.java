package f1sim.ui;

import f1sim.datos.GestorDatos;
import f1sim.model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class VentanaLogin extends JFrame {

    private JTextField campoUsuario;
    private JPasswordField campoPassword;
    private JLabel etiquetaMensaje;

    public VentanaLogin() {
        super("F1 Simulator - Inicio de Sesión");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 360);
        setLocationRelativeTo(null);
        setResizable(false);

        getContentPane().setBackground(F1Theme.BG_DARK);
        setLayout(new BorderLayout());

        add(construirHeader(), BorderLayout.NORTH);
        add(construirFormulario(), BorderLayout.CENTER);

        // Cargar usuarios por defecto si no existen
        GestorDatos.cargarUsuarios();
    }

    private JPanel construirHeader() {
        JPanel banner = new JPanel(new GridLayout(2, 1, 0, 4)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(F1Theme.CARD_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(F1Theme.BORDER_COLOR);
                g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                g2.dispose();
            }
        };
        banner.setOpaque(false);
        banner.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel titulo = new JLabel("F1 SIMULATOR LOGIN", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(F1Theme.TEXT_WHITE);

        JLabel subtitulo = new JLabel("Ingresa tus credenciales para continuar", SwingConstants.CENTER);
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(F1Theme.TEXT_MUTED);

        banner.add(titulo);
        banner.add(subtitulo);

        return banner;
    }

    private JPanel construirFormulario() {
        JPanel container = F1Theme.createCardPanel();
        container.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 12, 6, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Usuario
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblUser = new JLabel("Usuario:");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        container.add(lblUser, gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        campoUsuario = new JTextField(16);
        campoUsuario.setText("admin"); // Default hint
        container.add(campoUsuario, gbc);

        // Contraseña
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        container.add(lblPass, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        campoPassword = new JPasswordField(16);
        campoPassword.setText("admin123"); // Default hint
        container.add(campoPassword, gbc);

        // Mensaje de estado
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        etiquetaMensaje = new JLabel("Usuarios: admin/admin123 | verstappen/123 | leclerc/123", SwingConstants.CENTER);
        etiquetaMensaje.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        etiquetaMensaje.setForeground(F1Theme.TEXT_MUTED);
        container.add(etiquetaMensaje, gbc);


        // Boton Iniciar Sesion
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.insets = new Insets(14, 12, 6, 12);
        JButton btnLogin = F1Theme.createF1Button("INICIAR SESIÓN", true);
        btnLogin.addActionListener(e -> autenticar());
        container.add(btnLogin, gbc);

        return container;
    }

    private void autenticar() {
        String username = campoUsuario.getText().trim();
        String password = new String(campoPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            etiquetaMensaje.setText("Por favor ingresa usuario y contraseña.");
            etiquetaMensaje.setForeground(F1Theme.F1_RED);
            return;
        }

        List<Usuario> lista = GestorDatos.cargarUsuarios();
        Usuario usuarioAutenticado = null;

        for (Usuario u : lista) {
            if (u.username.equalsIgnoreCase(username) && u.password.equals(password)) {
                usuarioAutenticado = u;
                break;
            }
        }

        if (usuarioAutenticado != null) {
            final Usuario usr = usuarioAutenticado;
            this.dispose();
            SwingUtilities.invokeLater(() -> {
                VentanaPrincipal principal = new VentanaPrincipal(usr);
                principal.setVisible(true);
            });
        } else {
            etiquetaMensaje.setText("Usuario o contraseña incorrectos.");
            etiquetaMensaje.setForeground(F1Theme.F1_RED);
        }
    }
}
