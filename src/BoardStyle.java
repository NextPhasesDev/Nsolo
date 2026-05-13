import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public final class BoardStyle {
    public static final Color APP_BACKGROUND = new Color(0x0B1020);
    public static final Color BOARD_BASE = new Color(0x1B2238);
    public static final Color BOARD_EDGE = new Color(0x0F1530);
    public static final Color EMPTY_CELL = new Color(0x3A4566);
    public static final Color PLAYER_A = new Color(0xFF4FD8);
    public static final Color PLAYER_A_GLOW = new Color(0xFF8AE8);
    public static final Color PLAYER_B = new Color(0x4DA6FF);
    public static final Color PLAYER_B_GLOW = new Color(0x8DCAFF);
    public static final Color ACTIVE_BORDER = new Color(0xFFD84D);
    public static final Color TEXT_PRIMARY = new Color(0xF4F7FF);
    public static final Color TEXT_SECONDARY = new Color(0xA8B2D1);
    public static final Color SUCCESS = new Color(0x5DFFB2);
    public static final Color DANGER = new Color(0xFF6B6B);
    public static final Color SURFACE = new Color(0x11192E);
    public static final Color SURFACE_HIGHLIGHT = new Color(0x1A2646);

    public static final int BOARD_GAP = 8;
    public static final int BOARD_PADDING = 16;
    public static final int BOARD_MIN_CELL = 46;
    public static final int BOARD_MAX_PADDING = 40;
    public static final int CELL_RADIUS = 26;
    public static final int BUTTON_RADIUS = 18;
    public static final int SHADOW_OFFSET = 6;
    public static final float HOVER_SCALE = 1.04f;
    public static final float PRESS_SCALE = 0.98f;

    private BoardStyle() {
    }

    public static void enableQuality(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    }

    public static Color mix(Color a, Color b, float t) {
        t = Math.max(0f, Math.min(1f, t));
        int r = Math.round(a.getRed() + (b.getRed() - a.getRed()) * t);
        int g = Math.round(a.getGreen() + (b.getGreen() - a.getGreen()) * t);
        int bl = Math.round(a.getBlue() + (b.getBlue() - a.getBlue()) * t);
        int al = Math.round(a.getAlpha() + (b.getAlpha() - a.getAlpha()) * t);
        return new Color(r, g, bl, al);
    }

    public static Color withAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(0, Math.min(255, alpha)));
    }

    public static RoundRectangle2D.Float roundRect(float x, float y, float w, float h, float arc) {
        return new RoundRectangle2D.Float(x, y, w, h, arc, arc);
    }

    public static Shape inflatedRoundRect(Shape shape, float inflate, float arc) {
        Rectangle bounds = shape.getBounds();
        return roundRect(bounds.x - inflate, bounds.y - inflate, bounds.width + inflate * 2f, bounds.height + inflate * 2f, arc + inflate * 2f);
    }

    public static void paintGlow(Graphics2D g2, Shape shape, Color color, float intensity) {
        if (intensity <= 0f) {
            return;
        }
        Composite oldComposite = g2.getComposite();
        Shape current = shape;
        for (int i = 5; i >= 1; i--) {
            float layer = intensity * (i / 5f);
            int alpha = Math.max(0, Math.min(255, Math.round(color.getAlpha() * layer * 0.34f)));
            g2.setColor(withAlpha(color, alpha));
            g2.fill(inflatedRoundRect(current, i * 2f, CELL_RADIUS + i * 2f));
        }
        g2.setComposite(oldComposite);
    }

    public static void paintLayeredShadow(Graphics2D g2, Shape shape, float strength) {
        if (strength <= 0f) {
            return;
        }
        for (int i = 6; i >= 1; i--) {
            int alpha = Math.max(0, Math.min(120, Math.round(strength * i * 10f)));
            g2.setColor(new Color(0, 0, 0, alpha));
            Shape shadowShape = inflatedRoundRect(shape, i * 1.4f, CELL_RADIUS + i * 2f);
            g2.fill(shadowShape);
        }
    }

    public static Color teamColorForRow(int row) {
        return row < 2 ? PLAYER_B : PLAYER_A;
    }

    public static Color teamGlowForRow(int row) {
        return row < 2 ? PLAYER_B_GLOW : PLAYER_A_GLOW;
    }

    public static Color teamTextForRow(int row) {
        return row < 2 ? PLAYER_B : PLAYER_A;
    }

    public static Color stoneColorForRow(int row) {
        return row < 2 ? PLAYER_B : PLAYER_A;
    }
}

