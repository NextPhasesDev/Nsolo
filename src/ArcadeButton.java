import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
public class ArcadeButton extends JButton {
    public interface GlyphPainter {
        void paint(Graphics2D g2, Rectangle area, Color color, boolean hovered, boolean pressed);
    }
    private static final List<ArcadeButton> INSTANCES = new CopyOnWriteArrayList<>();
    private static final Timer ANIMATION_TIMER = new Timer(16, e -> tickAll());
    private final Color baseColor;
    private final Color accentColor;
    private final boolean ghost;
    private final GlyphPainter glyphPainter;
    private float hoverProgress;
    private float pressProgress;
    private boolean hovered;
    private boolean pressed;
    public ArcadeButton(String text, Color baseColor, Color accentColor) {
        this(text, baseColor, accentColor, false, null);
    }
    public ArcadeButton(String text, Color baseColor, Color accentColor, boolean ghost, GlyphPainter glyphPainter) {
        super(text);
        this.baseColor = baseColor;
        this.accentColor = accentColor;
        this.ghost = ghost;
        this.glyphPainter = glyphPainter;
        configure();
    }
    public ArcadeButton(Color baseColor, Color accentColor, GlyphPainter glyphPainter) {
        this("", baseColor, accentColor, true, glyphPainter);
    }
    private void configure() {
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setForeground(BoardStyle.TEXT_PRIMARY);
        setFont(new Font("Segoe UI", Font.BOLD, 14));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                startAnimator();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                pressed = false;
                startAnimator();
            }
            @Override
            public void mousePressed(MouseEvent e) {
                pressed = true;
                startAnimator();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                pressed = false;
                startAnimator();
            }
        });
        INSTANCES.add(this);
        startAnimator();
    }
    private static void startAnimator() {
        if (!ANIMATION_TIMER.isRunning()) {
            ANIMATION_TIMER.start();
        }
    }
    private static void tickAll() {
        boolean needsMore = false;
        for (ArcadeButton button : INSTANCES) {
            float hoverTarget = button.hovered ? 1f : 0f;
            float pressTarget = button.pressed ? 1f : 0f;
            button.hoverProgress += (hoverTarget - button.hoverProgress) * 0.18f;
            button.pressProgress += (pressTarget - button.pressProgress) * 0.24f;
            if (Math.abs(hoverTarget - button.hoverProgress) > 0.01f || Math.abs(pressTarget - button.pressProgress) > 0.01f) {
                needsMore = true;
            }
            button.repaint();
        }
        if (!needsMore) {
            ANIMATION_TIMER.stop();
        }
    }
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        BoardStyle.enableQuality(g2);
        int w = getWidth();
        int h = getHeight();
        float scale = 1f + hoverProgress * 0.035f - pressProgress * 0.02f;
        AffineTransform oldTx = g2.getTransform();
        g2.translate(w / 2.0, h / 2.0);
        g2.scale(scale, scale);
        g2.translate(-w / 2.0, -h / 2.0);
        float arc = BoardStyle.BUTTON_RADIUS;
        Shape shape = BoardStyle.roundRect(4, 4, w - 8, h - 8, arc);
        Color glowColor = hovered ? accentColor : baseColor;
        BoardStyle.paintLayeredShadow(g2, shape, ghost ? 0.22f : 0.34f);
        if (!ghost) {
            BoardStyle.paintGlow(g2, shape, glowColor, 0.55f + hoverProgress * 0.6f);
            GradientPaint basePaint = new GradientPaint(0, 4,
                    BoardStyle.mix(baseColor, Color.WHITE, 0.08f),
                    0, h - 4,
                    BoardStyle.mix(baseColor, Color.BLACK, 0.18f));
            g2.setPaint(basePaint);
            g2.fill(shape);
            g2.setColor(BoardStyle.withAlpha(Color.WHITE, 16));
            g2.fill(BoardStyle.roundRect(8, 7, w - 16, Math.max(8, h / 2.0f - 6), arc));
            g2.setColor(BoardStyle.withAlpha(accentColor, Math.round(40 + hoverProgress * 50)));
            g2.setStroke(new BasicStroke(1.8f));
            g2.draw(shape);
        } else if (hoverProgress > 0f) {
            BoardStyle.paintGlow(g2, shape, accentColor, 0.75f * hoverProgress + 0.15f);
            g2.setColor(BoardStyle.withAlpha(accentColor, Math.round(28 + hoverProgress * 70)));
            g2.setStroke(new BasicStroke(1.6f));
            g2.draw(shape);
        }
        if (pressProgress > 0f) {
            g2.setColor(BoardStyle.withAlpha(BoardStyle.ACTIVE_BORDER, Math.round(45 + pressProgress * 90)));
            g2.fill(shape);
        }
        Rectangle content = new Rectangle(6, 6, w - 12, h - 12);
        if (glyphPainter != null) {
            glyphPainter.paint(g2, content, getForeground(), hovered, pressed || pressProgress > 0.3f);
        } else {
            String text = getText();
            if (text != null && !text.isEmpty()) {
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int tx = (w - fm.stringWidth(text)) / 2;
                int ty = (h + fm.getAscent() - fm.getDescent()) / 2;
                g2.setColor(BoardStyle.withAlpha(Color.BLACK, 80));
                g2.drawString(text, tx + 1, ty + 1);
                g2.setColor(getForeground());
                g2.drawString(text, tx, ty);
            }
        }
        g2.setTransform(oldTx);
        g2.dispose();
        super.paintComponent(g);
    }
    public static GlyphPainter muteGlyphPainter() {
        return (g2, area, color, hovered, pressed) -> {
            BoardStyle.enableQuality(g2);
            float cx = area.x + area.width / 2f;
            float cy = area.y + area.height / 2f;
            float scale = Math.min(area.width, area.height) * 0.42f;
            Color c = color != null ? color : BoardStyle.TEXT_PRIMARY;
            g2.setStroke(new BasicStroke(Math.max(2f, scale * 0.18f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            Path2D speaker = new Path2D.Float();
            speaker.moveTo(cx - scale * 0.95f, cy - scale * 0.35f);
            speaker.lineTo(cx - scale * 0.45f, cy - scale * 0.35f);
            speaker.lineTo(cx - scale * 0.05f, cy - scale * 0.75f);
            speaker.lineTo(cx - scale * 0.05f, cy + scale * 0.75f);
            speaker.lineTo(cx - scale * 0.45f, cy + scale * 0.35f);
            speaker.lineTo(cx - scale * 0.95f, cy + scale * 0.35f);
            speaker.closePath();
            g2.setColor(BoardStyle.withAlpha(Color.BLACK, 70));
            g2.translate(1.5, 1.5);
            g2.fill(speaker);
            g2.translate(-1.5, -1.5);
            g2.setColor(c);
            g2.fill(speaker);
            if (hovered || pressed) {
                g2.setColor(BoardStyle.withAlpha(BoardStyle.ACTIVE_BORDER, pressed ? 180 : 140));
            } else {
                g2.setColor(BoardStyle.withAlpha(c, 200));
            }
            g2.draw(speaker);
            if (c.equals(BoardStyle.DANGER)) {
                g2.drawLine(Math.round(cx - scale * 0.72f), Math.round(cy - scale * 0.72f), Math.round(cx + scale * 0.74f), Math.round(cy + scale * 0.74f));
                g2.drawLine(Math.round(cx - scale * 0.72f), Math.round(cy + scale * 0.72f), Math.round(cx + scale * 0.74f), Math.round(cy - scale * 0.74f));
            } else {
                float wave1 = scale * 0.35f;
                float wave2 = scale * 0.60f;
                g2.setStroke(new BasicStroke(Math.max(2f, scale * 0.14f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawArc(Math.round(cx - wave1), Math.round(cy - wave1), Math.round(wave1 * 2), Math.round(wave1 * 2), -35, 70);
                g2.drawArc(Math.round(cx - wave2), Math.round(cy - wave2), Math.round(wave2 * 2), Math.round(wave2 * 2), -35, 70);
            }
        };
    }
}
