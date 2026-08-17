package f1sim.ui;

import f1sim.model.Circuito;
import f1sim.model.Piloto;
import f1sim.model.Vehiculo;
import f1sim.race.EventoProgreso;
import f1sim.race.HiloPiloto;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class PanelCarrera extends JPanel {

    private final Map<Integer, Piloto> pilotosMap;
    private final Map<String, Vehiculo> vehiculosMap;
    private final Map<String, Circuito> circuitosMap;

    private JComboBox<Circuito> comboCircuito;
    private JSpinner spinnerVueltas;
    private JComboBox<String> comboModo;
    private JComboBox<String> comboClima;
    private JComboBox<String> comboVelocidad;
    private JComboBox<String> comboPilotoTelemetria;
    private JButton botonIniciar;
    private JButton botonPausa;
    private JButton botonSaltarMeta;
    private JButton botonDetener;
    private JButton botonSonido;
    private JButton botonTelemetriaGlobal;
    private JLabel etiquetaEstadoFlag;
    private JLabel etiquetaDRS;

    private PistaVisual2D pistaVisual;
    private DefaultTableModel modeloPosiciones;
    private JTable tablaPosiciones;
    private JTextArea areaEventos;

    private BlockingQueue<EventoProgreso> colaEventos;
    private final List<HiloPiloto> hilosActivos = new ArrayList<>();
    private final Map<String, EventoProgreso> ultimosEventos = new HashMap<>();
    private final Set<String> finalizados = new LinkedHashSet<>();
    private final Map<String, Color> coloresPorPiloto = new HashMap<>();
    private List<Piloto> pilotosCarrera = new ArrayList<>();
    private int totalVueltas = 0;
    private javax.swing.Timer timer;
    private boolean enPausa = false;
    private String pilotoSeleccionado = null;

    public PanelCarrera(Map<Integer, Piloto> pilotos, Map<String, Vehiculo> vehiculos, Map<String, Circuito> circuitos) {
        this.pilotosMap = pilotos;
        this.vehiculosMap = vehiculos;
        this.circuitosMap = circuitos;
        this.pilotosCarrera = new ArrayList<>(pilotos.values());

        setLayout(new BorderLayout(12, 12));
        setBackground(F1Theme.BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        add(construirPanelBarraTop(), BorderLayout.NORTH);

        pistaVisual = new PistaVisual2D();

        JPanel centroPanel = new JPanel(new BorderLayout(0, 10));
        centroPanel.setOpaque(false);
        centroPanel.add(pistaVisual, BorderLayout.CENTER);
        centroPanel.add(construirPanelTickerEventos(), BorderLayout.SOUTH);

        add(centroPanel, BorderLayout.CENTER);
        add(construirTorrePosiciones(), BorderLayout.EAST);

        timer = new javax.swing.Timer(35, e -> procesarColaYActualizar());

        // Cargar estado inicial de la tabla con todos los pilotos registrados

        inicializarEstadoParrilla();
    }

    private JPanel construirPanelBarraTop() {
        JPanel container = F1Theme.createCardPanel();
        container.setLayout(new GridLayout(2, 1, 0, 6));
        container.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        // FILA 1: Parámetros de Simulación y Selector de Piloto para Telemetría
        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        fila1.setOpaque(false);

        JLabel lblCircuito = new JLabel("Circuito:");
        lblCircuito.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCircuito.setForeground(F1Theme.TEXT_WHITE);
        fila1.add(lblCircuito);

        comboCircuito = new JComboBox<>(circuitosMap.values().toArray(new Circuito[0]));
        comboCircuito.setPreferredSize(new Dimension(160, 26));
        comboCircuito.addActionListener(e -> actualizarCircuitoSeleccionado());
        fila1.add(comboCircuito);

        JLabel lblVueltas = new JLabel("Vueltas:");
        lblVueltas.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblVueltas.setForeground(F1Theme.TEXT_WHITE);
        fila1.add(lblVueltas);

        spinnerVueltas = new JSpinner(new SpinnerNumberModel(52, 1, 200, 1));
        spinnerVueltas.setPreferredSize(new Dimension(55, 26));
        fila1.add(spinnerVueltas);

        JLabel lblModo = new JLabel("Modo:");
        lblModo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        fila1.add(lblModo);
        comboModo = new JComboBox<>(new String[]{"normal", "agresiva", "ahorro"});
        comboModo.setPreferredSize(new Dimension(90, 26));
        fila1.add(comboModo);

        JLabel lblClima = new JLabel("Clima:");
        lblClima.setFont(new Font("Segoe UI", Font.BOLD, 12));
        fila1.add(lblClima);
        comboClima = new JComboBox<>(new String[]{"aleatorio", "seco", "lluvioso", "extremo"});
        comboClima.setPreferredSize(new Dimension(90, 26));
        fila1.add(comboClima);

        JLabel lblVel = new JLabel("Velocidad:");
        lblVel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        fila1.add(lblVel);
        comboVelocidad = new JComboBox<>(new String[]{"1x", "2x", "4x"});
        comboVelocidad.setPreferredSize(new Dimension(55, 26));
        comboVelocidad.addActionListener(e -> {
            String velStr = (String) comboVelocidad.getSelectedItem();
            double factor = 15.0;
            if ("2x".equals(velStr)) factor = 30.0;
            else if ("4x".equals(velStr)) factor = 60.0;
            HiloPiloto.setFactorAceleracionGlobal(factor);
        });
        fila1.add(comboVelocidad);

        JLabel lblFocus = new JLabel("Ver Telemetría:");
        lblFocus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblFocus.setForeground(F1Theme.TEXT_WHITE);
        fila1.add(lblFocus);

        comboPilotoTelemetria = new JComboBox<>();
        comboPilotoTelemetria.setPreferredSize(new Dimension(140, 26));
        for (Piloto p : pilotosMap.values()) {
            comboPilotoTelemetria.addItem(p.nombre);
        }
        comboPilotoTelemetria.addActionListener(e -> {
            pilotoSeleccionado = (String) comboPilotoTelemetria.getSelectedItem();
            if (pistaVisual != null) pistaVisual.repaint();
        });
        fila1.add(comboPilotoTelemetria);

        // FILA 2: Botones de Acción, Telemetría Global, Sonido y Estado
        JPanel fila2 = new JPanel(new BorderLayout());
        fila2.setOpaque(false);

        JPanel botonesAccion = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        botonesAccion.setOpaque(false);

        botonIniciar = F1Theme.createF1Button("INICIAR CARRERA", true);
        botonIniciar.addActionListener(e -> iniciarCarrera());
        botonesAccion.add(botonIniciar);

        botonPausa = F1Theme.createF1Button("PAUSA", false);
        botonPausa.setEnabled(false);
        botonPausa.addActionListener(e -> alternarPausa());
        botonesAccion.add(botonPausa);

        botonSaltarMeta = F1Theme.createF1Button("FINALIZAR (SKIP)", true);
        botonSaltarMeta.setEnabled(false);
        botonSaltarMeta.addActionListener(e -> finalizarCarreraInmediatamente());
        botonesAccion.add(botonSaltarMeta);

        botonDetener = F1Theme.createF1Button("DETENER", false);
        botonDetener.setEnabled(false);
        botonDetener.addActionListener(e -> detenerCarrera("Carrera Detenida"));
        botonesAccion.add(botonDetener);

        botonTelemetriaGlobal = F1Theme.createF1Button("TELEMETRÍA GLOBAL", true);
        botonTelemetriaGlobal.addActionListener(e -> mostrarTelemetriaGlobal());
        botonesAccion.add(botonTelemetriaGlobal);

        botonSonido = F1Theme.createF1Button("AUDIO: OFF", false);
        botonSonido.addActionListener(e -> alternarSonido());
        botonesAccion.add(botonSonido);

        fila2.add(botonesAccion, BorderLayout.WEST);

        // Status Badges (Derecha)
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        statusPanel.setOpaque(false);

        etiquetaDRS = new JLabel("DRS: OFF");
        etiquetaDRS.setFont(new Font("Segoe UI", Font.BOLD, 11));
        etiquetaDRS.setOpaque(true);
        etiquetaDRS.setBackground(F1Theme.INPUT_BG);
        etiquetaDRS.setForeground(F1Theme.TEXT_MUTED);
        etiquetaDRS.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        statusPanel.add(etiquetaDRS);

        etiquetaEstadoFlag = new JLabel(" GREEN FLAG ");
        etiquetaEstadoFlag.setFont(new Font("Segoe UI", Font.BOLD, 11));
        etiquetaEstadoFlag.setOpaque(true);
        etiquetaEstadoFlag.setBackground(F1Theme.COLOR_GREEN);
        etiquetaEstadoFlag.setForeground(Color.BLACK);
        etiquetaEstadoFlag.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        statusPanel.add(etiquetaEstadoFlag);

        fila2.add(statusPanel, BorderLayout.EAST);

        container.add(fila1);
        container.add(fila2);

        actualizarCircuitoSeleccionado();

        return container;
    }

    private void actualizarCircuitoSeleccionado() {
        Circuito c = (Circuito) comboCircuito.getSelectedItem();
        if (c != null) {
            int vueltasOficiales = c.vueltas > 0 ? c.vueltas : 52;
            spinnerVueltas.setValue(vueltasOficiales);
        }
        if (pistaVisual != null) {
            pistaVisual.repaint();
        }
    }

    private void alternarSonido() {
        botonSonido.setText("AUDIO: OFF");
    }


    private JPanel construirTorrePosiciones() {
        JPanel panel = F1Theme.createCardPanel();
        panel.setLayout(new BorderLayout(0, 8));
        panel.setPreferredSize(new Dimension(480, 0));

        JLabel titulo = new JLabel("POSICIONES EN VIVO", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titulo.setForeground(F1Theme.TEXT_WHITE);
        panel.add(titulo, BorderLayout.NORTH);

        String[] columnas = {"Pos", "Piloto", "Diferencia", "Neumático", "Gasolina", "Sector", "Mejor Vuelta"};
        modeloPosiciones = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tablaPosiciones = new JTable(modeloPosiciones);
        F1Theme.styleTable(tablaPosiciones);

        tablaPosiciones.getColumnModel().getColumn(0).setPreferredWidth(40);
        tablaPosiciones.getColumnModel().getColumn(1).setPreferredWidth(125);
        tablaPosiciones.getColumnModel().getColumn(2).setPreferredWidth(75);
        tablaPosiciones.getColumnModel().getColumn(3).setPreferredWidth(85);
        tablaPosiciones.getColumnModel().getColumn(4).setPreferredWidth(65);
        tablaPosiciones.getColumnModel().getColumn(5).setPreferredWidth(50);
        tablaPosiciones.getColumnModel().getColumn(6).setPreferredWidth(75);

        tablaPosiciones.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String pilotoNombre = (String) value;
                Color colorEquipo = coloresPorPiloto.getOrDefault(pilotoNombre, F1Theme.F1_RED);
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 4, 0, 0, colorEquipo),
                        BorderFactory.createEmptyBorder(0, 6, 0, 0)
                ));
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                return label;
            }
        });

        tablaPosiciones.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                String tyre = value != null ? value.toString() : "M (100%)";
                label.setForeground(F1Theme.getCompoundColor(tyre.substring(0, 1)));
                label.setFont(new Font("Segoe UI", Font.BOLD, 11));
                return label;
            }
        });

        tablaPosiciones.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setFont(new Font("Segoe UI", Font.BOLD, 11));
                label.setForeground(F1Theme.COLOR_BLUE);
                return label;
            }
        });

        tablaPosiciones.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = tablaPosiciones.getSelectedRow();
            if (selectedRow != -1) {
                pilotoSeleccionado = (String) tablaPosiciones.getValueAt(selectedRow, 1);
                if (comboPilotoTelemetria != null) comboPilotoTelemetria.setSelectedItem(pilotoSeleccionado);
            }
        });

        JScrollPane scroll = new JScrollPane(tablaPosiciones);
        F1Theme.styleScrollPane(scroll);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel construirPanelTickerEventos() {
        JPanel panel = F1Theme.createCardPanel();
        panel.setLayout(new BorderLayout());
        panel.setPreferredSize(new Dimension(0, 80));

        JLabel title = new JLabel("EVENTOS DE CARRERA", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 11));
        title.setForeground(F1Theme.TEXT_MUTED);
        panel.add(title, BorderLayout.NORTH);

        areaEventos = new JTextArea();
        areaEventos.setEditable(false);
        areaEventos.setBackground(F1Theme.CARD_BG);
        areaEventos.setForeground(F1Theme.TEXT_WHITE);
        areaEventos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        areaEventos.setText("Esperando inicio de carrera...\n");

        JScrollPane scrollArea = new JScrollPane(areaEventos);
        F1Theme.styleScrollPane(scrollArea);
        panel.add(scrollArea, BorderLayout.CENTER);

        return panel;
    }

    private void inicializarEstadoParrilla() {
        ultimosEventos.clear();
        coloresPorPiloto.clear();
        pilotosCarrera = new ArrayList<>(pilotosMap.values());

        for (int i = 0; i < pilotosCarrera.size(); i++) {
            Piloto p = pilotosCarrera.get(i);
            coloresPorPiloto.put(p.nombre, F1Theme.getTeamColor(p.equipo, i));
            String comp = "Lider".equalsIgnoreCase(p.rol) ? "S" : "M";

            ultimosEventos.put(p.nombre, new EventoProgreso(
                    p.nombre, 0.0, 1, false, 0.0,
                    220.0, 1, 0.0, Double.MAX_VALUE,
                    comp, 0.0, 100.0, false, false, "Parrilla de salida lista"
            ));
        }

        actualizarTablaPosiciones();
    }

    private void iniciarCarrera() {
        Circuito circuito = (Circuito) comboCircuito.getSelectedItem();
        String modo = (String) comboModo.getSelectedItem();
        String climaSeleccionado = (String) comboClima.getSelectedItem();
        int vueltasSeleccionadas = (int) spinnerVueltas.getValue();

        if (circuito == null || pilotosMap.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Asegúrate de registrar al menos un circuito y un piloto.");
            return;
        }

        Circuito circuitoCarrera = new Circuito();
        circuitoCarrera.nombre = circuito.nombre;
        circuitoCarrera.pais = circuito.pais;
        circuitoCarrera.longitudKm = circuito.longitudKm;
        circuitoCarrera.vueltas = vueltasSeleccionadas;
        circuitoCarrera.climaPromedio = circuito.climaPromedio;

        String clima = climaSeleccionado;
        if ("aleatorio".equalsIgnoreCase(clima)) {
            String[] opciones = {"seco", "lluvioso", "extremo"};
            clima = opciones[(int) (Math.random() * opciones.length)];
        }

        pilotosCarrera = new ArrayList<>(pilotosMap.values());
        finalizados.clear();
        hilosActivos.clear();
        totalVueltas = vueltasSeleccionadas;

        comboPilotoTelemetria.removeAllItems();
        for (Piloto p : pilotosCarrera) {
            comboPilotoTelemetria.addItem(p.nombre);
        }

        areaEventos.setText("Carrera iniciada en " + circuitoCarrera.nombre + " (" + totalVueltas + " vueltas, " + pilotosCarrera.size() + " pilotos, clima " + clima + ")\n");

        inicializarEstadoParrilla();
        colaEventos = new LinkedBlockingQueue<>();

        for (Piloto piloto : pilotosCarrera) {
            Vehiculo v = obtenerOcrearVehiculoParaEquipo(piloto.equipo);
            HiloPiloto hiloPiloto = new HiloPiloto(piloto, v, circuitoCarrera, modo, clima, colaEventos);
            hilosActivos.add(hiloPiloto);
            Thread hilo = new Thread(hiloPiloto, "f1-hilo-" + piloto.nombre);
            hilo.setDaemon(true);
            hilo.start();
        }

        botonIniciar.setEnabled(false);
        botonPausa.setEnabled(true);
        botonSaltarMeta.setEnabled(true);
        botonDetener.setEnabled(true);
        enPausa = false;

        etiquetaEstadoFlag.setText(" GREEN FLAG ");
        etiquetaEstadoFlag.setBackground(F1Theme.COLOR_GREEN);

        timer.start();
        pistaVisual.repaint();
    }

    private Vehiculo obtenerOcrearVehiculoParaEquipo(String equipoNombre) {
        Vehiculo v = buscarVehiculoDeEquipo(equipoNombre);
        if (v != null) return v;

        Vehiculo nuevo = new Vehiculo();
        nuevo.equipo = equipoNombre;
        nuevo.modelo = equipoNombre + " F1-2024";
        nuevo.motor = "V6 Turbo";
        nuevo.velocidadMaximaKmh = 350.0;
        nuevo.aceleracion = 2.6;
        return nuevo;
    }

    private void mostrarTelemetriaGlobal() {
        if (ultimosEventos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Inicia la carrera para ver la telemetría global.");
            return;
        }

        JDialog dialogo = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Telemetría Global de Todos los Pilotos", true);
        dialogo.setSize(880, 560);
        dialogo.setLocationRelativeTo(this);
        dialogo.getContentPane().setBackground(F1Theme.BG_DARK);
        dialogo.setLayout(new BorderLayout(12, 12));

        JLabel title = new JLabel("TELEMETRÍA EN TIEMPO REAL - TODOS LOS PILOTOS (" + pilotosCarrera.size() + ")", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(F1Theme.TEXT_WHITE);
        dialogo.add(title, BorderLayout.NORTH);

        String[] columnas = {"Piloto", "Equipo", "Velocidad", "Neumático (Vida %)", "Gasolina %", "Sector", "Última Vuelta", "Mejor Vuelta"};
        DefaultTableModel model = new DefaultTableModel(columnas, 0);
        JTable table = new JTable(model);
        F1Theme.styleTable(table);

        for (Piloto p : pilotosCarrera) {
            EventoProgreso ev = ultimosEventos.get(p.nombre);
            if (ev != null) {
                int vidaTyre = Math.max(0, (int)(100 - ev.desgasteNeumatico));
                String best = ev.tiempoMejorVuelta < 9999 ? formatearTiempo(ev.tiempoMejorVuelta) : "--:--";
                String last = ev.tiempoUltimaVuelta > 0 ? formatearTiempo(ev.tiempoUltimaVuelta) : "--:--";
                model.addRow(new Object[]{
                        p.nombre, p.equipo, String.format("%.0f km/h", ev.velocidadKmh),
                        ev.compuestoNeumatico + " (" + vidaTyre + "%)", String.format("%.0f%%", Math.max(0, ev.combustibleRestante)),
                        "S" + ev.sectorActual, last, best
                });
            }
        }

        JScrollPane scroll = new JScrollPane(table);
        F1Theme.styleScrollPane(scroll);
        dialogo.add(scroll, BorderLayout.CENTER);

        JButton btnClose = F1Theme.createF1Button("Cerrar", false);
        btnClose.addActionListener(e -> dialogo.dispose());
        JPanel pnlClose = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlClose.setOpaque(false);
        pnlClose.add(btnClose);
        dialogo.add(pnlClose, BorderLayout.SOUTH);

        dialogo.setVisible(true);
    }

    private void alternarPausa() {
        enPausa = !enPausa;
        for (HiloPiloto h : hilosActivos) {
            h.setPausado(enPausa);
        }
        botonPausa.setText(enPausa ? "REANUDAR" : "PAUSA");
        if (enPausa) {
            etiquetaEstadoFlag.setText(" SAFETY CAR / PAUSA ");
            etiquetaEstadoFlag.setBackground(F1Theme.COLOR_YELLOW);
        } else {
            etiquetaEstadoFlag.setText(" GREEN FLAG ");
            etiquetaEstadoFlag.setBackground(F1Theme.COLOR_GREEN);
        }
    }

    private void finalizarCarreraInmediatamente() {
        if (hilosActivos.isEmpty()) return;
        areaEventos.append("Saltando vueltas inmediatamente hasta la meta...\n");
        for (HiloPiloto h : hilosActivos) {
            h.saltarAMeta();
        }
    }

    private void procesarColaYActualizar() {
        EventoProgreso evento;
        boolean drsGlobal = false;

        while ((evento = colaEventos.poll()) != null) {
            ultimosEventos.put(evento.piloto, evento);
            if (evento.drsActivo) drsGlobal = true;

            if (evento.mensajeEvento != null) {
                areaEventos.append("[" + evento.piloto + "] " + evento.mensajeEvento + "\n");
                areaEventos.setCaretPosition(areaEventos.getDocument().getLength());
            }

            if (evento.terminado) {
                finalizados.add(evento.piloto);
            }
        }

        etiquetaDRS.setText("DRS: " + (drsGlobal ? "ENABLED [OK]" : "OFF"));
        etiquetaDRS.setForeground(drsGlobal ? F1Theme.COLOR_GREEN : F1Theme.TEXT_MUTED);

        pistaVisual.repaint();
        actualizarTablaPosiciones();

        if (!pilotosCarrera.isEmpty() && finalizados.size() == pilotosCarrera.size()) {

            detenerCarrera("Carrera Finalizada");
            etiquetaEstadoFlag.setText(" CHECKERED FLAG ");
            etiquetaEstadoFlag.setBackground(F1Theme.TEXT_WHITE);

            mostrarPodioFinal();
        }
    }

    private void mostrarPodioFinal() {
        List<String> podio = new ArrayList<>(finalizados);
        String p1 = podio.size() > 0 ? podio.get(0) : "N/A";
        String p2 = podio.size() > 1 ? podio.get(1) : "N/A";
        String p3 = podio.size() > 2 ? podio.get(2) : "N/A";

        Color colorOro = new Color(255, 215, 0);
        Color colorPlata = new Color(220, 220, 220);
        Color colorBronce = new Color(205, 127, 50);

        JDialog dialogoPodio = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "PODIO FINAL DE CARRERA", true);
        dialogoPodio.setSize(480, 360);
        dialogoPodio.setLocationRelativeTo(this);
        dialogoPodio.getContentPane().setBackground(F1Theme.BG_DARK);
        dialogoPodio.setLayout(new BorderLayout(12, 12));

        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(14, 14, 4, 14));

        JLabel title = new JLabel("GRAND PRIX FINISHED - PODIO F1", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(F1Theme.F1_RED);

        JLabel sub = new JLabel("Resultados Oficiales (" + totalVueltas + " Vueltas)", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(F1Theme.TEXT_MUTED);

        header.add(title);
        header.add(sub);
        dialogoPodio.add(header, BorderLayout.NORTH);

        JPanel panelPodio = F1Theme.createCardPanel();
        panelPodio.setLayout(new GridLayout(3, 1, 8, 8));

        JLabel lbl1 = new JLabel(" [ P1 - GANADOR ]:  " + p1.toUpperCase());
        lbl1.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl1.setForeground(colorOro);
        lbl1.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 6, 0, 0, colorOro),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        JLabel lbl2 = new JLabel(" [ P2 - SEGUNDO LUGAR ]:  " + p2);
        lbl2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl2.setForeground(F1Theme.TEXT_WHITE);
        lbl2.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 6, 0, 0, colorPlata),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        JLabel lbl3 = new JLabel(" [ P3 - TERCER LUGAR ]:  " + p3);
        lbl3.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl3.setForeground(colorBronce);
        lbl3.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 6, 0, 0, colorBronce),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        panelPodio.add(lbl1);
        panelPodio.add(lbl2);
        panelPodio.add(lbl3);

        dialogoPodio.add(panelPodio, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setOpaque(false);
        JButton btnCerrar = F1Theme.createF1Button("ACEPTAR", true);
        btnCerrar.addActionListener(e -> dialogoPodio.dispose());
        footer.add(btnCerrar);

        dialogoPodio.add(footer, BorderLayout.SOUTH);
        dialogoPodio.setVisible(true);
    }

    private void actualizarTablaPosiciones() {
        List<Piloto> ordenados = new ArrayList<>(pilotosCarrera);
        ordenados.sort((a, b) -> {
            EventoProgreso evA = ultimosEventos.get(a.nombre);
            EventoProgreso evB = ultimosEventos.get(b.nombre);
            if (evA == null) return 1;
            if (evB == null) return -1;

            if (evA.terminado && evB.terminado) {
                return Double.compare(evA.tiempoTotalSegundos, evB.tiempoTotalSegundos);
            }
            if (evA.terminado) return -1;
            if (evB.terminado) return 1;

            double progA = ((evA.vueltaActual - 1) + evA.progresoVueltaActual);
            double progB = ((evB.vueltaActual - 1) + evB.progresoVueltaActual);
            return Double.compare(progB, progA);
        });

        modeloPosiciones.setRowCount(0);
        int pos = 1;
        double tiempoLider = 0.0;

        for (Piloto p : ordenados) {
            EventoProgreso ev = ultimosEventos.get(p.nombre);
            if (ev == null) continue;

            if (pos == 1) {
                tiempoLider = ev.tiempoTotalSegundos;
            }

            String gap = (pos == 1) ? "LIDER" : String.format("+%.2fs", Math.max(0, ev.tiempoTotalSegundos - tiempoLider));
            int vidaNeumaticoPct = Math.max(0, (int)(100 - ev.desgasteNeumatico));
            String tyre = ev.compuestoNeumatico + " (" + vidaNeumaticoPct + "%)";
            String fuel = String.format("%.0f%%", Math.max(0, ev.combustibleRestante));

            String s1 = ev.sectorActual == 1 ? "[1]" : "OK";
            String s2 = ev.sectorActual == 2 ? (ev.drsActivo ? "[DRS]" : "OK") : "--";
            String s3 = ev.sectorActual == 3 ? "OK" : "--";

            String best = ev.tiempoMejorVuelta < 9999 ? formatearTiempo(ev.tiempoMejorVuelta) : "--:--";

            modeloPosiciones.addRow(new Object[]{
                    "P" + pos, p.nombre, gap, tyre, fuel, s1, s2, s3, best
            });
            pos++;
        }
    }

    private String formatearTiempo(double segs) {
        return FormateadorF1.formatearTiempoVuelta(segs);
    }


    private void detenerCarrera(String mensajeEstado) {
        for (HiloPiloto h : hilosActivos) {
            h.detener();
        }
        timer.stop();
        botonIniciar.setEnabled(true);

        botonPausa.setEnabled(false);
        botonSaltarMeta.setEnabled(false);
        botonDetener.setEnabled(false);
    }

    private Vehiculo buscarVehiculoDeEquipo(String nombreEquipo) {
        for (Vehiculo v : vehiculosMap.values()) {
            if (v.equipo.equalsIgnoreCase(nombreEquipo)) return v;
        }
        return null;
    }

    private class PistaVisual2D extends JPanel {

        private Path2D trackPath;
        private List<Point2D> trackPoints;

        PistaVisual2D() {
            setBackground(F1Theme.CARD_BG);
        }

        private void generarPistaShape(int width, int height) {
            Circuito cSel = (Circuito) comboCircuito.getSelectedItem();
            String nombreCircuito = cSel != null ? cSel.nombre.toLowerCase() : "";

            int cx = width / 2;
            int cy = height / 2;
            int rx = (width - 140) / 2;
            int ry = (height - 140) / 2;

            trackPoints = new ArrayList<>();
            int numPoints = 240;

            if (nombreCircuito.contains("mónaco") || nombreCircuito.contains("monaco")) {
                for (int i = 0; i < numPoints; i++) {
                    double t = 2 * Math.PI * i / numPoints;
                    double r = 1.0 + 0.30 * Math.sin(2 * t) + 0.25 * Math.cos(4 * t);
                    double x = cx + (rx * 0.85) * r * Math.cos(t);
                    double y = cy + (ry * 0.75) * r * Math.sin(t);
                    trackPoints.add(new Point2D.Double(x, y));
                }
            } else if (nombreCircuito.contains("silverstone")) {
                for (int i = 0; i < numPoints; i++) {
                    double t = 2 * Math.PI * i / numPoints;
                    double r = 1.0 + 0.20 * Math.cos(3 * t) - 0.15 * Math.sin(5 * t);
                    double x = cx + rx * r * Math.cos(t);
                    double y = cy + ry * r * Math.sin(t);
                    trackPoints.add(new Point2D.Double(x, y));
                }
            } else if (nombreCircuito.contains("spa")) {
                for (int i = 0; i < numPoints; i++) {
                    double t = 2 * Math.PI * i / numPoints;
                    double r = 1.1 + 0.35 * Math.sin(t) * Math.cos(2 * t);
                    double x = cx + (rx * 1.05) * r * Math.cos(t);
                    double y = cy + (ry * 0.65) * r * Math.sin(t);
                    trackPoints.add(new Point2D.Double(x, y));
                }
            } else if (nombreCircuito.contains("monza")) {
                for (int i = 0; i < numPoints; i++) {
                    double t = 2 * Math.PI * i / numPoints;
                    double r = 1.0 + 0.10 * Math.pow(Math.cos(2 * t), 3);
                    double x = cx + (rx * 1.1) * r * Math.cos(t);
                    double y = cy + (ry * 0.55) * r * Math.sin(t);
                    trackPoints.add(new Point2D.Double(x, y));
                }
            } else if (nombreCircuito.contains("interlagos")) {
                for (int i = 0; i < numPoints; i++) {
                    double t = 2 * Math.PI * i / numPoints;
                    double r = 1.0 + 0.22 * Math.sin(4 * t);
                    double x = cx + (rx * 0.9) * r * Math.cos(t);
                    double y = cy + (ry * 0.85) * r * Math.sin(t);
                    trackPoints.add(new Point2D.Double(x, y));
                }
            } else {
                for (int i = 0; i < numPoints; i++) {
                    double t = 2 * Math.PI * i / numPoints;
                    double r = 1.0 + 0.15 * Math.sin(3 * t) + 0.10 * Math.cos(5 * t);
                    double x = cx + rx * r * Math.cos(t);
                    double y = cy + ry * r * Math.sin(t);
                    trackPoints.add(new Point2D.Double(x, y));
                }
            }

            trackPath = new Path2D.Double();
            trackPath.moveTo(trackPoints.get(0).getX(), trackPoints.get(0).getY());
            for (int i = 1; i < trackPoints.size(); i++) {
                trackPath.lineTo(trackPoints.get(i).getX(), trackPoints.get(i).getY());
            }
            trackPath.closePath();
        }

        private Point2D obtenerPuntoPista(double progreso) {
            if (trackPoints == null || trackPoints.isEmpty()) return new Point2D.Double(0, 0);
            double idxDouble = (progreso % 1.0) * trackPoints.size();
            int idx1 = (int) idxDouble % trackPoints.size();
            int idx2 = (idx1 + 1) % trackPoints.size();
            double frac = idxDouble - Math.floor(idxDouble);

            Point2D p1 = trackPoints.get(idx1);
            Point2D p2 = trackPoints.get(idx2);

            double x = p1.getX() + (p2.getX() - p1.getX()) * frac;
            double y = p1.getY() + (p2.getY() - p1.getY()) * frac;
            return new Point2D.Double(x, y);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

            int w = getWidth();
            int h = getHeight();
            generarPistaShape(w, h);

            g2.setColor(F1Theme.CARD_BG);
            g2.fillRoundRect(0, 0, w, h, 8, 8);

            // Kerbs Border
            g2.setColor(F1Theme.F1_RED);
            g2.setStroke(new BasicStroke(24f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(trackPath);

            g2.setColor(F1Theme.TEXT_WHITE);
            g2.setStroke(new BasicStroke(20f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(trackPath);

            // Asphalt Track
            g2.setColor(new Color(18, 20, 28));
            g2.setStroke(new BasicStroke(16f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(trackPath);

            // Dotted Centerline
            g2.setColor(new Color(45, 50, 68));
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[]{6f, 6f}, 0f));
            g2.draw(trackPath);

            // Start/Finish Line Indicator
            if (!trackPoints.isEmpty()) {
                Point2D pMeta = trackPoints.get(0);
                g2.setColor(F1Theme.TEXT_WHITE);
                g2.fillOval((int) pMeta.getX() - 6, (int) pMeta.getY() - 6, 12, 12);
                g2.setColor(F1Theme.F1_RED);
                g2.drawOval((int) pMeta.getX() - 8, (int) pMeta.getY() - 8, 16, 16);
            }

            if (pilotosCarrera.isEmpty()) {
                g2.setColor(F1Theme.TEXT_MUTED);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                g2.drawString("Selecciona un circuito y presiona 'INICIAR CARRERA'", w / 3, h / 2);
                return;
            }

            // Draw Cars
            for (Piloto p : pilotosCarrera) {
                EventoProgreso ev = ultimosEventos.get(p.nombre);
                double prog = ev != null ? ev.progresoVueltaActual : 0.0;
                Point2D pCoche = obtenerPuntoPista(prog);

                int x = (int) pCoche.getX();
                int y = (int) pCoche.getY();

                Color colorEquipo = coloresPorPiloto.getOrDefault(p.nombre, Color.WHITE);

                g2.setColor(new Color(colorEquipo.getRed(), colorEquipo.getGreen(), colorEquipo.getBlue(), 60));
                g2.fillOval(x - 8, y - 8, 16, 16);

                g2.setColor(colorEquipo);
                g2.fillOval(x - 5, y - 5, 10, 10);
                g2.setColor(F1Theme.TEXT_WHITE);
                g2.drawOval(x - 5, y - 5, 10, 10);

                g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                g2.setColor(F1Theme.TEXT_WHITE);
                String label = p.nombre.split(" ")[0];
                if (ev != null && ev.drsActivo) label += " (DRS)";
                if (ev != null && ev.enPitLane) label += " (PIT)";
                g2.drawString(label, x + 8, y + 4);
            }

            // Telemetry Card
            String targetDriver = pilotoSeleccionado != null ? pilotoSeleccionado : (pilotosCarrera.isEmpty() ? null : pilotosCarrera.get(0).nombre);
            if (targetDriver != null && ultimosEventos.containsKey(targetDriver)) {
                EventoProgreso ev = ultimosEventos.get(targetDriver);
                g2.setColor(new Color(18, 20, 30, 230));
                g2.fillRoundRect(15, h - 85, 260, 70, 8, 8);
                g2.setColor(F1Theme.BORDER_COLOR);
                g2.drawRoundRect(15, h - 85, 260, 70, 8, 8);

                g2.setColor(F1Theme.TEXT_WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                g2.drawString("TELEMETRIA: " + targetDriver, 25, h - 65);

                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                g2.drawString(String.format("Velocidad: %.0f km/h  |  Vuelta: %d/%d", ev.velocidadKmh, ev.vueltaActual, totalVueltas), 25, h - 48);
                g2.drawString(String.format("Neumático: %s (%d%% vida) | Sector %d", ev.compuestoNeumatico, Math.max(0, (int)(100 - ev.desgasteNeumatico)), ev.sectorActual), 25, h - 33);
                g2.drawString(String.format("Gasolina: %.0f%% restante", Math.max(0, ev.combustibleRestante)), 25, h - 18);
            }
        }
    }
}
