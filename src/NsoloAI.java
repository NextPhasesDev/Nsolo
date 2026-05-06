import java.util.*;

public class NsoloAI {
    private NsoloGame game;
    private String difficulty;
    private Random random;
    private boolean isTopPlayer;

    public NsoloAI(NsoloGame game, String difficulty) {
        this(game, difficulty, true);
    }

    public NsoloAI(NsoloGame game, String difficulty, boolean isTopPlayer) {
        this.game = game;
        this.difficulty = difficulty;
        this.random = new Random();
        this.isTopPlayer = isTopPlayer;
    }

    public void makeMove() {
        if (game.isGameOver() || game.isAnimating() || !game.isInputAllowed()) return;

        int[] move;
        if (difficulty.equals("EASY")) {
            move = getRandomMove();
        } else {
            move = getSmartMove();
        }

        if (move != null) {
            // Simulate click on the chosen cell
            game.cellPanels[move[0]][move[1]].dispatchEvent(
                    new java.awt.event.MouseEvent(
                            game.cellPanels[move[0]][move[1]],
                            java.awt.event.MouseEvent.MOUSE_CLICKED,
                            System.currentTimeMillis(), 0, 0, 0, 1, false
                    )
            );
        }
    }

    private int[] getRandomMove() {
        int[][] board = game.getBoard();
        List<int[]> validMoves = new ArrayList<>();

        int startRow = isTopPlayer ? 0 : 2;
        int endRow = isTopPlayer ? 2 : 4;
        for (int row = startRow; row < endRow; row++) {
            for (int col = 0; col < 8; col++) {
                if (board[row][col] > 0) {
                    validMoves.add(new int[]{row, col});
                }
            }
        }

        if (validMoves.isEmpty()) return null;
        return validMoves.get(random.nextInt(validMoves.size()));
    }

    private int[] getSmartMove() {
        int[][] board = game.getBoard();
        int[] bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        // Evaluate all possible moves
        int startRow = isTopPlayer ? 0 : 2;
        int endRow = isTopPlayer ? 2 : 4;
        for (int row = startRow; row < endRow; row++) {
            for (int col = 0; col < 8; col++) {
                if (board[row][col] > 0) {
                    int score = evaluateMove(row, col, board);
                    if (score > bestScore) {
                        bestScore = score;
                        bestMove = new int[]{row, col};
                    }
                }
            }
        }

        return bestMove != null ? bestMove : getRandomMove();
    }

    private int evaluateMove(int row, int col, int[][] board) {
        // Simulate the move and evaluate the outcome
        int[][] simBoard = copyBoard(board);
        int score = 0;

        int stonesInHand = simBoard[row][col];
        simBoard[row][col] = 0;

        int currentRow = row;
        int currentCol = col;

        // Simulate stone distribution
        while (stonesInHand > 0) {
            int[] next = getNextCell(currentRow, currentCol, simBoard);
            if (next == null) break;

            currentRow = next[0];
            currentCol = next[1];
            simBoard[currentRow][currentCol]++;
            stonesInHand--;

            if (stonesInHand == 0 && simBoard[currentRow][currentCol] > 1) {
                stonesInHand = simBoard[currentRow][currentCol];
                simBoard[currentRow][currentCol] = 0;
            }
        }

        // Evaluate final position
        boolean landedInOwnTerritory = isTopPlayer ? (currentRow == 0 || currentRow == 1) : (currentRow == 2 || currentRow == 3);
        if (landedInOwnTerritory) {
            int captureValue = isTopPlayer
                    ? simBoard[2][currentCol] + simBoard[3][currentCol]
                    : simBoard[0][currentCol] + simBoard[1][currentCol];
            score += captureValue * 10; // Heavy weight on captures
        }

        // Prefer moves with more stones (longer chains)
        score += board[row][col];

        // Prefer moves that end in occupied holes (continue sowing)
        if (simBoard[currentRow][currentCol] > 1) {
            score += 5;
        }

        // Add some randomness to avoid predictability
        score += random.nextInt(3);

        return score;
    }

    private int[] getNextCell(int row, int col, int[][] board) {
        int cols = 8;
        if (isTopPlayer) {
            if (row == 0) {
                if (col > 0) return new int[]{0, col - 1};
                return new int[]{1, 0};
            }
            if (row == 1) {
                if (col < cols - 1) return new int[]{1, col + 1};
                return new int[]{0, cols - 1};
            }
        } else {
            if (row == 3) {
                if (col < cols - 1) return new int[]{3, col + 1};
                return new int[]{2, cols - 1};
            }
            if (row == 2) {
                if (col > 0) return new int[]{2, col - 1};
                return new int[]{3, 0};
            }
        }
        return null;
    }

    private int[][] copyBoard(int[][] board) {
        int[][] copy = new int[board.length][board[0].length];
        for (int i = 0; i < board.length; i++) {
            System.arraycopy(board[i], 0, copy[i], 0, board[i].length);
        }
        return copy;
    }
}