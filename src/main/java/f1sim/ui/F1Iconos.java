package f1sim.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;

// Generador de íconos vectoriales HD estilizados de F1 para Swing (100% confiables sin depender de fuentes de emojis)
public class F1Iconos {

    public static Icon getHelmetIcon(int width, int height, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.translate(x, y);

                // Casco F1
                g2.setColor(color != null ? color : F1Theme.TEXT_WHITE);
                g2.fillOval(2, 2, width - 4, height - 4);

                // Visor de casco
                g2.setColor(new Color(20, 25, 35));
                g2.fillRoundRect(width / 3, height / 3, width / 2, height / 4, 4, 4);

                g2.setColor(F1Theme.COLOR_BLUE);
                g2.drawRoundRect(width / 3, height / 3, width / 2, height / 4, 4, 4);

                g2.dispose();
            }

            @Override
            public int getIconWidth() { return width; }
            @Override
            public int getIconHeight() { return height; }
        };
    }

    public static Icon getCarIcon(int width, int height, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.translate(x, y);

                Color cMain = color != null ? color : F1Theme.F1_RED;

                // Alerón trasero
                g2.setColor(cMain);
                g2.fillRect(0, 4, 3, height - 8);

                // Cuerpo del monoplaza
                Path2D car = new Path2D.Double();
                car.moveTo(3, height / 2.0 - 3);
                car.lineTo(width - 6, height / 2.0 - 2);
                car.lineTo(width - 1, height / 2.0); // Trocha delantera
                car.lineTo(width - 6, height / 2.0 + 2);
                car.lineTo(3, height / 2.0 + 3);
                car.closePath();
                g2.fill(car);

                // Neumáticos
                g2.setColor(Color.DARK_GRAY);
                g2.fillRect(4, 1, 4, 3);
                g2.fillRect(4, height - 4, 4, 3);
                g2.fillRect(width - 7, 1, 4, 3);
                g2.fillRect(width - 7, height - 4, 4, 3);

                g2.dispose();
            }

            @Override
            public int getIconWidth() { return width; }
            @Override
            public int getIconHeight() { return height; }
        };
    }

    public static Icon getCircuitIcon(int width, int height, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.translate(x, y);

                g2.setColor(color != null ? color : F1Theme.COLOR_GREEN);
                g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                Path2D track = new Path2D.Double();
                track.moveTo(3, height / 2.0);
                track.curveTo(3, 2, width / 2.0, 2, width - 4, height / 3.0);
                track.curveTo(width - 2, height - 2, width / 2.0, height - 2, 3, height / 2.0);
                g2.draw(track);

                g2.dispose();
            }

            @Override
            public int getIconWidth() { return width; }
            @Override
            public int getIconHeight() { return height; }
        };
    }

    public static Icon getTrophyIcon(int width, int height) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.translate(x, y);

                // Copa Dorada
                g2.setColor(new Color(255, 215, 0));
                g2.fillOval(3, 1, width - 6, height / 2);
                g2.fillRect(width / 3, height / 2, width / 3, height / 3);

                // Base
                g2.setColor(new Color(180, 140, 20));
                g2.fillRect(2, height - 4, width - 4, 3);

                g2.dispose();
            }

            @Override
            public int getIconWidth() { return width; }
            @Override
            public int getIconHeight() { return height; }
        };
    }

    public static Icon getFlagIcon(int width, int height) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.translate(x, y);

                // Mástil
                g2.setColor(F1Theme.TEXT_MUTED);
                g2.fillRect(1, 1, 2, height - 2);

                // Cuadrícula Bandera a Cuadros
                int sq = 3;
                for (int r = 0; r < 3; r++) {
                    for (int col = 0; col < 4; col++) {
                        boolean isWhite = (r + col) % 2 == 0;
                        g2.setColor(isWhite ? Color.WHITE : Color.BLACK);
                        g2.fillRect(3 + (col * sq), 1 + (r * sq), sq, sq);
                    }
                }

                g2.dispose();
            }

            @Override
            public int getIconWidth() { return width; }
            @Override
            public int getIconHeight() { return height; }
        };
    }

    public static Icon getTelemetryIcon(int width, int height) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.translate(x, y);

                // Volante F1
                g2.setColor(F1Theme.INPUT_BG);
                g2.fillRoundRect(1, 2, width - 2, height - 4, 6, 6);
                g2.setColor(F1Theme.TEXT_WHITE);
                g2.drawRoundRect(1, 2, width - 2, height - 4, 6, 6);

                // Pantalla LCD Telemetría
                g2.setColor(F1Theme.COLOR_GREEN);
                g2.fillRect(4, 4, width - 8, 4);

                g2.dispose();
            }

            @Override
            public int getIconWidth() { return width; }
            @Override
            public int getIconHeight() { return height; }
        };
    }
}
