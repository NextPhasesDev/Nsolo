import javax.swing.*;
import java.awt.*;
public class StoneCell extends JPanel {
    private int stoneCount;
    private Color territoryColor;
    private boolean hovered;
    private boolean highlighted;

    public StoneCell(int stones, Color territoryColor) {
        this.stoneCount = stones;
        this.territoryColor = territoryColor;
        setPreferredSize(new Dimension(100, 100));
        setBackground(territoryColor);
        setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        setOpaque(true);
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
        repaint();
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (stoneCount == 0) return;

        int width = getWidth();
        int height = getHeight();
        int stoneSize = 20;

        if (stoneCount <= 6) {
            int cols = Math.min(stoneCount, 3);
            int rows = (stoneCount + 2) / 3;
            int offsetX = (width - (cols * 25)) / 2;
            int offsetY = (height - (rows * 25)) / 2;

            for (int i = 0; i < stoneCount; i++) {
                int row = i / 3;
                int col = i % 3;
                int x = offsetX + col * 25;
                int y = offsetY + row * 25;

                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.fillOval(x + 2, y + 2, stoneSize, stoneSize);

                g2d.setColor(new Color(101, 67, 33));
                g2d.fillOval(x, y, stoneSize, stoneSize);

                g2d.setColor(new Color(150, 100, 50));
                g2d.fillOval(x + 3, y + 3, 8, 8);
            }
        } else {
            for (int i = 0; i < Math.min(stoneCount, 9); i++) {
                int x = width/2 - stoneSize/2 + (i % 3 - 1) * 8;
                int y = height/2 - stoneSize/2 + (i / 3 - 1) * 8;

                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.fillOval(x + 2, y + 2, stoneSize, stoneSize);

                g2d.setColor(new Color(101, 67, 33));
                g2d.fillOval(x, y, stoneSize, stoneSize);
            }

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            FontMetrics fm = g2d.getFontMetrics();
            String countStr = String.valueOf(stoneCount);
            int textX = (width - fm.stringWidth(countStr)) / 2;
            int textY = (height + fm.getAscent()) / 2;

            // Text outline
            g2d.setColor(Color.BLACK);
            g2d.drawString(countStr, textX - 1, textY);
            g2d.drawString(countStr, textX + 1, textY);
            g2d.drawString(countStr, textX, textY - 1);
            g2d.drawString(countStr, textX, textY + 1);

            g2d.setColor(Color.WHITE);
            g2d.drawString(countStr, textX, textY);
        }
    }
}
