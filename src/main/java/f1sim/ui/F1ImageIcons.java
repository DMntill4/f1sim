package f1sim.ui;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

// Carga e inicialización de íconos web reales de F1 desde internet (con caché en memoria)
public class F1ImageIcons {

    private static final Map<String, ImageIcon> cacheIconos = new HashMap<>();

    public static ImageIcon getWebIcon(String nombre, String urlStr, int width, int height, Icon fallback) {
        String key = nombre + "_" + width + "x" + height;
        if (cacheIconos.containsKey(key)) {
            return cacheIconos.get(key);
        }

        try {
            URL url = URI.create(urlStr).toURL();
            ImageIcon original = new ImageIcon(url);
            if (original.getIconWidth() > 0 && original.getIconHeight() > 0) {
                Image img = original.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                ImageIcon icono = new ImageIcon(img);
                cacheIconos.put(key, icono);
                return icono;
            }
        } catch (Exception ignored) {
        }

        // Fallback gráfico si no hay conexión a internet
        return convertIconToImageIcon(fallback, width, height);
    }

    private static ImageIcon convertIconToImageIcon(Icon icon, int width, int height) {
        if (icon == null) return new ImageIcon();
        Image img = new java.awt.image.BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.getGraphics();
        icon.paintIcon(null, g, 0, 0);
        g.dispose();
        return new ImageIcon(img);
    }

    public static Icon getF1Logo(int w, int h) {
        return getWebIcon("f1_logo", "https://upload.wikimedia.org/wikipedia/commons/thumb/3/33/F1.svg/120px-F1.svg.png", w, h, F1Iconos.getCarIcon(w, h, F1Theme.F1_RED));
    }

    public static Icon getFlagIcon(int w, int h) {
        return getWebIcon("flag", "https://upload.wikimedia.org/wikipedia/commons/thumb/8/86/Chequered_flag.png/64px-Chequered_flag.png", w, h, F1Iconos.getFlagIcon(w, h));
    }

    public static Icon getTrophyIcon(int w, int h) {
        return getWebIcon("trophy", "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e0/Gold_Trophy_Icon.png/64px-Gold_Trophy_Icon.png", w, h, F1Iconos.getTrophyIcon(w, h));
    }

    public static Icon getHelmetIcon(int w, int h) {
        return getWebIcon("helmet", "https://upload.wikimedia.org/wikipedia/commons/thumb/c/ca/Helmet_icon.svg/64px-Helmet_icon.svg.png", w, h, F1Iconos.getHelmetIcon(w, h, F1Theme.TEXT_WHITE));
    }

    public static Icon getCarIcon(int w, int h) {
        return getWebIcon("car", "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d4/Sports_car_icon.png/64px-Sports_car_icon.png", w, h, F1Iconos.getCarIcon(w, h, F1Theme.F1_RED));
    }
}
