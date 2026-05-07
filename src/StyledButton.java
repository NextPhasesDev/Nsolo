import javax.swing.*;
import java.awt.*;

public class StyledButton extends JButton {

    private static final int BASE_RADIUS = 24;
    private static final int BASE_INSET = 4;
    private static final float MAX_SCALE_PX = 2.0f;

    private Color currentBg;
    private Color targetBg;
    private javax.swing.Timer animTimer;
    private static final int ANIM_DURATION = 140; // ms
    private long animStart;
    private boolean hovered = false;
    private float currentScale = 0f;
    private float targetScale = 0f;

    public StyledButton(String text, float fontScale) {
        super(text);

        setFont(UITheme.getFont(fontScale, Font.BOLD, 18));
        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setForeground(UITheme.TEXT_MAIN);
        setBackground(UITheme.PRIMARY);
        setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));
        setAlignmentX(Component.CENTER_ALIGNMENT);
        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);

        setOpaque(false);
        setContentAreaFilled(false);

        currentBg = UITheme.PRIMARY;
        targetBg = UITheme.PRIMARY;

        animTimer = new javax.swing.Timer(15, e -> animateStep());

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                hovered = true;
                startTransition(UITheme.PRIMARY_HOVER);
                targetScale = 1f;
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                hovered = false;
                startTransition(UITheme.PRIMARY);
                targetScale = 0f;
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                // immediate darker feedback on press
                currentBg = darken(currentBg, 0.12f);
                repaint();
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                // restore to hover/normal target
                startTransition(hovered ? UITheme.PRIMARY_HOVER : UITheme.PRIMARY);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int scaleDelta = Math.round(MAX_SCALE_PX * currentScale);
        int inset = Math.max(1, BASE_INSET - scaleDelta);
        int shadowOffset = 4 - Math.min(2, scaleDelta);
        int w = getWidth() - (inset * 2);
        int h = getHeight() - (inset * 2);
        int arc = BASE_RADIUS + scaleDelta;

        // Soft layered shadow
        g2.setColor(new Color(0, 0, 0, 38));
        g2.fillRoundRect(inset + shadowOffset + 1, inset + shadowOffset + 1, w - 2, h - 2, arc, arc);
        g2.setColor(new Color(0, 0, 0, 58));
        g2.fillRoundRect(inset + shadowOffset, inset + shadowOffset, w, h, arc, arc);

        // Button using animated color and subtle hover scale illusion
        g2.setColor(currentBg != null ? currentBg : getBackground());
        g2.fillRoundRect(inset, inset, w, h, arc, arc);

        super.paintComponent(g);
    }

    private void startTransition(Color to) {
        targetBg = to;
        animStart = System.currentTimeMillis();
        if (!animTimer.isRunning()) animTimer.start();
    }

    private void animateStep() {
        long elapsed = System.currentTimeMillis() - animStart;
        float t = Math.min(1f, (float) elapsed / ANIM_DURATION);
        currentBg = blend(currentBg, targetBg, t);
        currentScale = currentScale + (targetScale - currentScale) * 0.25f;
        repaint();
        if (t >= 1f && Math.abs(currentScale - targetScale) < 0.02f) {
            currentScale = targetScale;
            animTimer.stop();
        }
    }

    private static Color blend(Color a, Color b, float t) {
        if (a == null) return b;
        int r = (int) (a.getRed() + (b.getRed() - a.getRed()) * t);
        int g = (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t);
        int bl = (int) (a.getBlue() + (b.getBlue() - a.getBlue()) * t);
        int alpha = (int) (a.getAlpha() + (b.getAlpha() - a.getAlpha()) * t);
        return new Color(clamp(r), clamp(g), clamp(bl), clamp(alpha));
    }

    private static Color darken(Color c, float factor) {
        int r = (int) (c.getRed() * (1f - factor));
        int g = (int) (c.getGreen() * (1f - factor));
        int b = (int) (c.getBlue() * (1f - factor));
        return new Color(clamp(r), clamp(g), clamp(b), c.getAlpha());
    }

    private static int clamp(int v) {
        return Math.max(0, Math.min(255, v));
    }
}