import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.*;

public class MainMenu extends JFrame {
    private AudioManager audioManager;
    private static final String VERSION = "1.1.0";

    // Simple accessibility + localization settings (shared with game windows)
    private static boolean HIGH_CONTRAST = false;
    private static float FONT_SCALE = 1.0f; // 1.0 = normal, 1.2 = large
    private static String LANGUAGE_CODE = "en"; // "en", "bem", "ny", ...

    private JPanel rootPanel;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private JPanel buttonsStack;
    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private JLabel versionLabel;
    private ArcadeButton pvpButton;
    private ArcadeButton aiEasyButton;
    private ArcadeButton aiHardButton;
    private ArcadeButton settingsButton;
    private ArcadeButton exitButton;
    private javax.swing.Timer bgTimer;
    private float gradientPhase = 0f;
    private final java.util.List<Particle> particles = new java.util.ArrayList<>();
    private float fadeAlpha = 0f;
    private javax.swing.Timer fadeTimer;
    private String pendingMode = null;
    private boolean handoffToGame = false;
    private final Runnable languageChangeListener = this::refreshLocalizedText;

    public MainMenu() {
        super(LanguageManager.get("app.title.menu"));
        audioManager = AudioManager.getInstance();
        setIconImage(loadIcon());
        setupGUI();
        LanguageManager.addLanguageChangeListener(languageChangeListener);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 640));
        setSize(1180, 720);
        setLocationRelativeTo(null);
        setVisible(true);
        audioManager.startMusic("menu_music");
    }

    private Image loadIcon() {
        try {
            // Try to load .ico file first
            var iconStream = getClass().getResourceAsStream("/icon.ico");
            if (iconStream != null) {
                return ImageIO.read(iconStream);
            }
            // Fallback to .png
            iconStream = getClass().getResourceAsStream("/icon.png");
            if (iconStream != null) {
                return ImageIO.read(iconStream);
            }
            iconStream = getClass().getResourceAsStream("/resources/icon.ico");
            if (iconStream != null) {
                return ImageIO.read(iconStream);
            }
            iconStream = getClass().getResourceAsStream("/resources/icon.png");
            if (iconStream != null) {
                return ImageIO.read(iconStream);
            }
        } catch (IOException e) {
            System.err.println("Could not load app icon: " + e.getMessage());
        }
        return null;
    }

    public static boolean isHighContrastEnabled() {
        return HIGH_CONTRAST;
    }

    public static float getFontScale() {
        return FONT_SCALE;
    }

    public static String getLanguageCode() {
        return LANGUAGE_CODE;
    }

    private static void setHighContrast(boolean enabled) {
        HIGH_CONTRAST = enabled;
    }

    private static void setFontScale(float scale) {
        FONT_SCALE = scale;
    }

    private static void setLanguageCode(String code) {
        LANGUAGE_CODE = code;
    }

    // Fonts are provided by UITheme.getFont(FONT_SCALE, style, size)

    private void setupGUI() {
        rootPanel = new JPanel(new BorderLayout(24, 20)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                BoardStyle.enableQuality(g2);
                int w = getWidth();
                int h = getHeight();

                // moving vertical gradient
                float offset = (float) Math.sin(gradientPhase) * 0.12f;
                Color a = BoardStyle.mix(BoardStyle.BOARD_BASE, BoardStyle.PLAYER_A, 0.02f + offset);
                Color b = BoardStyle.mix(BoardStyle.BOARD_EDGE, BoardStyle.PLAYER_B, 0.02f - offset);
                GradientPaint gp = new GradientPaint(0, 0, a, w, h, b);
                g2.setPaint(gp);
                g2.fillRect(0, 0, w, h);

                // subtle ambient glow overlay
                RadialGradientPaint rg = new RadialGradientPaint(new Point2D.Float(w * 0.5f, h * 0.35f), Math.max(w, h) * 0.8f,
                        new float[]{0f, 0.6f, 1f}, new Color[]{BoardStyle.withAlpha(BoardStyle.PLAYER_A, 22), BoardStyle.withAlpha(BoardStyle.PLAYER_B, 10), new Color(0, 0, 0, 0)});
                g2.setPaint(rg);
                g2.fillRect(0, 0, w, h);

                // particles
                for (Particle p : particles) {
                    g2.setColor(BoardStyle.withAlpha(p.color, (int) (p.alpha * 255)));
                    int s = Math.max(2, Math.round(p.size));
                    g2.fillOval(Math.round(p.x - s / 2f), Math.round(p.y - s / 2f), s, s);
                }

                // fade overlay
                if (fadeAlpha > 0f) {
                    g2.setColor(BoardStyle.withAlpha(Color.BLACK, (int) (fadeAlpha * 255)));
                    g2.fillRect(0, 0, w, h);
                }
                g2.dispose();
            }
        };
        rootPanel.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));
        rootPanel.setOpaque(true);
        setContentPane(rootPanel);

        leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setOpaque(false);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(34, 34, 34, 28));

        JPanel leftContent = new JPanel();
        leftContent.setOpaque(false);
        leftContent.setLayout(new BoxLayout(leftContent, BoxLayout.Y_AXIS));

        titleLabel = new JLabel(t("menu.title")) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                BoardStyle.enableQuality(g2);
                String text = getText();
                Font f = getFont();
                g2.setFont(f);
                FontMetrics fm = g2.getFontMetrics();
                int w = getWidth();
                int h = getHeight();
                int tx = (w - fm.stringWidth(text)) / 2;
                int ty = (h + fm.getAscent() - fm.getDescent()) / 2;

                // glow
                float pulse = 0.5f + 0.5f * (float) Math.sin(gradientPhase * 1.6f);
                Color glow = BoardStyle.withAlpha(BoardStyle.ACTIVE_BORDER, Math.round(120 + 80 * pulse));
                g2.setColor(BoardStyle.withAlpha(glow, 90));
                g2.setStroke(new BasicStroke(8f));
                g2.drawString(text, tx, ty);

                g2.setColor(BoardStyle.TEXT_PRIMARY);
                g2.drawString(text, tx, ty);
                g2.dispose();
            }
        };
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        subtitleLabel = new JLabel(t("menu.subtitle"));
        subtitleLabel.setForeground(BoardStyle.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        versionLabel = new JLabel(tf("menu.version", VERSION));
        versionLabel.setForeground(BoardStyle.TEXT_SECONDARY);
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftContent.add(Box.createVerticalGlue());
        leftContent.add(titleLabel);
        leftContent.add(Box.createVerticalStrut(16));
        leftContent.add(subtitleLabel);
        leftContent.add(Box.createVerticalStrut(14));
        leftContent.add(versionLabel);
        leftContent.add(Box.createVerticalGlue());
        leftPanel.add(leftContent, new GridBagConstraints());

        rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));

        JPanel rightCard = new JPanel();
        rightCard.setOpaque(false);
        rightCard.setLayout(new BoxLayout(rightCard, BoxLayout.Y_AXIS));

        buttonsStack = new JPanel(new GridLayout(5, 1, 0, 12));
        buttonsStack.setOpaque(false);

        // Player vs Player Button
        pvpButton = createMenuButton(t("menu.pvp"), BoardStyle.PLAYER_A);
        pvpButton.addActionListener(e -> startFadeTransition("PVP"));

        // Player vs AI Easy Button
        aiEasyButton = createMenuButton(t("menu.ai.easy"), BoardStyle.PLAYER_B);
        aiEasyButton.addActionListener(e -> startFadeTransition("AI_EASY"));

        // Player vs AI Hard Button
        aiHardButton = createMenuButton(t("menu.ai.hard"), BoardStyle.PLAYER_B);
        aiHardButton.addActionListener(e -> startFadeTransition("AI_HARD"));

        // Settings / Accessibility & Language Button
        settingsButton = createMenuButton(t("menu.settings"), BoardStyle.ACTIVE_BORDER);
        settingsButton.addActionListener(e -> {
                    audioManager.playSfx("click");
                    showSettingsDialog();
        });

        // Exit Button
        exitButton = createMenuButton(t("menu.exit"), BoardStyle.DANGER);
        exitButton.addActionListener(e -> {
                    audioManager.playSfx("click");
            audioManager.dispose();
                    System.exit(0);
        });

        buttonsStack.add(pvpButton);
        buttonsStack.add(aiEasyButton);
        buttonsStack.add(aiHardButton);
        buttonsStack.add(settingsButton);
        buttonsStack.add(exitButton);

        rightCard.add(Box.createVerticalGlue());
        rightCard.add(buttonsStack);
        rightCard.add(Box.createVerticalGlue());

        rightPanel.add(rightCard, new GridBagConstraints());

        rootPanel.add(leftPanel, BorderLayout.WEST);
        rootPanel.add(rightPanel, BorderLayout.CENTER);

        // Background animation timer: gradient + particles
        bgTimer = new javax.swing.Timer(33, e -> {
            gradientPhase += 0.028f;
            updateParticles();
            rootPanel.repaint();
        });
        initParticles();
        bgTimer.start();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                updateResponsiveMenuSizing();
            }
        });

        updateResponsiveMenuSizing();
    }

    private ArcadeButton createMenuButton(String text) {
        return createMenuButton(text, BoardStyle.PLAYER_A);
    }

    private ArcadeButton createMenuButton(String text, Color baseColor) {
        ArcadeButton btn = new ArcadeButton(text, baseColor, BoardStyle.mix(baseColor, Color.WHITE, 0.25f));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setForeground(BoardStyle.TEXT_PRIMARY);
        btn.setFont(UITheme.getFont(FONT_SCALE, Font.BOLD, 18));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        return btn;
    }

    private void updateResponsiveMenuSizing() {
        if (leftPanel == null || rightPanel == null || buttonsStack == null) {
            return;
        }

        int frameW = Math.max(1, getWidth());
        int frameH = Math.max(1, getHeight());
        float screenScale = Math.min(frameW / 1366f, frameH / 768f);
        float scale = Math.max(0.82f, Math.min(1.12f, screenScale));

        int leftWidth = Math.max(320, Math.min(560, Math.round(frameW * 0.42f)));
        leftPanel.setPreferredSize(new Dimension(leftWidth, 0));

        int titleSize = Math.max(50, Math.min(74, Math.round(66 * scale)));
        int subtitleSize = Math.max(16, Math.min(26, Math.round(21 * scale)));
        int versionSize = Math.max(11, Math.min(14, Math.round(12 * scale)));
        titleLabel.setFont(UITheme.getFont(FONT_SCALE * scale, Font.BOLD, titleSize));
        subtitleLabel.setFont(UITheme.getFont(FONT_SCALE * scale, Font.ITALIC, subtitleSize));
        versionLabel.setFont(UITheme.getFont(FONT_SCALE * scale, Font.PLAIN, versionSize));

        int buttonWidth = Math.max(260, Math.min(520, Math.round(frameW * 0.30f)));
        int buttonHeight = Math.max(54, Math.min(76, Math.round(64 * scale)));
        int buttonFont = Math.max(15, Math.min(20, Math.round(18 * scale)));
        int gap = Math.max(8, Math.min(16, Math.round(12 * scale)));

        GridLayout layout = (GridLayout) buttonsStack.getLayout();
        layout.setVgap(gap);

        Dimension buttonSize = new Dimension(buttonWidth, buttonHeight);
        int totalHeight = buttonHeight * 5 + gap * 4;
        buttonsStack.setPreferredSize(new Dimension(buttonWidth, totalHeight));
        buttonsStack.setMaximumSize(new Dimension(buttonWidth, totalHeight));
        for (ArcadeButton button : new ArcadeButton[]{pvpButton, aiEasyButton, aiHardButton, settingsButton, exitButton}) {
            button.setPreferredSize(buttonSize);
            button.setMaximumSize(new Dimension(Integer.MAX_VALUE, buttonHeight));
            button.setFont(UITheme.getFont(FONT_SCALE * scale, Font.BOLD, buttonFont));
        }

        int rightPadX = Math.max(20, Math.round(28 * scale));
        int rightPadY = Math.max(18, Math.round(24 * scale));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(rightPadY, rightPadX, rightPadY, rightPadX));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(Math.max(24, Math.round(32 * scale)), Math.max(24, Math.round(34 * scale)), Math.max(24, Math.round(32 * scale)), Math.max(24, Math.round(28 * scale))));

        leftPanel.revalidate();
        rightPanel.revalidate();
        rootPanel.revalidate();
        repaint();
    }

    private void showSettingsDialog() {
        JDialog dialog = new JDialog(this, t("menu.settings"), true);
        dialog.setLayout(new BorderLayout(10, 10));

        final float originalMusicVolume = audioManager.getMusicVolume();
        final float originalSfxVolume = audioManager.getSfxVolume();

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Text size
        JPanel textSizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        textSizePanel.add(new JLabel(t("settings.textSize")));
        String[] sizeOptions = {t("settings.size.normal"), t("settings.size.large")};
        JComboBox<String> sizeCombo = new JComboBox<>(sizeOptions);
        sizeCombo.setSelectedIndex(FONT_SCALE > 1.0f ? 1 : 0);
        textSizePanel.add(sizeCombo);

        // High contrast
        JCheckBox highContrastCheck = new JCheckBox(t("settings.highContrast"), HIGH_CONTRAST);

        // Language
        JPanel languagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        languagePanel.add(new JLabel(t("settings.language")));
        String[] langLabels = {t("settings.language.en"), t("settings.language.bem"), t("settings.language.ny")};
        String[] langCodes = {"en", "bem", "ny"};
        JComboBox<String> langCombo = new JComboBox<>(langLabels);
        int initialLangIndex = 0;
        for (int i = 0; i < langCodes.length; i++) {
            if (langCodes[i].equalsIgnoreCase(LANGUAGE_CODE)) {
                initialLangIndex = i;
                break;
            }
        }
        langCombo.setSelectedIndex(initialLangIndex);
        languagePanel.add(langCombo);

        // Volumes
        JPanel volumesPanel = new JPanel();
        volumesPanel.setLayout(new BoxLayout(volumesPanel, BoxLayout.Y_AXIS));
        volumesPanel.setBorder(BorderFactory.createTitledBorder(t("settings.audio")));

        JPanel musicPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        musicPanel.add(new JLabel(t("settings.music")));
        JSlider musicSlider = new JSlider(0, 100, (int) (audioManager.getMusicVolume() * 100));
        musicSlider.setPreferredSize(new Dimension(220, 30));
        musicSlider.setMajorTickSpacing(25);
        musicSlider.setPaintTicks(true);
        musicPanel.add(musicSlider);

        musicSlider.addChangeListener(e -> {
            float newVolume = musicSlider.getValue() / 100f;
            audioManager.previewMusicVolume(newVolume);
        });

        JPanel sfxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sfxPanel.add(new JLabel(t("settings.sfx")));
        JSlider sfxSlider = new JSlider(0, 100, (int) (audioManager.getSfxVolume() * 100));
        sfxSlider.setPreferredSize(new Dimension(220, 30));
        sfxSlider.setMajorTickSpacing(25);
        sfxSlider.setPaintTicks(true);
        sfxPanel.add(sfxSlider);

        sfxSlider.addChangeListener(e -> {
            float newVolume = sfxSlider.getValue() / 100f;
            audioManager.previewSfxVolume(newVolume);
            if (!sfxSlider.getValueIsAdjusting()) {
                audioManager.playSfxPreview();
            }
        });

        volumesPanel.add(musicPanel);
        volumesPanel.add(sfxPanel);

        mainPanel.add(textSizePanel);
        mainPanel.add(highContrastCheck);
        mainPanel.add(languagePanel);
        mainPanel.add(volumesPanel);

        JLabel noteLabel = new JLabel(t("settings.note"));
        noteLabel.setFont(UITheme.getFont(FONT_SCALE, Font.PLAIN, 11));

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton(t("common.ok"));
        okButton.addActionListener(e -> {
            int idx = sizeCombo.getSelectedIndex();
            setFontScale(idx == 1 ? 1.2f : 1.0f);
            setHighContrast(highContrastCheck.isSelected());
            setLanguageCode(langCodes[langCombo.getSelectedIndex()]);
            LanguageManager.load(LANGUAGE_CODE);

            audioManager.setMusicVolume(musicSlider.getValue() / 100f);
            audioManager.setSfxVolume(sfxSlider.getValue() / 100f);
            dialog.dispose();
            refreshLocalizedText();
        });
        JButton cancelButton = new JButton(t("common.cancel"));
        cancelButton.addActionListener(e -> {
            audioManager.previewMusicVolume(originalMusicVolume);
            audioManager.previewSfxVolume(originalSfxVolume);
            dialog.dispose();
        });
        bottomPanel.add(cancelButton);
        bottomPanel.add(okButton);

        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                audioManager.previewMusicVolume(originalMusicVolume);
                audioManager.previewSfxVolume(originalSfxVolume);
            }
        });

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(noteLabel, BorderLayout.NORTH);
        dialog.add(bottomPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // --- Particles & animations ---
    private static final class Particle {
        float x, y, vx, vy, size, alpha;
        Color color;
    }

    private void initParticles() {
        particles.clear();
        int count = Math.max(12, getWidth() / 60);
        Random rnd = new Random(42);
        for (int i = 0; i < count; i++) {
            Particle p = new Particle();
            p.x = rnd.nextFloat() * Math.max(1, getWidth());
            p.y = rnd.nextFloat() * Math.max(1, getHeight());
            p.vx = (rnd.nextFloat() - 0.5f) * 0.6f;
            p.vy = (rnd.nextFloat() - 0.3f) * 0.4f - 0.05f;
            p.size = 2f + rnd.nextFloat() * 6f;
            p.alpha = 0.08f + rnd.nextFloat() * 0.6f;
            p.color = rnd.nextBoolean() ? BoardStyle.PLAYER_A : BoardStyle.PLAYER_B;
            particles.add(p);
        }
    }

    private void updateParticles() {
        int w = Math.max(1, getWidth());
        int h = Math.max(1, getHeight());
        gradientPhase += 0.02f;
        for (Particle p : particles) {
            p.x += p.vx;
            p.y += p.vy;
            p.alpha *= 0.995f;
            if (p.x < -20 || p.x > w + 20 || p.y < -20 || p.y > h + 20 || p.alpha < 0.02f) {
                Random rnd = new Random();
                p.x = rnd.nextFloat() * w;
                p.y = h + 8 + rnd.nextFloat() * 40;
                p.vx = (rnd.nextFloat() - 0.5f) * 0.4f;
                p.vy = -0.6f - rnd.nextFloat() * 0.6f;
                p.size = 2f + rnd.nextFloat() * 6f;
                p.alpha = 0.08f + rnd.nextFloat() * 0.6f;
                p.color = rnd.nextBoolean() ? BoardStyle.PLAYER_A : BoardStyle.PLAYER_B;
            }
        }
    }

    // --- Fade transition to game ---
    private void startFadeTransition(String mode) {
        pendingMode = mode;
        fadeAlpha = 0f;
        if (fadeTimer != null && fadeTimer.isRunning()) fadeTimer.stop();
        fadeTimer = new javax.swing.Timer(16, e -> {
            fadeAlpha = Math.min(1f, fadeAlpha + 0.06f);
            rootPanel.repaint();
            if (fadeAlpha >= 1f) {
                ((javax.swing.Timer) e.getSource()).stop();
                // proceed to game
                audioManager.playSfx("click");
                SwingUtilities.invokeLater(() -> {
                    handoffToGame = true;
                    dispose();
                    new NsoloGame(pendingMode, null);
                });
            }
        });
        fadeTimer.start();
    }

    private void showRules() {
        JTextArea textArea = new JTextArea(t("rules.full"));
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(UITheme.getFont(FONT_SCALE, Font.PLAIN, 14));
        textArea.setBackground(UITheme.TEXT_SECONDARY);

        JPanel rulesPanel = new JPanel(new BorderLayout());
        rulesPanel.setOpaque(false);
        rulesPanel.setPreferredSize(new Dimension(520, 420));
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        rulesPanel.add(textArea, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, rulesPanel, t("menu.rules"), JOptionPane.INFORMATION_MESSAGE);
    }

    private void refreshLocalizedText() {
        setTitle(t("app.title.menu"));
        if (titleLabel != null) titleLabel.setText(t("menu.title"));
        if (subtitleLabel != null) subtitleLabel.setText(t("menu.subtitle"));
        if (versionLabel != null) versionLabel.setText(tf("menu.version", VERSION));
        if (pvpButton != null) pvpButton.setText(t("menu.pvp"));
        if (aiEasyButton != null) aiEasyButton.setText(t("menu.ai.easy"));
        if (aiHardButton != null) aiHardButton.setText(t("menu.ai.hard"));
        if (settingsButton != null) settingsButton.setText(t("menu.settings"));
        if (exitButton != null) exitButton.setText(t("menu.exit"));
        revalidate();
        repaint();
    }

    private String t(String key) {
        return LanguageManager.get(key);
    }

    private String tf(String key, Object... args) {
        return LanguageManager.format(key, args);
    }

    public static void main(String[] args) {
        LanguageManager.load(LANGUAGE_CODE);
        SwingUtilities.invokeLater(() -> new MainMenu());
    }

    @Override
    public void dispose() {
        LanguageManager.removeLanguageChangeListener(languageChangeListener);
        if (bgTimer != null) {
            bgTimer.stop();
        }
        if (!handoffToGame && audioManager != null) {
            audioManager.dispose();
        }
        super.dispose();
    }
}

