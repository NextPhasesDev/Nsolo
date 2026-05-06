import javax.swing.*;
import java.awt.*;
import javax.imageio.ImageIO;
import java.io.IOException;

public class MainMenu extends JFrame {
    private SoundManager soundManager;
    private static final String VERSION = "1.1.0";

    // Simple accessibility + localization settings (shared with game windows)
    private static boolean HIGH_CONTRAST = false;
    private static float FONT_SCALE = 1.0f; // 1.0 = normal, 1.2 = large
    private static String LANGUAGE_CODE = "en"; // "en", "bem", "ny", ...

    public MainMenu() {
        super("Nsolo - Main Menu");
        setIconImage(loadIcon());
        setupGUI();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(760, 620));
        setSize(820, 680);
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

    private Font scaledFont(String family, int style, int size) {
        int finalSize = (int) (size * FONT_SCALE);
        if (finalSize < 10) finalSize = 10;
        return new Font(family, style, finalSize);
    }

    private Font uiFont(int style, int size) {
        return scaledFont("Segoe UI", style, size);
    }

    private void setupGUI() {
        // Root layout: split-screen using BorderLayout
        setLayout(new BorderLayout(12, 12));
        getContentPane().setBackground(Theme.BG);

        // LEFT: Large title block (vertically centered)
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Theme.SURFACE.darker());
        leftPanel.setBorder(BorderFactory.createEmptyBorder(40, 36, 40, 36));

        JPanel leftCenter = new JPanel();
        leftCenter.setLayout(new BoxLayout(leftCenter, BoxLayout.Y_AXIS));
        leftCenter.setOpaque(false);
        leftCenter.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel(LanguageManager.get("menu.title"));
        titleLabel.setFont(uiFont(Font.BOLD, 48));
        titleLabel.setForeground(Theme.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel(LanguageManager.get("menu.subtitle"));
        subtitleLabel.setFont(uiFont(Font.PLAIN, 18));
        subtitleLabel.setForeground(Theme.TEXT_MUTED);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel versionLabel = new JLabel("v" + VERSION);
        versionLabel.setFont(uiFont(Font.PLAIN, 12));
        versionLabel.setForeground(Theme.TEXT_MUTED);
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftCenter.add(Box.createVerticalGlue());
        leftCenter.add(titleLabel);
        leftCenter.add(Box.createVerticalStrut(12));
        leftCenter.add(subtitleLabel);
        leftCenter.add(Box.createVerticalStrut(8));
        leftCenter.add(versionLabel);
        leftCenter.add(Box.createVerticalGlue());

        leftPanel.add(leftCenter, BorderLayout.CENTER);
        add(leftPanel, BorderLayout.WEST);

        // RIGHT: Main menu buttons (centered, evenly spaced)
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Theme.PANEL.brighter());
        rightPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(8, 0, 8, 0);

        // top spacer (pushes buttons to vertical center)
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        rightPanel.add(Box.createVerticalGlue(), gbc);

        // Menu panel (holds buttons stacked)
        JPanel buttonsStack = new JPanel();
        buttonsStack.setLayout(new BoxLayout(buttonsStack, BoxLayout.Y_AXIS));
        buttonsStack.setOpaque(false);
        buttonsStack.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Player vs Player Button
        JButton pvpButton = createMenuButton(LanguageManager.get("menu.pvp"));
        pvpButton.addActionListener(e -> {
            soundManager.playSound("click");
            soundManager.stopBackgroundMusic();
            dispose();
            new NsoloGame("PVP", null);
        });

        // Player vs AI Easy Button
        JButton aiEasyButton = createMenuButton(LanguageManager.get("menu.ai.easy"));
        aiEasyButton.addActionListener(e -> {
            soundManager.playSound("click");
            soundManager.stopBackgroundMusic();
            dispose();
            new NsoloGame("AI_EASY", null);
        });

        // Player vs AI Hard Button
        JButton aiHardButton = createMenuButton(LanguageManager.get("menu.ai.hard"));
        aiHardButton.addActionListener(e -> {
            soundManager.playSound("click");
            soundManager.stopBackgroundMusic();
            dispose();
            new NsoloGame("AI_HARD", null);
        });

        // Rules Button
        JButton rulesButton = createMenuButton(LanguageManager.get("menu.rules"));
        rulesButton.addActionListener(e -> {
            soundManager.playSound("click");
            showRules();
        });

        // Settings / Accessibility & Language Button
        JButton settingsButton = createMenuButton(LanguageManager.get("menu.settings"));
        settingsButton.addActionListener(e -> {
            soundManager.playSound("click");
            showSettingsDialog();
        });

        // Exit Button
        JButton exitButton = createMenuButton(LanguageManager.get("menu.exit"));
        exitButton.setBackground(new Color(139, 69, 19));
        exitButton.addActionListener(e -> {
            soundManager.playSound("click");
            System.exit(0);
        });

        buttonsStack.add(pvpButton);
        buttonsStack.add(Box.createVerticalStrut(12));
        buttonsStack.add(aiEasyButton);
        buttonsStack.add(Box.createVerticalStrut(12));
        buttonsStack.add(aiHardButton);
        buttonsStack.add(Box.createVerticalStrut(12));
        buttonsStack.add(rulesButton);
        buttonsStack.add(Box.createVerticalStrut(12));
        buttonsStack.add(settingsButton);
        buttonsStack.add(Box.createVerticalStrut(12));
        buttonsStack.add(exitButton);

        // Add the stack to the GridBag center area
        gbc.gridy = 1;
        gbc.weighty = 0.0;
        rightPanel.add(buttonsStack, gbc);

        // bottom spacer
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        rightPanel.add(Box.createVerticalGlue(), gbc);

        // Wrap rightPanel in a scrollpane only if needed
        JScrollPane rightScroll = new JScrollPane(rightPanel);
        rightScroll.setBorder(BorderFactory.createEmptyBorder());
        rightScroll.getVerticalScrollBar().setUnitIncrement(16);
        rightScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(rightScroll, BorderLayout.CENTER);
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(uiFont(Font.BOLD, 18));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(300, 50));
        button.setFocusPainted(false);
        button.setBackground(Theme.PRIMARY);
        button.setForeground(Theme.TEXT_ON_PRIMARY);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0, 70), 2, true),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(Theme.PRIMARY_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Theme.PRIMARY);
            }
        });

        return button;
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
        noteLabel.setFont(scaledFont("Arial", Font.PLAIN, 11));

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
        textArea.setFont(scaledFont("Arial", Font.PLAIN, 14));
        textArea.setBackground(new Color(255, 248, 220));

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
}