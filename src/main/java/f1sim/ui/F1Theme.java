package f1sim.ui;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class F1Theme {

    // ---- Muted, Soft Premium Palette ----
    public static final Color F1_RED = new Color(195, 30, 35);            // Muted, elegant Crimson Red (not harsh)
    public static final Color BG_DARK = new Color(18, 20, 26);           // Soft Deep Dark Background
    public static final Color CARD_BG = new Color(26, 29, 39);           // Harmonious Soft Card Background
    public static final Color CARD_BG_HOVER = new Color(34, 38, 52);
    public static final Color INPUT_BG = new Color(34, 38, 52);          // Input & Header Background
    public static final Color BORDER_COLOR = new Color(44, 49, 66);      // Very subtle dark border (NO white borders)

    public static final Color TEXT_WHITE = new Color(230, 235, 245);     // Soft white
    public static final Color TEXT_MUTED = new Color(140, 148, 170);     // Subtle slate gray

    // Muted Status Colors
    public static final Color COLOR_GREEN = new Color(40, 167, 69);
    public static final Color COLOR_PURPLE = new Color(142, 68, 173);
    public static final Color COLOR_YELLOW = new Color(212, 160, 23);
    public static final Color COLOR_BLUE = new Color(41, 128, 185);

    // F1 Team Colors (Muted & Harmonious)
    private static final Map<String, Color> TEAM_COLORS = new HashMap<>();
    static {
        TEAM_COLORS.put("red bull", new Color(45, 95, 170));
        TEAM_COLORS.put("ferrari", new Color(200, 30, 35));
        TEAM_COLORS.put("mercedes", new Color(30, 190, 165));
        TEAM_COLORS.put("mclaren", new Color(230, 115, 0));
        TEAM_COLORS.put("aston martin", new Color(30, 135, 100));
        TEAM_COLORS.put("alpine", new Color(0, 130, 185));
        TEAM_COLORS.put("williams", new Color(80, 165, 220));
        TEAM_COLORS.put("haas", new Color(160, 165, 170));
        TEAM_COLORS.put("sauber", new Color(70, 190, 70));
        TEAM_COLORS.put("rb", new Color(85, 125, 220));
    }

    public static final Color[] FALLBACK_COLORS = {
            F1_RED, new Color(45, 95, 170), new Color(30, 190, 165),
            new Color(230, 115, 0), new Color(30, 135, 100), new Color(142, 68, 173)
    };

    public static Color getTeamColor(String equipo, int index) {
        if (equipo == null) return FALLBACK_COLORS[index % FALLBACK_COLORS.length];
        String key = equipo.trim().toLowerCase();
        for (Map.Entry<String, Color> entry : TEAM_COLORS.entrySet()) {
            if (key.contains(entry.getKey())) return entry.getValue();
        }
        return FALLBACK_COLORS[Math.abs(equipo.hashCode()) % FALLBACK_COLORS.length];
    }

    public static Color getCompoundColor(String compuesto) {
        if (compuesto == null) return COLOR_GREEN;
        switch (compuesto.toUpperCase()) {
            case "SOFT": case "S": return F1_RED;
            case "MEDIUM": case "M": return COLOR_YELLOW;
            case "HARD": case "H": return TEXT_WHITE;
            case "INTERMEDIATE": case "I": return COLOR_GREEN;
            case "WET": case "W": return COLOR_BLUE;
            default: return COLOR_YELLOW;
        }
    }

    public static void applyGlobalTheme() {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        // Force all Swing component UIManager keys to dark colors
        UIManager.put("Panel.background", BG_DARK);
        UIManager.put("Viewport.background", CARD_BG);
        UIManager.put("ScrollPane.background", BG_DARK);
        UIManager.put("ScrollPane.border", BorderFactory.createLineBorder(BORDER_COLOR, 1));
        
        UIManager.put("OptionPane.background", BG_DARK);
        UIManager.put("OptionPane.messageForeground", TEXT_WHITE);
        UIManager.put("Label.foreground", TEXT_WHITE);
        UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 13));
        UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 12));
        
        UIManager.put("Table.background", CARD_BG);
        UIManager.put("Table.foreground", TEXT_WHITE);
        UIManager.put("Table.gridColor", BORDER_COLOR);
        UIManager.put("TableHeader.background", INPUT_BG);
        UIManager.put("TableHeader.foreground", TEXT_MUTED);
        UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 12));
        
        UIManager.put("ComboBox.background", INPUT_BG);
        UIManager.put("ComboBox.foreground", TEXT_WHITE);
        UIManager.put("ComboBox.selectionBackground", CARD_BG_HOVER);
        UIManager.put("ComboBox.selectionForeground", TEXT_WHITE);

        UIManager.put("TextField.background", INPUT_BG);
        UIManager.put("TextField.foreground", TEXT_WHITE);
        UIManager.put("TextField.caretForeground", TEXT_WHITE);
        UIManager.put("TextField.border", BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));

        UIManager.put("TextArea.background", CARD_BG);
        UIManager.put("TextArea.foreground", TEXT_WHITE);
        UIManager.put("TextArea.caretForeground", TEXT_WHITE);

        UIManager.put("TabbedPane.background", BG_DARK);
        UIManager.put("TabbedPane.foreground", TEXT_MUTED);
        UIManager.put("TabbedPane.selected", CARD_BG);
        UIManager.put("TabbedPane.selectedForeground", F1_RED);
        UIManager.put("TabbedPane.contentAreaColor", CARD_BG);
        UIManager.put("TabbedPane.borderHighlightColor", BORDER_COLOR);
        UIManager.put("TabbedPane.darkShadow", BG_DARK);
        UIManager.put("TabbedPane.shadow", BORDER_COLOR);
        UIManager.put("TabbedPane.light", BORDER_COLOR);
        UIManager.put("TabbedPane.highlight", BORDER_COLOR);

        UIManager.put("SplitPane.background", BG_DARK);
        UIManager.put("SplitPaneDivider.border", BorderFactory.createEmptyBorder());
    }

    public static JButton createF1Button(String text, boolean primary) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color bg = primary ? F1_RED : INPUT_BG;
                if (getModel().isPressed()) {
                    bg = primary ? F1_RED.darker() : INPUT_BG.darker();
                } else if (getModel().isRollover()) {
                    bg = primary ? F1_RED.brighter() : CARD_BG_HOVER;
                }

                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);

                g2.setColor(primary ? F1_RED.brighter() : BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);

                g2.setFont(getFont());
                g2.setColor(TEXT_WHITE);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };

        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 16, 30));
        return btn;
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(36);
        table.setIntercellSpacing(new Dimension(8, 2));
        table.setShowGrid(true);
        table.setGridColor(BORDER_COLOR);
        table.setBackground(CARD_BG);
        table.setForeground(TEXT_WHITE);
        table.setSelectionBackground(CARD_BG_HOVER);
        table.setSelectionForeground(TEXT_WHITE);

        JTableHeader header = table.getTableHeader();
        header.setBackground(INPUT_BG);
        header.setForeground(TEXT_MUTED);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setPreferredSize(new Dimension(0, 36));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? CARD_BG : new Color(22, 25, 34));
                    c.setForeground(TEXT_WHITE);
                }
                setBorder(new EmptyBorder(0, 8, 0, 8));
                return c;
            }
        });
    }

    public static void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(true);
        scrollPane.getViewport().setBackground(CARD_BG);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
    }

    public static JPanel createCardPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 12, 10, 12));
        return panel;
    }
}
