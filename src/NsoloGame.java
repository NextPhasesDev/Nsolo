// ============================================
// NsoloGame.java - Main Game Controller
// ============================================
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.Timer;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class NsoloGame extends JFrame {
    private static final int ROWS = 4;
    private static final int COLS = 8;
    private static final int INITIAL_STONES = 2;
    private boolean isAnimating;
    private Timer animationTimer;
    private String gameMode; // "PVP", "AI_EASY", "AI_HARD"
    private NsoloAI ai;
    private JButton muteButton;
    private SoundManager soundManager;

    private int[][] board;
    private char currentPlayer;
    private int capturedA;
    private int capturedB;
    private int moveCount;
    private boolean gameOver;

    StoneCell[][] cellPanels;
    private JLabel statusLabel;
    private JLabel scoreLabel;
    private JLabel movesLabel;
    private JPanel boardPanel;

    public NsoloGame(String mode, JFrame parentMenu) {
        super("Nsolo - Traditional Zambian Board Game");
        this.gameMode = mode;
        setIconImage(loadIcon());

        if (mode.equals("AI_EASY")) {
            this.ai = new NsoloAI(this, "EASY");
        } else if (mode.equals("AI_HARD")) {
            this.ai = new NsoloAI(this, "HARD");
        }

        soundManager = SoundManager.getInstance();
        // Choose music based on game mode
        if (mode.equals("AI_HARD")) {
            soundManager.startBackgroundMusic("ai_hard_music");
        } else if (mode.equals("AI_EASY")) {
            soundManager.startBackgroundMusic("ai_easy_music");
        } else {
            soundManager.startBackgroundMusic("pvp_music");
        }

        initializeGame();
        setupGUI();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setVisible(true);
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
    }

    private void setupGUI() {
        setLayout(new BorderLayout(10, 10));

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BorderLayout());
        titlePanel.setBackground(new Color(139, 69, 19));
        titlePanel.setPreferredSize(new Dimension(1000, 120));

// Title label in center
        JLabel titleLabel = new JLabel("NSOLO - Traditional Zambian Board Game", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

// Mute button in a separate panel on the left
        JPanel mutePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        mutePanel.setBackground(new Color(139, 69, 19));
        mutePanel.setOpaque(false);

        muteButton = new JButton();
        muteButton.setPreferredSize(new Dimension(45, 45));
        muteButton.setFocusPainted(false);
        muteButton.setBorderPainted(false);
        muteButton.setContentAreaFilled(false);
        muteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        muteButton.setToolTipText("Toggle Sound");
        updateMuteIcon();

        muteButton.addActionListener(e -> {
            soundManager.toggleMute();
            updateMuteIcon();
        });

        mutePanel.add(muteButton);
        titlePanel.add(mutePanel, BorderLayout.WEST);

        add(titlePanel, BorderLayout.NORTH);

        // Board Panel
        boardPanel = new JPanel(new GridLayout(ROWS, COLS, 5, 5));
        boardPanel.setBackground(new Color(101, 67, 33));
        boardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        cellPanels = new StoneCell[ROWS][COLS];

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                final int row = i;
                final int col = j;

                // Color coding for territories
                Color territoryColor;
                if (i == 0 || i == 1) {
                    territoryColor = new Color(173, 216, 230); // Light blue for Player B
                } else {
                    territoryColor = new Color(255, 182, 193); // Light pink for Player A
                }

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
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        stoneCell.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    }
                });

                cellPanels[i][j] = stoneCell;
                boardPanel.add(stoneCell);
            }
        }
        add(boardPanel, BorderLayout.CENTER);

        // Info Panel
        JPanel infoPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        infoPanel.setBackground(new Color(245, 222, 179));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        statusLabel = new JLabel("Player A's Turn", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 18));
        statusLabel.setForeground(new Color(139, 0, 0));

        scoreLabel = new JLabel("Score - A: 0 | B: 0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));

        movesLabel = new JLabel("Moves: 0", SwingConstants.CENTER);
        movesLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton resetBtn = new JButton("Reset Game");
        resetBtn.setFont(new Font("Arial", Font.BOLD, 14));
        resetBtn.setBackground(new Color(139, 69, 19));
        resetBtn.setForeground(Color.WHITE);
        resetBtn.addActionListener(e -> resetGame());

        JButton backBtn = new JButton("Back to Menu");
        backBtn.setFont(new Font("Arial", Font.BOLD, 14));
        backBtn.setBackground(new Color(184, 134, 11));
        backBtn.setForeground(Color.WHITE);
        backBtn.addActionListener(e -> {
            soundManager.playSound("click");
            soundManager.stopBackgroundMusic();
            dispose();
            new MainMenu();
        });

        infoPanel.add(statusLabel);
        infoPanel.add(scoreLabel);
        infoPanel.add(movesLabel);
        infoPanel.add(resetBtn);
        infoPanel.add(backBtn);

        add(infoPanel, BorderLayout.SOUTH);

        // Rules Panel
        JPanel rulesPanel = new JPanel();
        rulesPanel.setLayout(new BoxLayout(rulesPanel, BoxLayout.Y_AXIS));
        rulesPanel.setBackground(new Color(255, 248, 220));
        rulesPanel.setBorder(BorderFactory.createTitledBorder("Game Rules"));

        String[] rules = {
                "• Player A (Red): Bottom 2 rows",
                "• Player B (Blue): Top 2 rows",
                "• Click a cell to pick stones",
                "• Stones drop one per cell",
                "• Capture opponent's stones",
                "• Most captures wins!"
        };

        for (String rule : rules) {
            JLabel ruleLabel = new JLabel(rule);
            ruleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            rulesPanel.add(ruleLabel);
        }

        add(rulesPanel, BorderLayout.EAST);
    }

    private void updateMuteIcon() {
        BufferedImage icon = new BufferedImage(45, 45, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = icon.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (soundManager.isMuted()) {
            // RED MUTED ICON
            g2d.setColor(new Color(200, 50, 50));

            // Speaker body (larger, clearer trapezoid)
            int[] speakerX = {8, 8, 18, 18};
            int[] speakerY = {18, 27, 30, 15};
            g2d.fillPolygon(speakerX, speakerY, 4);

            // Speaker cone (triangle pointing right)
            int[] coneX = {18, 18, 28};
            int[] coneY = {15, 30, 27};
            g2d.fillPolygon(coneX, coneY, 3);
            g2d.fillPolygon(new int[]{18, 18, 28}, new int[]{15, 30, 18}, 3);

            // Bold X
            g2d.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.drawLine(30, 12, 40, 33);
            g2d.drawLine(40, 12, 30, 33);

        } else {
            // WHITE UNMUTED ICON
            g2d.setColor(Color.WHITE);

            // Speaker body (larger, clearer trapezoid)
            int[] speakerX = {8, 8, 18, 18};
            int[] speakerY = {18, 27, 30, 15};
            g2d.fillPolygon(speakerX, speakerY, 4);

            // Speaker cone (triangle pointing right)
            int[] coneX = {18, 18, 28};
            int[] coneY = {15, 30, 27};
            g2d.fillPolygon(coneX, coneY, 3);
            g2d.fillPolygon(new int[]{18, 18, 28}, new int[]{15, 30, 18}, 3);

            // Sound waves (3 curved arcs)
            g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.drawArc(26, 16, 8, 13, -40, 80);
            g2d.drawArc(30, 13, 12, 19, -40, 80);
            g2d.drawArc(34, 10, 16, 25, -40, 80);
        }

        g2d.dispose();
        muteButton.setIcon(new ImageIcon(icon));
    }

    private void handleCellClick(int row, int col) {
        if (gameOver) {
            JOptionPane.showMessageDialog(this, "Game is over! Please reset to play again.");
            return;
        }

        if (isAnimating) {
            statusLabel.setText("Please wait for animation to complete...");
            return;
        }

        // Check if it's the player's territory
        if (!isPlayerTerritory(row, currentPlayer)) {
            statusLabel.setText("Invalid move! Select from your territory.");
            soundManager.playSound("invalid");
            return;
        }

        // Check if cell has stones
        if (board[row][col] == 0) {
            statusLabel.setText("Cannot select empty cell!");
            soundManager.playSound("invalid");
            return;
        }

        // Perform the move with animation
        isAnimating = true;
        disableAllCells();
        dropStonesAnimated(row, col);
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

        // Animation timer - drops one stone every 500ms
        animationTimer = new Timer(500, null);
        animationTimer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (stonesInHand[0] > 0) {
                    // Get next cell
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
                    updateBoard();

                    // If last stone and cell has more than 1, pick up all stones
                    if (stonesInHand[0] == 0 && board[currentRow[0]][currentCol[0]] > 1) {
                        stonesInHand[0] = board[currentRow[0]][currentCol[0]];
                        board[currentRow[0]][currentCol[0]] = 0;

                        // Brief pause to show the pickup
                        animationTimer.stop();
                        soundManager.playSound("pickup");
                        Timer pickupTimer = new Timer(300, new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent evt) {
                                updateBoard();
                                soundManager.playSound("drop");
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
        statusLabel.setText("Player " + currentPlayer + "'s Turn");
        movesLabel.setText("Moves: " + moveCount);
        soundManager.playSound("complete");

        isAnimating = false;
        enableAllCells();
        checkGameOver();
        // Trigger AI move if it's AI's turn
        if (!gameOver && gameMode != null && gameMode.startsWith("AI") && currentPlayer == 'B') {
            Timer aiDelayTimer = new Timer(1000, new ActionListener() {
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
    private void highlightCell(int row, int col) {
        // Temporarily highlight the cell being updated
        Color highlight = new Color(255, 255, 0, 100); // Yellow highlight
        Color original = cellPanels[row][col].getBackground();

        cellPanels[row][col].setBorder(BorderFactory.createLineBorder(Color.YELLOW, 4));

        Timer highlightTimer = new Timer(400, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cellPanels[row][col].setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
            }
        });
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
        // Flash captured cells red
        final int flashCount[] = {0};
        Timer flashTimer = new Timer(150, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (flashCount[0] < 4) {
                    if (flashCount[0] % 2 == 0) {
                        cellPanels[row1][col].setTerritoryColor(new Color(255, 0, 0, 150));
                        cellPanels[row2][col].setTerritoryColor(new Color(255, 0, 0, 150));
                    } else {
                        cellPanels[row1][col].setTerritoryColor(Color.LIGHT_GRAY);
                        cellPanels[row2][col].setTerritoryColor(Color.LIGHT_GRAY);
                    }
                    flashCount[0]++;
                } else {
                    ((Timer)e.getSource()).stop();
                }
            }
        });
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
                soundManager.playSound("capture");

                // Flash the captured cells
                flashCapturedCells(0, 1, finalCol);
            } else {
                capturedB += captured;
                statusLabel.setText("Player B captured " + captured + " stones from column " + finalCol + "!");
                soundManager.playSound("capture");

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

                // Update background based on stones
                if (board[i][j] == 0) {
                    cellPanels[i][j].setTerritoryColor(Color.LIGHT_GRAY);
                } else {
                    if (i == 0 || i == 1) {
                        cellPanels[i][j].setTerritoryColor(new Color(173, 216, 230));
                    } else {
                        cellPanels[i][j].setTerritoryColor(new Color(255, 182, 193));
                    }
                }
            }
        }
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
            soundManager.setMusicIntensity(SoundManager.MusicIntensity.INTENSE);
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
            soundManager.stopBackgroundMusic();

            String winner;
            boolean playerAWins = capturedA > capturedB;
            boolean isTie = capturedA == capturedB;

            if (playerAWins) {
                winner = "Player A wins with " + capturedA + " captured stones!";
                // In AI mode, player A is the human
                if (gameMode != null && gameMode.startsWith("AI")) {
                    soundManager.playSound("win");
                } else {
                    soundManager.playSound("win");
                }
            } else if (isTie) {
                winner = "It's a tie! Both players captured " + capturedA + " stones.";
                soundManager.playSound("complete");
            } else {
                winner = "Player B wins with " + capturedB + " captured stones!";
                // In AI mode, player B is the AI
                if (gameMode != null && gameMode.startsWith("AI")) {
                    soundManager.playSound("lose");
                } else {
                    soundManager.playSound("win");
                }
            }

            statusLabel.setText("GAME OVER!");
            JOptionPane.showMessageDialog(this,
                    winner + "\n\nTotal Moves: " + moveCount,
                    "Game Over",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void resetGame() {
        initializeGame();
        updateBoard();
        statusLabel.setText("Player A's Turn");
        scoreLabel.setText("Score - A: 0 | B: 0");
        movesLabel.setText("Moves: 0");
        soundManager.playSound("click");
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
}

