// ============================================
// NsoloGame.java - Main Game Controller
// ============================================
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.Timer;
import javax.imageio.ImageIO;
import java.io.IOException;

public class NsoloGame extends JFrame {
    private static final int ROWS = 4;
    private static final int COLS = 8;
    private static final int INITIAL_STONES = 2;
    private boolean isAnimating;
    private boolean isResetting = false;
    private Timer animationTimer;
    private Timer pickupTimer;
    private Timer aiDelayTimer;
    private final java.util.List<Timer> uiTimers = new java.util.ArrayList<>();
    private final String gameMode; // "PVP", "AI_EASY", "AI_HARD"
    private NsoloAI ai;
    private ArcadeButton muteButton;
    private JSlider musicSlider;
    private JSlider sfxSlider;
    private final AudioManager audioManager;

    private int[][] board;
    private char currentPlayer;
    private int capturedA;
    private int capturedB;
    private int moveCount;
    private boolean gameOver;
    private GameState gameState = GameState.PLAYING;
    private final java.util.List<TutorialStep> tutorialSteps = new java.util.ArrayList<>();
    private final java.util.List<JLabel> ruleLabels = new java.util.ArrayList<>();
    private int tutorialStepIndex = -1;
    private boolean tutorialActive = false;

    StoneCell[][] cellPanels;
    private JLabel gameTitleLabel;
    private JLabel statusLabel;
    private JLabel scoreLabel;
    private JLabel movesLabel;
    private JLabel turnIndicatorLabel;
    private JLabel musicTextLabel;
    private JLabel sfxTextLabel;
    private JLabel rulesTitleLabel;
    private ArcadeButton resetBtn;
    private ArcadeButton backBtn;
    private ArcadeButton tutorialBtn;
    private JPanel boardPanel;
    private JPanel boardViewport;
    private JLabel tutorialBubbleLabel;
    private boolean tutorialAwaitingMoveCompletion = false;

    private static final int BOARD_GAP = BoardStyle.BOARD_GAP;
    private static final int BOARD_MIN_CELL = BoardStyle.BOARD_MIN_CELL;
    private static final int BOARD_MAX_PADDING = BoardStyle.BOARD_MAX_PADDING;
    private long turnTransitionUntil = 0L;
    private StoneFlightLayer stoneFlightLayer;

