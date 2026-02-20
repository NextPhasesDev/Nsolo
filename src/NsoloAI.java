import java.util.*;

public class NsoloAI {
    private NsoloGame game;
    private String difficulty;
    private Random random;

    public NsoloAI(NsoloGame game, String difficulty) {
        this.game = game;
        this.difficulty = difficulty;
        this.random = new Random();
    }

    public void makeMove() {
        if (game.isGameOver() || game.isAnimating()) return;

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

        // Player B's territory is rows 0 and 1
        for (int row = 0; row < 2; row++) {
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
        for (int row = 0; row < 2; row++) {
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
        // Bonus for landing in own territory (potential capture)
        if (currentRow == 0 || currentRow == 1) {
            int captureValue = simBoard[2][currentCol] + simBoard[3][currentCol];
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
        int COLS = 8;
        // Player B: anticlockwise in top rows
        if (row == 0) {
            if (col > 0) return new int[]{0, col - 1};
            return new int[]{1, 0};
        }
        if (row == 1) {
            if (col < COLS - 1) return new int[]{1, col + 1};
            return new int[]{0, COLS - 1};
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