import javax.swing.*;
import java.awt.*;

public class StyledButton extends JButton {

    public StyledButton(String text, float fontScale) {
        super(text);

        setFont(UITheme.getFont(fontScale, Font.BOLD, 18));
        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setForeground(UITheme.TEXT_MAIN);
        setBackground(UITheme.PRIMARY);
        setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        setAlignmentX(Component.CENTER_ALIGNMENT);

        setOpaque(false);
        setContentAreaFilled(false);

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setBackground(UITheme.PRIMARY_HOVER);
                repaint();
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                setBackground(UITheme.PRIMARY);
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Shadow
        g2.setColor(new Color(0, 0, 0, 60));
        g2.fillRoundRect(4, 4, getWidth()-4, getHeight()-4, 20, 20);

        // Button
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth()-4, getHeight()-4, 20, 20);

        super.paintComponent(g);
    }
}