    public NsoloGame(String mode, JFrame parentMenu) {
        super("Nsolo - Traditional Zambian Board Game");
        this.gameMode = mode;
        setIconImage(loadIcon());

        if (mode.equals("AI_EASY")) {
            this.ai = new NsoloAI(this, "EASY");
        } else if (mode.equals("AI_HARD")) {
            this.ai = new NsoloAI(this, "HARD");
        }

        audioManager = AudioManager.getInstance();
        // Choose music based on game mode
        if (mode.equals("AI_HARD")) {
            audioManager.startMusic("ai_hard_music");
        } else if (mode.equals("AI_EASY")) {
            audioManager.startMusic("ai_easy_music");
        } else {
            audioManager.startMusic("pvp_music");
        }

        initializeGame();
        setupGUI();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1024, 720));
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setVisible(true);
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

    private void initializeGame() {
        board = new int[ROWS][COLS];
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                board[i][j] = INITIAL_STONES;
            }
        }
        currentPlayer = 'A';
        capturedA = 0;
        capturedB = 0;
        moveCount = 0;
        gameOver = false;
        isAnimating = false;
        gameState = GameState.PLAYING;
        tutorialActive = false;
        tutorialStepIndex = -1;
    }

    private void setupGUI() {
        float fontScale = MainMenu.getFontScale();

        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(BoardStyle.APP_BACKGROUND);

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BorderLayout());
        titlePanel.setBackground(BoardStyle.BOARD_EDGE);
        titlePanel.setPreferredSize(new Dimension(1000, 104));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        gameTitleLabel = new JLabel(LanguageManager.t("game.title"), SwingConstants.CENTER);
        gameTitleLabel.setFont(scaledFont("Segoe UI", Font.BOLD, 30, fontScale));
        gameTitleLabel.setForeground(BoardStyle.TEXT_PRIMARY);
        titlePanel.add(gameTitleLabel, BorderLayout.CENTER);

        JPanel audioPanel = new JPanel();
        audioPanel.setLayout(new BoxLayout(audioPanel, BoxLayout.Y_AXIS));
        audioPanel.setOpaque(false);

        muteButton = new ArcadeButton(BoardStyle.SURFACE, BoardStyle.ACTIVE_BORDER, ArcadeButton.muteGlyphPainter());
        muteButton.setPreferredSize(new Dimension(45, 45));
        muteButton.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        muteButton.setToolTipText(t("game.audio.muteTooltip"));
        updateMuteIcon();

        muteButton.addActionListener(e -> {
            audioManager.playSfx("click");
            audioManager.toggleMute();
            updateMuteIcon();
        });

        musicSlider = new JSlider(0, 100, (int) (audioManager.getMusicVolume() * 100));
        musicSlider.setOpaque(false);
        musicSlider.setToolTipText(t("game.audio.music"));
        musicSlider.setPreferredSize(new Dimension(120, 22));
        musicSlider.addChangeListener(e -> {
            float v = musicSlider.getValue() / 100f;
            audioManager.previewMusicVolume(v);
            if (!musicSlider.getValueIsAdjusting()) {
                audioManager.setMusicVolume(v);
            }
            updateMuteIcon();
        });

        sfxSlider = new JSlider(0, 100, (int) (audioManager.getSfxVolume() * 100));
        sfxSlider.setOpaque(false);
        sfxSlider.setToolTipText(t("game.audio.sfx"));
        sfxSlider.setPreferredSize(new Dimension(120, 22));
        sfxSlider.addChangeListener(e -> {
            float v = sfxSlider.getValue() / 100f;
            audioManager.previewSfxVolume(v);
            if (!sfxSlider.getValueIsAdjusting()) {
                audioManager.setSfxVolume(v);
                audioManager.playSfxPreview();
            }
        });

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        row1.setOpaque(false);
        row1.add(muteButton);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        row2.setOpaque(false);
        musicTextLabel = new JLabel(t("game.audio.music"));
        musicTextLabel.setForeground(BoardStyle.TEXT_SECONDARY);
        row2.add(musicTextLabel);
        row2.add(musicSlider);

        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        row3.setOpaque(false);
        sfxTextLabel = new JLabel(t("game.audio.sfx"));
        sfxTextLabel.setForeground(BoardStyle.TEXT_SECONDARY);
        row3.add(sfxTextLabel);
        row3.add(sfxSlider);

        audioPanel.add(row1);
        audioPanel.add(row2);
        audioPanel.add(row3);
        titlePanel.add(audioPanel, BorderLayout.WEST);

        add(titlePanel, BorderLayout.NORTH);

        // Board Panel
        boardPanel = new JPanel(new GridLayout(ROWS, COLS, BOARD_GAP, BOARD_GAP)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                BoardStyle.enableQuality(g2);
                int w = getWidth();
                int h = getHeight();
                float pad = Math.max(BoardStyle.BOARD_PADDING, Math.min(w, h) * 0.05f);
                Shape boardShape = BoardStyle.roundRect(pad, pad, w - pad * 2f, h - pad * 2f, 34f);

                BoardStyle.paintLayeredShadow(g2, boardShape, 0.34f);
                BoardStyle.paintGlow(g2, boardShape, BoardStyle.ACTIVE_BORDER, 0.16f);

                GradientPaint boardGradient = new GradientPaint(0, 0, BoardStyle.BOARD_BASE, 0, h, BoardStyle.BOARD_EDGE);
                g2.setPaint(boardGradient);
                g2.fill(boardShape);

                g2.setColor(BoardStyle.withAlpha(BoardStyle.PLAYER_A, 18));
                g2.fill(BoardStyle.roundRect(pad + 8, pad + 8, w / 2f - pad - 12, h - pad * 2f - 16, 30f));
                g2.setColor(BoardStyle.withAlpha(BoardStyle.PLAYER_B, 18));
                g2.fill(BoardStyle.roundRect(w / 2f, pad + 8, w / 2f - pad - 8, h - pad * 2f - 16, 30f));

                if (System.currentTimeMillis() < turnTransitionUntil) {
                    float pulse = 0.5f + 0.5f * (float) Math.sin((System.currentTimeMillis() % 1000L) / 1000f * Math.PI * 2);
                    BoardStyle.paintGlow(g2, boardShape, BoardStyle.ACTIVE_BORDER, 0.35f + pulse * 0.45f);
                }

                g2.setColor(BoardStyle.withAlpha(Color.WHITE, 16));
                g2.setStroke(new BasicStroke(1.2f));
                g2.draw(boardShape);
                g2.dispose();
            }
        };
        boardPanel.setOpaque(false);
        boardPanel.setBorder(BorderFactory.createEmptyBorder());
        cellPanels = new StoneCell[ROWS][COLS];

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                final int row = i;
                final int col = j;

                // Color coding for territories
                Color territoryColor = BoardStyle.teamColorForRow(i);

                // Stone visualization panel (now clickable)
                StoneCell stoneCell = new StoneCell(board[i][j], territoryColor);
                stoneCell.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        handleCellClick(row, col);
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        stoneCell.setCursor(new Cursor(Cursor.HAND_CURSOR));
                        if (!isAnimating && !isResetting && isInputAllowed() && !gameOver && board[row][col] > 0 && isPlayerTerritory(row, currentPlayer)) {
                            stoneCell.setHovered(true);
                        }
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        stoneCell.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        stoneCell.setHovered(false);
                    }
                });

                cellPanels[i][j] = stoneCell;
                boardPanel.add(stoneCell);
            }
        }
        boardViewport = new JPanel(null) {
            @Override
            public void doLayout() {
                super.doLayout();
                layoutBoardViewport();
            }
        };
        boardViewport.setOpaque(false);
        boardViewport.setBorder(BorderFactory.createEmptyBorder());
        boardViewport.add(boardPanel);
        stoneFlightLayer = new StoneFlightLayer();
        stoneFlightLayer.attach(cellPanels);
        boardViewport.add(stoneFlightLayer);
        add(boardViewport, BorderLayout.CENTER);

        // Info Panel
        JPanel infoPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        infoPanel.setBackground(BoardStyle.BOARD_EDGE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        infoPanel.setPreferredSize(new Dimension(0, 128));

        turnIndicatorLabel = new JLabel();
        turnIndicatorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        turnIndicatorLabel.setFont(scaledFont("Segoe UI", Font.BOLD, 17, fontScale));
        turnIndicatorLabel.setForeground(BoardStyle.PLAYER_A);
        updateTurnIndicator();

        statusLabel = new JLabel(tf("game.status.playerTurn", t("game.playerA")), SwingConstants.CENTER);
        statusLabel.setFont(scaledFont("Segoe UI", Font.BOLD, 20, fontScale));
        statusLabel.setForeground(BoardStyle.TEXT_PRIMARY);

        tutorialBubbleLabel = new JLabel(" ");
        tutorialBubbleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        tutorialBubbleLabel.setOpaque(true);
        tutorialBubbleLabel.setBackground(new Color(255, 243, 210));
        tutorialBubbleLabel.setForeground(new Color(43, 37, 25));
        tutorialBubbleLabel.setFont(scaledFont("Segoe UI", Font.BOLD, 15, fontScale));
        tutorialBubbleLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(219, 180, 90), 2, true),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)
        ));
        tutorialBubbleLabel.setVisible(false);

        scoreLabel = new JLabel(tf("game.score", 0, 0), SwingConstants.CENTER);
        scoreLabel.setFont(scaledFont("Segoe UI", Font.BOLD, 16, fontScale));
        scoreLabel.setForeground(BoardStyle.TEXT_PRIMARY);

        movesLabel = new JLabel(tf("game.moves", 0), SwingConstants.CENTER);
        movesLabel.setFont(scaledFont("Segoe UI", Font.PLAIN, 14, fontScale));
        movesLabel.setForeground(BoardStyle.TEXT_SECONDARY);

        resetBtn = new ArcadeButton(t("game.button.reset"), BoardStyle.PLAYER_A, BoardStyle.PLAYER_A_GLOW);
        resetBtn.setFont(scaledFont("Segoe UI", Font.BOLD, 14, fontScale));
        resetBtn.addActionListener(e -> resetGame());

        backBtn = new ArcadeButton(t("game.button.back"), BoardStyle.PLAYER_B, BoardStyle.PLAYER_B_GLOW);
        backBtn.setFont(scaledFont("Segoe UI", Font.BOLD, 14, fontScale));
        backBtn.addActionListener(e -> {
            // Silent quit: stop sowing/AI and stop all audio immediately.
            stopAllTimers();
            super.dispose();
            new MainMenu();
        });
        tutorialBtn = new ArcadeButton(t("game.button.tutorial"), BoardStyle.ACTIVE_BORDER, BoardStyle.SUCCESS);
        tutorialBtn.setFont(scaledFont("Segoe UI", Font.BOLD, 14, fontScale));
        tutorialBtn.addActionListener(e -> startTutorial());

        infoPanel.add(statusLabel);
        infoPanel.add(scoreLabel);
        infoPanel.add(movesLabel);
        infoPanel.add(resetBtn);
        infoPanel.add(backBtn);

        add(infoPanel, BorderLayout.SOUTH);

        // Rules Panel
        JPanel rulesPanel = new JPanel();
        rulesPanel.setLayout(new BoxLayout(rulesPanel, BoxLayout.Y_AXIS));
        rulesPanel.setBackground(BoardStyle.BOARD_EDGE);
        rulesPanel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        rulesPanel.setPreferredSize(new Dimension(220, 0));
        rulesPanel.setMinimumSize(new Dimension(180, 0));
        rulesPanel.setMaximumSize(new Dimension(240, Integer.MAX_VALUE));

        rulesTitleLabel = new JLabel(t("game.rules.title"));
        rulesTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rulesTitleLabel.setFont(scaledFont("Segoe UI", Font.BOLD, 16, fontScale));
        rulesTitleLabel.setForeground(BoardStyle.TEXT_PRIMARY);
        rulesPanel.add(rulesTitleLabel);
        rulesPanel.add(Box.createVerticalStrut(12));

        for (int i = 1; i <= 6; i++) {
            JLabel ruleLabel = new JLabel(t("game.rules.line" + i));
            ruleLabel.setFont(scaledFont("Segoe UI", Font.PLAIN, 15, fontScale));
            ruleLabel.setForeground(BoardStyle.TEXT_SECONDARY);
            ruleLabels.add(ruleLabel);
            rulesPanel.add(ruleLabel);
        }

        add(rulesPanel, BorderLayout.EAST);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                layoutBoardViewport();
            }
        });
        layoutBoardViewport();
    }

    private void layoutBoardViewport() {
        if (boardViewport == null || boardPanel == null) {
            return;
        }

        int viewportW = boardViewport.getWidth();
        int viewportH = boardViewport.getHeight();
        if (viewportW <= 0 || viewportH <= 0) {
            return;
        }

        int padding = Math.min(BOARD_MAX_PADDING, Math.max(12, Math.min(viewportW, viewportH) / 18));
        int usableW = Math.max(0, viewportW - padding * 2);
        int usableH = Math.max(0, viewportH - padding * 2);

        int cellW = (usableW - (COLS - 1) * BOARD_GAP) / COLS;
        int cellH = (usableH - (ROWS - 1) * BOARD_GAP) / ROWS;
        int cellSize = Math.max(BOARD_MIN_CELL, Math.min(cellW, cellH));

        int boardW = cellSize * COLS + (COLS - 1) * BOARD_GAP;
        int boardH = cellSize * ROWS + (ROWS - 1) * BOARD_GAP;

        if (boardW > usableW) {
            cellSize = Math.max(BOARD_MIN_CELL, (usableW - (COLS - 1) * BOARD_GAP) / COLS);
            boardW = cellSize * COLS + (COLS - 1) * BOARD_GAP;
        }
        if (boardH > usableH) {
            cellSize = Math.max(BOARD_MIN_CELL, (usableH - (ROWS - 1) * BOARD_GAP) / ROWS);
            boardH = cellSize * ROWS + (ROWS - 1) * BOARD_GAP;
        }

        int x = Math.max(0, (viewportW - boardW) / 2);
        int y = Math.max(0, (viewportH - boardH) / 2);
        boardPanel.setBounds(x, y, boardW, boardH);
        if (stoneFlightLayer != null) {
            stoneFlightLayer.setBounds(x, y, boardW, boardH);
        }
        boardPanel.revalidate();
        boardPanel.repaint();
        if (stoneFlightLayer != null) {
            stoneFlightLayer.repaint();
        }
    }

    private void styleActionButton(JButton button, Color base, float fontScale) {
        button.setFont(scaledFont("Segoe UI", Font.BOLD, 14, fontScale));
        button.setBackground(base);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0, 70), 1, true),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)
        ));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(base.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(base);
            }
        });
    }

    private Font scaledFont(String family, int style, int size, float scale) {
        int finalSize = (int) (size * scale);
        if (finalSize < 10) finalSize = 10;
        return new Font(family, style, finalSize);
    }

    private void updateMuteIcon() {
        if (audioManager.isMuted() || audioManager.getMusicVolume() <= 0.01f) {
            muteButton.setForeground(BoardStyle.DANGER);
        } else {
            muteButton.setForeground(BoardStyle.TEXT_PRIMARY);
        }
        muteButton.repaint();
    }

    
    private String t(String key) {
        return LanguageManager.get(key);
    }

    private String tf(String key, Object... args) {
        return LanguageManager.format(key, args);
    }

    private void handleCellClick(int row, int col) {
        if (isAnimating() || isResetting) return;

        if (tutorialActive) {
            handleTutorialClick(row, col);
            return;
        }
        processMoveSelection(row, col);
    }

    private void processMoveSelection(int row, int col) {
        if (gameOver) {
            JOptionPane.showMessageDialog(this, "Game is over! Please reset to play again.");
            return;
        }

        if (!isInputAllowed() || isAnimating) {
            statusLabel.setText("Please wait for animation to complete...");
            return;
        }

        // Check if it's the player's territory
        if (!isPlayerTerritory(row, currentPlayer)) {
            statusLabel.setText("Invalid move! Select from your territory.");
            audioManager.playSfx("invalid");
            return;
        }

        // Check if cell has stones
        if (board[row][col] == 0) {
            statusLabel.setText("Cannot select empty cell!");
            audioManager.playSfx("invalid");
            return;
        }

        // Perform the move with animation
        isAnimating = true;
        gameState = GameState.ANIMATING;
        disableAllCells();
        dropStonesAnimated(row, col);
    }

    private void stopAllTimers() {
        if (animationTimer != null) {
            animationTimer.stop();
            animationTimer = null;
        }
        if (pickupTimer != null) {
            pickupTimer.stop();
            pickupTimer = null;
        }
        if (aiDelayTimer != null) {
            aiDelayTimer.stop();
            aiDelayTimer = null;
        }
        for (Timer t : uiTimers) {
            if (t != null) t.stop();
        }
        uiTimers.clear();
        clearCellVisualStates();
        isAnimating = false;
        if (!gameOver) {
            gameState = GameState.PLAYING;
        }
    }

    private void clearCellVisualStates() {
        if (cellPanels == null) return;
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (cellPanels[i][j] != null) {
                    cellPanels[i][j].setHovered(false);
                    cellPanels[i][j].setHighlighted(false);
                }
            }
        }
    }

    private boolean isPlayerTerritory(int row, char player) {
        if (player == 'A') return row == 2 || row == 3;
        return row == 0 || row == 1;
    }

    private void dropStonesAnimated(int startRow, int startCol) {
        final int[] stonesInHand = {board[startRow][startCol]};
        board[startRow][startCol] = 0;
        updateBoard();

        final int[] currentRow = {startRow};
        final int[] currentCol = {startCol};
        final char player = currentPlayer;
        final Color stoneFlightColor = player == 'A' ? BoardStyle.PLAYER_A : BoardStyle.PLAYER_B;

        // Animation timer - drops one stone every 500ms
        animationTimer = new Timer(500, null);
        animationTimer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (stonesInHand[0] > 0) {
                    // Get next cell
                    int prevRow = currentRow[0];
                    int prevCol = currentCol[0];
                    int[] next = getNextCell(currentRow[0], currentCol[0], player);
                    if (next == null) {
                        animationTimer.stop();
                        finishMove(currentRow[0], currentCol[0], player);
                        return;
                    }

                    currentRow[0] = next[0];
                    currentCol[0] = next[1];
                    board[currentRow[0]][currentCol[0]]++;
                    stonesInHand[0]--;

                    // Highlight current cell being updated
                    highlightCell(currentRow[0], currentCol[0]);
                    if (stoneFlightLayer != null) {
                        stoneFlightLayer.animateStone(prevRow, prevCol, currentRow[0], currentCol[0], stoneFlightColor);
                    }
                    updateBoard();
                    audioManager.playSfx("drop");

                    // If last stone and cell has more than 1, pick up all stones
                    if (stonesInHand[0] == 0 && board[currentRow[0]][currentCol[0]] > 1) {
                        stonesInHand[0] = board[currentRow[0]][currentCol[0]];
                        board[currentRow[0]][currentCol[0]] = 0;

                        // Brief pause to show the pickup
                        animationTimer.stop();
                        audioManager.playSfx("pickup");
                        pickupTimer = new Timer(300, new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent evt) {
                                updateBoard();
                                audioManager.playSfx("drop");
                                animationTimer.start();
                            }
                        });
                        pickupTimer.setRepeats(false);
                        pickupTimer.start();
                    }
                } else {
                    // Animation complete
                    animationTimer.stop();
                    finishMove(currentRow[0], currentCol[0], player);
                }
            }
        });
        animationTimer.start();
    }

    private void finishMove(int finalRow, int finalCol, char player) {
        // Check for capture
        checkCapture(finalRow, finalCol);
        updateBoard();

        // Switch player
        moveCount++;
        currentPlayer = (currentPlayer == 'A') ? 'B' : 'A';
        triggerTurnTransition();
        statusLabel.setText("Player " + currentPlayer + "'s Turn");
        updateTurnIndicator();
        movesLabel.setText("Moves: " + moveCount);
        audioManager.playSfx("complete");

        isAnimating = false;
        gameState = GameState.PLAYING;
        enableAllCells();
        checkGameOver();
        if (tutorialAwaitingMoveCompletion) {
            tutorialAwaitingMoveCompletion = false;
            tutorialActive = true;
            gameState = GameState.PAUSED;
            showTutorialStep();
            return;
        }
        // Trigger AI move if it's AI's turn
        if (!gameOver && gameMode != null && gameMode.startsWith("AI") && currentPlayer == 'B') {
            aiDelayTimer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (ai != null && !isAnimating) {
                        ai.makeMove();
                    }
                }
            });
            aiDelayTimer.setRepeats(false);
            aiDelayTimer.start();
        }
    }

    private void triggerTurnTransition() {
        turnTransitionUntil = System.currentTimeMillis() + 320;
        if (boardPanel != null) {
            boardPanel.repaint();
        }
    }
    private void highlightCell(int row, int col) {
        // Temporarily highlight active sowing cell
        if (cellPanels[row][col] == null) {
            return;
        }
        cellPanels[row][col].setHighlighted(true);

        Timer highlightTimer = new Timer(650, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cellPanels[row][col].setHighlighted(false);
            }
        });
        uiTimers.add(highlightTimer);
        highlightTimer.setRepeats(false);
        highlightTimer.start();
    }

    private void disableAllCells() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                cellPanels[i][j].setEnabled(false);
            }
        }
    }

    private void enableAllCells() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                cellPanels[i][j].setEnabled(true);
            }
        }
    }
    private void flashCapturedCells(int row1, int row2, int col) {
        if (cellPanels == null || cellPanels[row1][col] == null || cellPanels[row2][col] == null) {
            return;
        }
        cellPanels[row1][col].setHighlighted(true);
        cellPanels[row2][col].setHighlighted(true);
        Timer flashTimer = new Timer(180, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cellPanels[row1][col].setHighlighted(false);
                cellPanels[row2][col].setHighlighted(false);
                updateBoard();
                ((Timer)e.getSource()).stop();
            }
        });
        uiTimers.add(flashTimer);
        flashTimer.start();
    }

    private int[] getNextCell(int row, int col, char player) {
        if (player == 'A') {
            // Player A: anticlockwise in bottom rows
            // Row 3 (bottom): left to right
            if (row == 3) {
                if (col < COLS - 1) return new int[]{3, col + 1};
                return new int[]{2, COLS - 1}; // Move up to row 2
            }
            // Row 2: right to left
            if (row == 2) {
                if (col > 0) return new int[]{2, col - 1};
                return new int[]{3, 0}; // Move down to row 3
            }
        } else {
            // Player B: anticlockwise in top rows
            // Row 0 (top): right to left
            if (row == 0) {
                if (col > 0) return new int[]{0, col - 1};
                return new int[]{1, 0}; // Move down to row 1
            }
            // Row 1: left to right
            if (row == 1) {
                if (col < COLS - 1) return new int[]{1, col + 1};
                return new int[]{0, COLS - 1}; // Move up to row 0
            }
        }
        return null;
    }

    private void checkCapture(int finalRow, int finalCol) {
        int captured = 0;

        // Capture rule: last stone lands in player's territory, captures opponent's column
        if (currentPlayer == 'A' && (finalRow == 2 || finalRow == 3)) {
            // Player A captures from opponent rows 0 and 1 (same column)
            captured = board[0][finalCol] + board[1][finalCol];
            board[0][finalCol] = 0;
            board[1][finalCol] = 0;
        } else if (currentPlayer == 'B' && (finalRow == 0 || finalRow == 1)) {
            // Player B captures from opponent rows 2 and 3 (same column)
            captured = board[2][finalCol] + board[3][finalCol];
            board[2][finalCol] = 0;
            board[3][finalCol] = 0;
        }

        if (captured > 0) {
            if (currentPlayer == 'A') {
                capturedA += captured;
                statusLabel.setText("Player A captured " + captured + " stones from column " + finalCol + "!");
                audioManager.playSfx("capture");

                // Flash the captured cells
                flashCapturedCells(0, 1, finalCol);
            } else {
                capturedB += captured;
                statusLabel.setText("Player B captured " + captured + " stones from column " + finalCol + "!");
                audioManager.playSfx("capture");

                // Flash the captured cells
                flashCapturedCells(2, 3, finalCol);
            }
            scoreLabel.setText("Score - A: " + capturedA + " | B: " + capturedB);

            // Check for dynamic music intensity (close game detection)
            checkMusicIntensity();
        }
    }
    private void updateBoard() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                cellPanels[i][j].setStoneCount(board[i][j]);
                cellPanels[i][j].setTerritoryColor(board[i][j] == 0 ? BoardStyle.EMPTY_CELL : BoardStyle.teamColorForRow(i));
            }
        }
        updateMuteIcon();
    }

    private void updateTurnIndicator() {
        if (turnIndicatorLabel == null) {
            return;
        }
        Color indicatorColor = currentPlayer == 'A' ? BoardStyle.PLAYER_A : BoardStyle.PLAYER_B;
        turnIndicatorLabel.setText("TURN: " + (currentPlayer == 'A' ? "Player A" : "Player B"));
        turnIndicatorLabel.setForeground(indicatorColor);
    }

    private void checkMusicIntensity() {
        // Count remaining stones on each side
        int stonesA = 0, stonesB = 0;
        for (int j = 0; j < COLS; j++) {
            stonesA += board[2][j] + board[3][j];
            stonesB += board[0][j] + board[1][j];
        }

        int totalRemaining = stonesA + stonesB;
        int scoreDiff = Math.abs(capturedA - capturedB);

        // Switch to intense music if:
        // - Less than 25% stones remain AND score is close (diff <= 5)
        if (totalRemaining <= 16 && scoreDiff <= 5) {
            audioManager.setMusicIntensity(SoundManager.MusicIntensity.INTENSE);
        }
    }

    private void checkGameOver() {
        boolean playerAHasStones = false;
        boolean playerBHasStones = false;

        for (int j = 0; j < COLS; j++) {
            if (board[2][j] > 0 || board[3][j] > 0) playerAHasStones = true;
            if (board[0][j] > 0 || board[1][j] > 0) playerBHasStones = true;
        }

        if (!playerAHasStones || !playerBHasStones) {
            gameOver = true;
            gameState = GameState.GAME_OVER;
            audioManager.stopMusic();

            String winner;
            boolean playerAWins = capturedA > capturedB;
            boolean isTie = capturedA == capturedB;

            if (playerAWins) {
                winner = "Player A wins with " + capturedA + " captured stones!";
                // In AI mode, player A is the human
                    if (gameMode != null && gameMode.startsWith("AI")) {
                    audioManager.playSfx("win");
                } else {
                    audioManager.playSfx("win");
                }
            } else if (isTie) {
                winner = "It's a tie! Both players captured " + capturedA + " stones.";
                audioManager.playSfx("complete");
            } else {
                winner = "Player B wins with " + capturedB + " captured stones!";
                // In AI mode, player B is the AI
                if (gameMode != null && gameMode.startsWith("AI")) {
                    audioManager.playSfx("lose");
                } else {
                    audioManager.playSfx("win");
                }
            }

            statusLabel.setText("GAME OVER!");
            JOptionPane.showMessageDialog(this,
                    winner + "\n\nTotal Moves: " + moveCount,
                    "Game Over",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void resetGame() {
        if (isResetting) return;
        isResetting = true;
        gameState = GameState.PAUSED;

        stopAllAnimations();
        disableAllInput();

        SwingUtilities.invokeLater(() -> {
            initializeGame();
            clearCellVisualStates();
            updateBoard();
            statusLabel.setText("Player A's Turn");
            updateTurnIndicator();
            scoreLabel.setText("Score - A: 0 | B: 0");
            movesLabel.setText("Moves: 0");
            enableAllInput();
            isResetting = false;
        });
    }

    private void stopAllAnimations() {
        stopAllTimers();
    }

    private void disableAllInput() {
        disableAllCells();
    }

    private void enableAllInput() {
        if (!gameOver) {
            gameState = GameState.PLAYING;
        }
        enableAllCells();
    }

    public boolean isInputAllowed() {
        return gameState == GameState.PLAYING;
    }

    private void startTutorial() {
        if (isAnimating() || isResetting || gameOver) {
            return;
        }
        tutorialSteps.clear();
        tutorialSteps.add(new TutorialStep("Tap this pit to start sowing stones.", 3, 0));
        tutorialSteps.add(new TutorialStep("Great move. Now use this pit.", 2, 1));
        tutorialSteps.add(new TutorialStep("Last step: tap here to finish.", 3, 2));
        tutorialActive = true;
        tutorialStepIndex = 0;
        gameState = GameState.PAUSED;
        showTutorialStep();
    }

    private void showTutorialStep() {
        clearCellVisualStates();
        if (!tutorialActive || tutorialStepIndex < 0 || tutorialStepIndex >= tutorialSteps.size()) {
            tutorialActive = false;
            tutorialStepIndex = -1;
            gameState = GameState.PLAYING;
            tutorialBubbleLabel.setVisible(false);
            statusLabel.setText("Tutorial complete. Continue playing!");
            return;
        }
        TutorialStep step = tutorialSteps.get(tutorialStepIndex);
        cellPanels[step.row][step.col].setHighlighted(true);
        statusLabel.setText("Tutorial mode: follow the highlighted pit.");
        tutorialBubbleLabel.setText(step.message + "  (bouncing hint)");
        tutorialBubbleLabel.setVisible(true);
        animateTutorialBubble();
    }

    private void handleTutorialClick(int row, int col) {
        if (!tutorialActive || tutorialStepIndex < 0 || tutorialStepIndex >= tutorialSteps.size()) {
            return;
        }
        TutorialStep step = tutorialSteps.get(tutorialStepIndex);
        if (step.row != row || step.col != col) {
            statusLabel.setText("Follow the highlighted pit for the tutorial.");
            return;
        }
        tutorialStepIndex++;
        tutorialActive = false;
        gameState = GameState.PLAYING;
        tutorialAwaitingMoveCompletion = true;
        processMoveSelection(row, col);
    }

    private void animateTutorialBubble() {
        final int[] ticks = {0};
        Timer bounce = new Timer(110, e -> {
            if (!tutorialActive || !tutorialBubbleLabel.isVisible()) {
                ((Timer) e.getSource()).stop();
                tutorialBubbleLabel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(219, 180, 90), 2, true),
                        BorderFactory.createEmptyBorder(7, 10, 7, 10)
                ));
                return;
            }
            boolean up = (ticks[0] % 2 == 0);
            tutorialBubbleLabel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(219, 180, 90), 2, true),
                    BorderFactory.createEmptyBorder(up ? 5 : 9, 10, up ? 9 : 5, 10)
            ));
            ticks[0]++;
            if (ticks[0] > 8) {
                ((Timer) e.getSource()).stop();
            }
        });
        uiTimers.add(bounce);
        bounce.start();
    }

    // Getter methods for AI
    public int[][] getBoard() {
        return board;
    }

    public char getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isAnimating() {
        return isAnimating;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainMenu());
    }

    @Override
    public void dispose() {
        // Safety: if window is closed during an animation, prevent further board mutations and sounds.
        try {
            stopAllTimers();
            if (audioManager != null) {
                audioManager.stopMusic();
            }
        } catch (Exception ignored) {
            // Best-effort cleanup only.
        }
        super.dispose();
    }
}

