import java.awt.*;

public class UITheme {

    // Base colors
    public static final Color BACKGROUND = new Color(28, 22, 17);
    public static final Color PANEL = new Color(38, 30, 24);
    public static final Color PRIMARY = new Color(214, 177, 98);
    public static final Color PRIMARY_HOVER = new Color(235, 196, 112);
    public static final Color ACCENT = new Color(255, 214, 86);

    // Text colors
    public static final Color TEXT_MAIN = Color.WHITE;
    public static final Color TEXT_SECONDARY = new Color(255, 248, 220);

    // Borders / shadows
    public static final Color BORDER = new Color(0, 0, 0, 70);

    public static Font getFont(float scale, int style, int size) {
        int finalSize = Math.max(10, (int)(size * scale));
        return new Font("Segoe UI", style, finalSize);
    }
}