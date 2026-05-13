import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class StoneCell extends JPanel {
    private static final List<StoneCell> INSTANCES = new CopyOnWriteArrayList<>();
    private static final Timer ANIMATION_TIMER = new Timer(16, e -> tickAll());

    private int stoneCount;
    private Color territoryColor;
    private boolean hovered;
    private boolean highlighted;
    private float hoverProgress;
    private float highlightProgress;
    private final long pulseSeed = System.nanoTime();

    public StoneCell(int stones, Color territoryColor) {
        this.stoneCount = stones;
        this.territoryColor = territoryColor;
        setPreferredSize(new Dimension(100, 100));
        setBackground(territoryColor);
        setBorder(BorderFactory.createEmptyBorder());
        setOpaque(false);
        setDoubleBuffered(true);
        INSTANCES.add(this);
    }

    public void setStoneCount(int count) {
        this.stoneCount = count;
        repaint();
    }

    public void setTerritoryColor(Color color) {
        this.territoryColor = color;
        setBackground(color);
        repaint();
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
        startAnimator();
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
        startAnimator();
    }

    private static void startAnimator() {
        if (!ANIMATION_TIMER.isRunning()) {
            ANIMATION_TIMER.start();
        }
    }

    private static void tickAll() {
        boolean needsMore = false;
        for (StoneCell cell : INSTANCES) {
            float hoverTarget = cell.hovered ? 1f : 0f;
            float highlightTarget = cell.highlighted ? 1f : 0f;
            cell.hoverProgress += (hoverTarget - cell.hoverProgress) * 0.18f;
            cell.highlightProgress += (highlightTarget - cell.highlightProgress) * 0.16f;
            if (Math.abs(hoverTarget - cell.hoverProgress) > 0.01f || Math.abs(highlightTarget - cell.highlightProgress) > 0.01f) {
                needsMore = true;
            }
            cell.repaint();
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
        float scale = 1f + hoverProgress * 0.03f + highlightProgress * 0.008f;
        AffineTransform old = g2.getTransform();
        g2.translate(w / 2.0, h / 2.0);
        g2.scale(scale, scale);
        g2.translate(-w / 2.0, -h / 2.0);

        float pad = Math.max(5f, Math.min(w, h) * 0.07f);
        RoundRectangle2D.Float baseShape = BoardStyle.roundRect(pad, pad, w - pad * 2f, h - pad * 2f, BoardStyle.CELL_RADIUS);

        Color cellBase = stoneCount == 0
                ? BoardStyle.EMPTY_CELL
                : BoardStyle.mix(BoardStyle.EMPTY_CELL, territoryColor, 0.42f + hoverProgress * 0.1f);
        Color glowColor = territoryColor != null ? territoryColor : BoardStyle.EMPTY_CELL;
        float glowStrength = 0.18f + hoverProgress * 0.3f + (stoneCount > 0 ? 0.18f : 0f);

        BoardStyle.paintLayeredShadow(g2, baseShape, 0.18f + hoverProgress * 0.15f);
        BoardStyle.paintGlow(g2, baseShape, glowColor, glowStrength);

        GradientPaint fill = new GradientPaint(0, pad, BoardStyle.mix(cellBase, Color.WHITE, 0.05f), 0, h - pad, BoardStyle.mix(cellBase, Color.BLACK, 0.18f));
        g2.setPaint(fill);
        g2.fill(baseShape);

        g2.setColor(BoardStyle.withAlpha(Color.WHITE, 18));
        g2.fill(BoardStyle.roundRect(pad + 3, pad + 2, w - pad * 2f - 6, Math.max(8, h * 0.34f), BoardStyle.CELL_RADIUS));

        if (highlightProgress > 0f) {
            float pulse = 0.55f + 0.45f * (float) Math.sin((System.nanoTime() - pulseSeed) / 1.15e8);
            Color border = BoardStyle.withAlpha(BoardStyle.ACTIVE_BORDER, Math.round(120 + 110 * pulse));
            BoardStyle.paintGlow(g2, baseShape, BoardStyle.ACTIVE_BORDER, 0.55f + pulse * 0.45f);
            g2.setStroke(new BasicStroke(2.2f + pulse * 1.5f));
            g2.setColor(border);
            g2.draw(baseShape);
        } else if (hoverProgress > 0f) {
            g2.setStroke(new BasicStroke(1.4f));
            g2.setColor(BoardStyle.withAlpha(glowColor, 110));
            g2.draw(baseShape);
        } else {
            g2.setStroke(new BasicStroke(1.1f));
            g2.setColor(BoardStyle.withAlpha(BoardStyle.BOARD_EDGE, 110));
            g2.draw(baseShape);
        }

        if (stoneCount > 0) {
            drawStones(g2, w, h);
        } else {
            drawEmptyMark(g2, w, h);
        }

        g2.setTransform(old);
        g2.dispose();
    }

    private void drawStones(Graphics2D g2, int w, int h) {
        Color stoneColor = territoryColor != null ? territoryColor : BoardStyle.PLAYER_A;
        int stoneSize = Math.max(14, Math.min(w, h) / 5);
        int visible = Math.min(stoneCount, 9);
        int cols = visible <= 3 ? visible : 3;
        int rows = (visible + 2) / 3;
        int stepX = Math.max(stoneSize + 6, (w - stoneSize * cols) / Math.max(1, cols + 1));
        int stepY = Math.max(stoneSize + 6, (h - stoneSize * rows) / Math.max(1, rows + 1));
        int totalWidth = cols * stoneSize + Math.max(0, cols - 1) * Math.min(10, stepX / 3);
        int totalHeight = rows * stoneSize + Math.max(0, rows - 1) * Math.min(10, stepY / 3);
        int startX = (w - totalWidth) / 2;
        int startY = (h - totalHeight) / 2;

        for (int i = 0; i < visible; i++) {
            int row = i / 3;
            int col = i % 3;
            int x = startX + col * (stoneSize + Math.min(10, stepX / 3));
            int y = startY + row * (stoneSize + Math.min(10, stepY / 3));
            paintStone(g2, x, y, stoneSize, stoneColor);
        }

        if (stoneCount > 9) {
            String text = String.valueOf(stoneCount);
            g2.setFont(new Font("Segoe UI", Font.BOLD, Math.max(14, Math.min(w, h) / 5)));
            FontMetrics fm = g2.getFontMetrics();
            int badgeW = fm.stringWidth(text) + 16;
            int badgeH = fm.getHeight() + 6;
            int badgeX = w - badgeW - 12;
            int badgeY = h - badgeH - 12;
            g2.setColor(BoardStyle.withAlpha(Color.BLACK, 95));
            g2.fillRoundRect(badgeX + 2, badgeY + 3, badgeW, badgeH, badgeH, badgeH);
            g2.setColor(BoardStyle.withAlpha(BoardStyle.SURFACE, 210));
            g2.fillRoundRect(badgeX, badgeY, badgeW, badgeH, badgeH, badgeH);
            g2.setColor(BoardStyle.withAlpha(BoardStyle.TEXT_PRIMARY, 240));
            g2.drawString(text, badgeX + (badgeW - fm.stringWidth(text)) / 2, badgeY + fm.getAscent());
        }
    }

    private void paintStone(Graphics2D g2, int x, int y, int size, Color stoneColor) {
        RoundRectangle2D.Float body = BoardStyle.roundRect(x, y, size, size, size);
        BoardStyle.paintLayeredShadow(g2, body, 0.11f);
        Color top = BoardStyle.mix(stoneColor, Color.WHITE, 0.22f);
        Color bottom = BoardStyle.mix(stoneColor, Color.BLACK, 0.2f);
        GradientPaint paint = new GradientPaint(x, y, top, x + size, y + size, bottom);
        g2.setPaint(paint);
        g2.fill(new Ellipse2D.Float(x, y, size, size));
        g2.setColor(BoardStyle.withAlpha(Color.WHITE, 95));
        g2.fill(new Ellipse2D.Float(x + size * 0.18f, y + size * 0.15f, size * 0.35f, size * 0.28f));
        g2.setColor(BoardStyle.withAlpha(Color.BLACK, 90));
        g2.draw(new Ellipse2D.Float(x, y, size, size));
    }

    private void drawEmptyMark(Graphics2D g2, int w, int h) {
        g2.setColor(BoardStyle.withAlpha(Color.WHITE, 28));
        int size = Math.max(8, Math.min(w, h) / 8);
        g2.fillOval(w / 2 - size / 2, h / 2 - size / 2, size, size);
    }
}
