import java.awt.*;

public class UITheme {

    // Base colors
    public static final Color BACKGROUND = new Color(18, 18, 24);
    public static final Color PANEL = new Color(28, 28, 38);
    public static final Color PRIMARY = new Color(255, 209, 102);
    public static final Color PRIMARY_HOVER = new Color(255, 224, 138);
    public static final Color ACCENT = new Color(255, 209, 102);
    public static final Color PLAYER_A = new Color(255, 92, 146);
    public static final Color PLAYER_B = new Color(72, 149, 239);
    public static final Color HIGHLIGHT = new Color(255, 209, 102);
    public static final Color EMPTY_PIT = new Color(70, 70, 82);

    // Text colors
    public static final Color TEXT_MAIN = Color.WHITE;
    public static final Color TEXT_SECONDARY = new Color(210, 212, 224);

    // Borders / shadows
    public static final Color BORDER = new Color(255, 255, 255, 18);

    public static Font getFont(float scale, int style, int size) {
        int finalSize = Math.max(10, (int)(size * scale));
        return new Font("Segoe UI", style, finalSize);
    }
}