import javax.swing.*;
import java.awt.*;
import javax.imageio.ImageIO;
import java.io.IOException;

public class MainMenu extends JFrame {
    private SoundManager soundManager;
    private static final String VERSION = "1.1.0";

    public MainMenu() {
        super("Nsolo - Main Menu");
        setIconImage(loadIcon());
        setupGUI();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setVisible(true);
        soundManager = SoundManager.getInstance();
        soundManager.startBackgroundMusic("menu_music");
    }

    private Image loadIcon() {
        try {
            // Try to load .ico file first
            var iconStream = getClass().getResourceAsStream("/resources/icon.ico");
            if (iconStream != null) {
                return ImageIO.read(iconStream);
            }
            // Fallback to .png
            iconStream = getClass().getResourceAsStream("/resources/icon.png");
            if (iconStream != null) {
                return ImageIO.read(iconStream);
            }
        } catch (IOException e) {
            System.err.println("Could not load app icon: " + e.getMessage());
        }
        return null;
    }

    private void setupGUI() {
        setLayout(new BorderLayout(10, 10));

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(139, 69, 19));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));

        JLabel titleLabel = new JLabel("NSOLO");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Traditional Zambian Board Game");
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 18));
        subtitleLabel.setForeground(new Color(255, 248, 220));

        JLabel versionLabel = new JLabel("v" + VERSION);
        versionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        versionLabel.setForeground(new Color(255, 248, 220));
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(10));
        titlePanel.add(subtitleLabel);
        titlePanel.add(versionLabel);

        add(titlePanel, BorderLayout.NORTH);

        // Menu Panel
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(new Color(245, 222, 179));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        // Player vs Player Button
        JButton pvpButton = createMenuButton("Player vs Player");
        pvpButton.addActionListener(e -> {
            soundManager.playSound("click");
            soundManager.stopBackgroundMusic();
            dispose();
            new NsoloGame("PVP", null);
        });

        // Player vs AI Easy Button
        JButton aiEasyButton = createMenuButton("vs AI (Easy)");
        aiEasyButton.addActionListener(e -> {
            soundManager.playSound("click");
            soundManager.stopBackgroundMusic();
            dispose();
            new NsoloGame("AI_EASY", null);
        });

        // Player vs AI Hard Button
        JButton aiHardButton = createMenuButton("vs AI (Hard)");
        aiHardButton.addActionListener(e -> {
            soundManager.playSound("click");
            soundManager.stopBackgroundMusic();
            dispose();
            new NsoloGame("AI_HARD", null);
        });

        // Rules Button
        JButton rulesButton = createMenuButton("How to Play");
        rulesButton.addActionListener(e -> {
            soundManager.playSound("click");
            showRules();
        });

        // Exit Button
        JButton exitButton = createMenuButton("Exit");
        exitButton.setBackground(new Color(139, 69, 19));
        exitButton.addActionListener(e -> {
            soundManager.playSound("click");
            System.exit(0);
        });

        menuPanel.add(pvpButton);
        menuPanel.add(Box.createVerticalStrut(15));
        menuPanel.add(aiEasyButton);
        menuPanel.add(Box.createVerticalStrut(15));
        menuPanel.add(aiHardButton);
        menuPanel.add(Box.createVerticalStrut(15));
        menuPanel.add(rulesButton);
        menuPanel.add(Box.createVerticalStrut(15));
        menuPanel.add(exitButton);

        add(menuPanel, BorderLayout.CENTER);
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(300, 50));
        button.setFocusPainted(false);
        button.setBackground(new Color(173, 216, 230));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(135, 206, 250));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(173, 216, 230));
            }
        });

        return button;
    }

    private void showRules() {
        String rules = "HOW TO PLAY NSOLO\n\n" +
                "SETUP:\n" +
                "• 4 rows x 8 columns board\n" +
                "• Player A (Pink): Bottom 2 rows\n" +
                "• Player B (Blue): Top 2 rows\n" +
                "• Each hole starts with 2 stones\n\n" +
                "GAMEPLAY:\n" +
                "• Click a hole in your territory to pick up stones\n" +
                "• Stones are sowed anticlockwise, one per hole\n" +
                "• If last stone lands in a hole with stones, pick them all up and continue\n" +
                "• Movement continues until last stone lands in an empty hole\n\n" +
                "CAPTURING:\n" +
                "• When your last stone lands in your territory,\n" +
                "  you capture ALL stones from both opponent rows in that column\n" +
                "• Captured stones are added to your score\n\n" +
                "WINNING:\n" +
                "• Game ends when one player has no stones left\n" +
                "• Player with most captured stones wins!";

        JTextArea textArea = new JTextArea(rules);
        textArea.setEditable(false);
        textArea.setFont(new Font("Arial", Font.PLAIN, 12));
        textArea.setBackground(new Color(255, 248, 220));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(450, 400));

        JOptionPane.showMessageDialog(this, scrollPane, "How to Play", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainMenu());
    }
}