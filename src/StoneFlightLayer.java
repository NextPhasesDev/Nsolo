import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
public class StoneFlightLayer extends JComponent {
    private final List<Flight> flights = new ArrayList<>();
    private final Timer timer = new Timer(16, e -> tick());
    private StoneCell[][] cells;
    public StoneFlightLayer() {
        setOpaque(false);
    }
    public void attach(StoneCell[][] cells) {
        this.cells = cells;
    }
    public void animateStone(int fromRow, int fromCol, int toRow, int toCol, Color color) {
        if (cells == null || !isValid(fromRow, fromCol) || !isValid(toRow, toCol)) {
            return;
        }
        Point from = centerFor(fromRow, fromCol);
        Point to = centerFor(toRow, toCol);
        flights.add(new Flight(from.x, from.y, to.x, to.y, color == null ? BoardStyle.PLAYER_A : color));
        if (!timer.isRunning()) {
            timer.start();
        }
        repaint();
    }
    private boolean isValid(int row, int col) {
        return cells != null && row >= 0 && row < cells.length && col >= 0 && col < cells[row].length && cells[row][col] != null;
    }
    private Point centerFor(int row, int col) {
        Rectangle bounds = cells[row][col].getBounds();
        return new Point(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
    }
    private void tick() {
        long now = System.currentTimeMillis();
        Iterator<Flight> it = flights.iterator();
        while (it.hasNext()) {
            Flight flight = it.next();
            if (now - flight.startMs >= flight.durationMs) {
                it.remove();
            }
        }
        repaint();
        if (flights.isEmpty()) {
            timer.stop();
        }
    }
    @Override
    protected void paintComponent(Graphics g) {
        if (flights.isEmpty()) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        BoardStyle.enableQuality(g2);
        long now = System.currentTimeMillis();
        for (Flight flight : flights) {
            float t = Math.min(1f, (now - flight.startMs) / (float) flight.durationMs);
            float eased = easeOutCubic(t);
            float x = lerp(flight.fromX, flight.toX, eased);
            float y = lerp(flight.fromY, flight.toY, eased);
            float trail = Math.max(0f, eased - 0.22f);
            float trailX = lerp(flight.fromX, flight.toX, trail);
            float trailY = lerp(flight.fromY, flight.toY, trail);
            float size = Math.max(14f, Math.min(getWidth(), getHeight()) / 18f);
            float glow = 0.7f + (1f - t) * 0.3f;
            g2.setColor(BoardStyle.withAlpha(Color.BLACK, 55));
            g2.fill(new Ellipse2D.Float(x - size * 0.38f + 3, y - size * 0.38f + 4, size * 0.76f, size * 0.76f));
            g2.setStroke(new BasicStroke(Math.max(2f, size * 0.08f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(BoardStyle.withAlpha(flight.color, Math.round(90 + 80 * glow)));
            g2.drawLine(Math.round(flight.fromX), Math.round(flight.fromY), Math.round(x), Math.round(y));
            g2.setColor(BoardStyle.withAlpha(flight.color, 70));
            g2.drawLine(Math.round(flight.fromX), Math.round(flight.fromY + 1), Math.round(trailX), Math.round(trailY + 1));
            Color glowColor = BoardStyle.mix(flight.color, Color.WHITE, 0.22f);
            BoardStyle.paintGlow(g2, BoardStyle.roundRect(x - size / 2f, y - size / 2f, size, size, size), glowColor, glow);
            GradientPaint paint = new GradientPaint(x - size / 2f, y - size / 2f, BoardStyle.withAlpha(glowColor, 255), x + size / 2f, y + size / 2f, BoardStyle.withAlpha(flight.color.darker(), 255));
            g2.setPaint(paint);
            g2.fill(new Ellipse2D.Float(x - size / 2f, y - size / 2f, size, size));
            g2.setColor(BoardStyle.withAlpha(Color.WHITE, 120));
            g2.fill(new Ellipse2D.Float(x - size / 4f, y - size / 4f, size / 3f, size / 3f));
            g2.setColor(BoardStyle.withAlpha(Color.BLACK, 70));
            g2.draw(new Ellipse2D.Float(x - size / 2f, y - size / 2f, size, size));
        }
        g2.dispose();
    }
    private float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }
    private float easeOutCubic(float t) {
        float inv = 1f - t;
        return 1f - inv * inv * inv;
    }
    private static final class Flight {
        final float fromX;
        final float fromY;
        final float toX;
        final float toY;
        final Color color;
        final long startMs = System.currentTimeMillis();
        final int durationMs = 250;
        private Flight(float fromX, float fromY, float toX, float toY, Color color) {
            this.fromX = fromX;
            this.fromY = fromY;
            this.toX = toX;
            this.toY = toY;
            this.color = color;
        }
    }
}
