import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import javax.imageio.ImageIO;
import java.io.IOException;

public class MainMenu extends JFrame {
    private SoundManager soundManager;
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
    private StyledButton pvpButton;
    private StyledButton aiEasyButton;
    private StyledButton aiHardButton;
    private StyledButton settingsButton;
    private StyledButton exitButton;
    private Timer glowTimer;
    private float glowPhase = 0f;

    public MainMenu() {
        super("Nsolo - Main Menu");
        setIconImage(loadIcon());
        setupGUI();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 640));
        setSize(1180, 720);
        setLocationRelativeTo(null);
        setVisible(true);
        soundManager = SoundManager.getInstance();
        soundManager.startBackgroundMusic("menu_music");
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
        rootPanel = new JPanel(new BorderLayout(24, 20));
        rootPanel.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));
        rootPanel.setBackground(UITheme.BACKGROUND);
        setContentPane(rootPanel);

        leftPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                GradientPaint base = new GradientPaint(0, 0, UITheme.BACKGROUND, 0, h, UITheme.PANEL.darker());
                g2.setPaint(base);
                g2.fillRect(0, 0, w, h);

                float pulse = 0.5f + 0.5f * (float) Math.sin(glowPhase);
                int glowAlpha = 46 + (int) (36 * pulse);
                int secondaryAlpha = 22 + (int) (16 * (1f - pulse));
                RadialGradientPaint glow = new RadialGradientPaint(
                        new Point2D.Float(w * 0.45f, h * 0.36f),
                        Math.min(w, h) * 0.72f,
                        new float[]{0f, 0.42f, 1f},
                        new Color[]{
                                new Color(UITheme.ACCENT.getRed(), UITheme.ACCENT.getGreen(), UITheme.ACCENT.getBlue(), glowAlpha),
                                new Color(UITheme.PRIMARY.getRed(), UITheme.PRIMARY.getGreen(), UITheme.PRIMARY.getBlue(), secondaryAlpha),
                                new Color(0, 0, 0, 0)
                        }
                );
                g2.setPaint(glow);
                g2.fillRect(0, 0, w, h);

                g2.setColor(new Color(255, 255, 255, 16));
                g2.fillRoundRect(20, 20, w - 40, h - 40, 30, 30);
                g2.dispose();
            }
        };
        leftPanel.setBorder(BorderFactory.createEmptyBorder(34, 34, 34, 28));

        JPanel leftContent = new JPanel();
        leftContent.setOpaque(false);
        leftContent.setLayout(new BoxLayout(leftContent, BoxLayout.Y_AXIS));

        titleLabel = new JLabel(LanguageManager.get("menu.title"));
        titleLabel.setForeground(UITheme.TEXT_MAIN);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        subtitleLabel = new JLabel(LanguageManager.get("menu.subtitle"));
        subtitleLabel.setForeground(UITheme.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        versionLabel = new JLabel("v" + VERSION);
        versionLabel.setForeground(new Color(220, 220, 230));
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
        rightPanel.setOpaque(true);
        rightPanel.setBackground(UITheme.PANEL);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));

        JPanel rightCard = new JPanel();
        rightCard.setOpaque(false);
        rightCard.setLayout(new BoxLayout(rightCard, BoxLayout.Y_AXIS));

        buttonsStack = new JPanel(new GridLayout(5, 1, 0, 12));
        buttonsStack.setOpaque(false);

        // Player vs Player Button
        pvpButton = createMenuButton(LanguageManager.get("menu.pvp"));
        pvpButton.addActionListener(e -> {
            soundManager.playSound("click");
            soundManager.stopBackgroundMusic();
            dispose();
            new NsoloGame("PVP", null);
        });

        // Player vs AI Easy Button
        aiEasyButton = createMenuButton(LanguageManager.get("menu.ai.easy"));
        aiEasyButton.addActionListener(e -> {
            soundManager.playSound("click");
            soundManager.stopBackgroundMusic();
            dispose();
            new NsoloGame("AI_EASY", null);
        });

        // Player vs AI Hard Button
        aiHardButton = createMenuButton(LanguageManager.get("menu.ai.hard"));
        aiHardButton.addActionListener(e -> {
            soundManager.playSound("click");
            soundManager.stopBackgroundMusic();
            dispose();
            new NsoloGame("AI_HARD", null);
        });

        // Settings / Accessibility & Language Button
        settingsButton = createMenuButton(LanguageManager.get("menu.settings"));
        settingsButton.addActionListener(e -> {
            soundManager.playSound("click");
            showSettingsDialog();
        });

        // Exit Button
        exitButton = createMenuButton(LanguageManager.get("menu.exit"));
        exitButton.setBackground(UITheme.ACCENT);
        exitButton.addActionListener(e -> {
            soundManager.playSound("click");
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

        glowTimer = new Timer(33, e -> {
            glowPhase += 0.06f;
            leftPanel.repaint();
        });
        glowTimer.start();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                updateResponsiveMenuSizing();
            }
        });

        updateResponsiveMenuSizing();
    }

    private StyledButton createMenuButton(String text) {
        StyledButton button = new StyledButton(text, FONT_SCALE);
        button.setFont(UITheme.getFont(FONT_SCALE, Font.BOLD, 18));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 2, true),
                BorderFactory.createEmptyBorder(12, 18, 12, 18)
        ));


        return button;
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

        int buttonWidth = Math.max(280, Math.min(420, Math.round(frameW * 0.28f)));
        int buttonHeight = Math.max(54, Math.min(76, Math.round(64 * scale)));
        int buttonFont = Math.max(15, Math.min(20, Math.round(18 * scale)));
        int gap = Math.max(8, Math.min(16, Math.round(12 * scale)));

        GridLayout layout = (GridLayout) buttonsStack.getLayout();
        layout.setVgap(gap);

        Dimension buttonSize = new Dimension(buttonWidth, buttonHeight);
        int totalHeight = buttonHeight * 5 + gap * 4;
        buttonsStack.setPreferredSize(new Dimension(buttonWidth, totalHeight));
        buttonsStack.setMaximumSize(new Dimension(buttonWidth, totalHeight));
        for (StyledButton button : new StyledButton[]{pvpButton, aiEasyButton, aiHardButton, settingsButton, exitButton}) {
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
        JDialog dialog = new JDialog(this, LanguageManager.get("menu.settings"), true);
        dialog.setLayout(new BorderLayout(10, 10));

        final float originalMusicVolume = soundManager.getMusicVolume();
        final float originalSfxVolume = soundManager.getSfxVolume();

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Text size
        JPanel textSizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        textSizePanel.add(new JLabel(LanguageManager.get("settings.textSize")));
        String[] sizeOptions = {LanguageManager.get("settings.size.normal"), LanguageManager.get("settings.size.large")};
        JComboBox<String> sizeCombo = new JComboBox<>(sizeOptions);
        sizeCombo.setSelectedIndex(FONT_SCALE > 1.0f ? 1 : 0);
        textSizePanel.add(sizeCombo);

        // High contrast
        JCheckBox highContrastCheck = new JCheckBox(LanguageManager.get("settings.highContrast"), HIGH_CONTRAST);

        // Language
        JPanel languagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        languagePanel.add(new JLabel(LanguageManager.get("settings.language")));
        String[] langLabels = {"English", "Bemba", "Nyanja"};
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
        volumesPanel.setBorder(BorderFactory.createTitledBorder(LanguageManager.get("settings.audio")));

        JPanel musicPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        musicPanel.add(new JLabel(LanguageManager.get("settings.music")));
        JSlider musicSlider = new JSlider(0, 100, (int) (soundManager.getMusicVolume() * 100));
        musicSlider.setPreferredSize(new Dimension(220, 30));
        musicSlider.setMajorTickSpacing(25);
        musicSlider.setPaintTicks(true);
        musicPanel.add(musicSlider);

        musicSlider.addChangeListener(e -> {
            float newVolume = musicSlider.getValue() / 100f;
            soundManager.previewMusicVolume(newVolume);
        });

        JPanel sfxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sfxPanel.add(new JLabel(LanguageManager.get("settings.sfx")));
        JSlider sfxSlider = new JSlider(0, 100, (int) (soundManager.getSfxVolume() * 100));
        sfxSlider.setPreferredSize(new Dimension(220, 30));
        sfxSlider.setMajorTickSpacing(25);
        sfxSlider.setPaintTicks(true);
        sfxPanel.add(sfxSlider);

        sfxSlider.addChangeListener(e -> {
            float newVolume = sfxSlider.getValue() / 100f;
            soundManager.previewSfxVolume(newVolume);
            if (!sfxSlider.getValueIsAdjusting()) {
                soundManager.playSfxPreview();
            }
        });

        volumesPanel.add(musicPanel);
        volumesPanel.add(sfxPanel);

        mainPanel.add(textSizePanel);
        mainPanel.add(highContrastCheck);
        mainPanel.add(languagePanel);
        mainPanel.add(volumesPanel);

        JLabel noteLabel = new JLabel(LanguageManager.get("settings.note"));
        noteLabel.setFont(UITheme.getFont(FONT_SCALE, Font.PLAIN, 11));

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton(LanguageManager.get("common.ok"));
        okButton.addActionListener(e -> {
            int idx = sizeCombo.getSelectedIndex();
            setFontScale(idx == 1 ? 1.2f : 1.0f);
            setHighContrast(highContrastCheck.isSelected());
            setLanguageCode(langCodes[langCombo.getSelectedIndex()]);
            LanguageManager.load(LANGUAGE_CODE);

            soundManager.setMusicVolume(musicSlider.getValue() / 100f);
            soundManager.setSfxVolume(sfxSlider.getValue() / 100f);
            dialog.dispose();
            refreshLocalizedText();
        });
        JButton cancelButton = new JButton(LanguageManager.get("common.cancel"));
        cancelButton.addActionListener(e -> {
            soundManager.previewMusicVolume(originalMusicVolume);
            soundManager.previewSfxVolume(originalSfxVolume);
            dialog.dispose();
        });
        bottomPanel.add(cancelButton);
        bottomPanel.add(okButton);

        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                soundManager.previewMusicVolume(originalMusicVolume);
                soundManager.previewSfxVolume(originalSfxVolume);
            }
        });

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(noteLabel, BorderLayout.NORTH);
        dialog.add(bottomPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showRules() {
        JTextArea textArea = new JTextArea(LanguageManager.get("rules.full"));
        textArea.setEditable(false);
        textArea.setFont(UITheme.getFont(FONT_SCALE, Font.PLAIN, 14));
        textArea.setBackground(UITheme.TEXT_SECONDARY);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(450, 400));

        JOptionPane.showMessageDialog(this, scrollPane, LanguageManager.get("menu.rules"), JOptionPane.INFORMATION_MESSAGE);
    }

    private void refreshLocalizedText() {
        getContentPane().removeAll();
        setupGUI();
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        LanguageManager.load(LANGUAGE_CODE);
        SwingUtilities.invokeLater(() -> new MainMenu());
    }

    @Override
    public void dispose() {
        if (glowTimer != null) {
            glowTimer.stop();
        }
        super.dispose();
    }
}