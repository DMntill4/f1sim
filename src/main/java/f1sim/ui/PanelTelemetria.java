package f1sim.ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

// Panel Swing para visualizar la telemetría en tiempo real (tiempos de vuelta y deltas)
public class PanelTelemetria extends JPanel {

    private final List<Double> tiemposVuelta = new ArrayList<>();
    private String pilotoActual = "Piloto";

    public PanelTelemetria() {
        setBackground(new Color(25, 25, 30));
        setPreferredSize(new Dimension(500, 200));
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(225, 6, 0)),
                " Telemetría en Vivo - Tiempos por Vuelta ",
                0, 0, null, Color.WHITE));
    }

    public void registrarTiempoVuelta(String piloto, double tiempoSegundos) {
        this.pilotoActual = piloto;
        this.tiemposVuelta.add(tiempoSegundos);
        repaint();
    }

    public void limpiarTelemetria() {
        this.tiemposVuelta.clear();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        g2.setColor(Color.GRAY);
        g2.drawString("Piloto: " + pilotoActual, 20, 30);

        if (tiemposVuelta.size() < 2) {
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawString("Esperando suficientes datos de vuelta...", 20, height / 2);
            return;
        }

        // Dibujar gráfico de líneas
        g2.setColor(new Color(225, 6, 0));
        int margin = 40;
        int graphWidth = width - (margin * 2);
        int graphHeight = height - (margin * 2);

        double minTime = tiemposVuelta.stream().min(Double::compare).orElse(60.0);
        double maxTime = tiemposVuelta.stream().max(Double::compare).orElse(120.0);
        double range = Math.max(1.0, maxTime - minTime);

        int prevX = margin;
        int prevY = height - margin - (int) (((tiemposVuelta.get(0) - minTime) / range) * graphHeight);

        for (int i = 1; i < tiemposVuelta.size(); i++) {
            int x = margin + (i * graphWidth / (tiemposVuelta.size() - 1));
            int y = height - margin - (int) (((tiemposVuelta.get(i) - minTime) / range) * graphHeight);

            g2.setStroke(new BasicStroke(2.5f));
            g2.drawLine(prevX, prevY, x, y);
            g2.fillOval(x - 3, y - 3, 6, 6);

            prevX = x;
            prevY = y;
        }
    }
}
