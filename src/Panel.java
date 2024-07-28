import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Panel extends JPanel implements ActionListener {
    private static final short SCREEN_WIDTH = 600;
    private static final short SCREEN_HEIGHT = 700;
    private static final short GRID_SIZE = 3;
    private static final short CELL_SIZE = SCREEN_WIDTH / GRID_SIZE;
    private static GameStatus gameStatus = GameStatus.PLAY;
    private static short[][] board = new short[GRID_SIZE][GRID_SIZE];
    private static short stepCount = 1;
    private static short[] winningCombination = null;
    private int crossWins = 0;
    private int noughtWins = 0;
    private int draws = 0;

    // Create new board with empty grid
    public Panel() {
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addMouseListener(new NewMouseAdapter());

        resetBoard();
    }

    // Drawing grid and cross or nought
    private void draw(Graphics graphics) {
        Graphics2D g2d = (Graphics2D) graphics;
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(5));

        for (short i = 1; i < GRID_SIZE; i++) {
            short linePosition = (short) (i * CELL_SIZE);
            g2d.drawLine(linePosition, 0, linePosition, SCREEN_HEIGHT - 100);
            g2d.drawLine(0, linePosition, SCREEN_WIDTH, linePosition);
        }

        g2d.setStroke(new BasicStroke(15));

        if (gameStatus == GameStatus.PLAY) {
            drawCrossesAndNoughts(graphics);

        } else {
            g2d.setColor(new Color(128, 128, 128));
            drawCrossesAndNoughts(graphics);
            g2d.setColor(Color.WHITE);
            drawWinningCombination(graphics);
            gameOver(graphics);
        }
        drawScore(graphics);
    }

    // Additional function for creating crosses or noughts
    private void drawCrossesAndNoughts(Graphics graphics){
        for (short row = 0; row < GRID_SIZE; row++){
            for (short col = 0; col < GRID_SIZE; col++){

                short x = (short) (col * CELL_SIZE);
                short y = (short) (row * CELL_SIZE);

                if (board[row][col] == 0) {
                    graphics.drawOval(x + 50, y + 50, CELL_SIZE - 100, CELL_SIZE - 100);

                } else if (board[row][col] == 1) {
                    graphics.drawLine(x + 55, y + 55, x + CELL_SIZE - 55, y + CELL_SIZE - 55);
                    graphics.drawLine(x + CELL_SIZE - 55, y + 55, x + 55, y + CELL_SIZE - 55);
                }
            }
        }
    }

    // Highlight the winning combination
    private void drawWinningCombination(Graphics graphics){
        for (short i = 0; i < 3; i++) {
            if (winningCombination != null){
                short row = (short) (winningCombination[i] / 3);
                short col = (short) (winningCombination[i] % 3);
                short x = (short) (col * CELL_SIZE);
                short y = (short) (row * CELL_SIZE);

                if (stepCount % 2 != 0) {
                    graphics.drawOval(x + 50, y + 50, CELL_SIZE - 100, CELL_SIZE - 100);

                } else {
                    graphics.drawLine(x + 55, y + 55, x + CELL_SIZE - 55, y + CELL_SIZE - 55);
                    graphics.drawLine(x + CELL_SIZE - 55, y + 55, x + 55, y + CELL_SIZE - 55);

                }
            }
        }
    }

    // Draw the score of the game
    private void drawScore(Graphics graphics) {
        graphics.setColor(Color.WHITE);
        graphics.setFont(new Font("Arial", Font.TRUETYPE_FONT, 20));
        FontMetrics metrics = getFontMetrics(graphics.getFont());
        graphics.drawString("Cross Wins     Draws     Nought Wins",
                (SCREEN_WIDTH - metrics.stringWidth("Cross Wins     Draws     Nought Wins"))/2, SCREEN_HEIGHT - 60);
        graphics.setFont(new Font("Arial", Font.TRUETYPE_FONT, 30));
        metrics = getFontMetrics(graphics.getFont());
        graphics.drawString(crossWins + "           " + draws + "           " + noughtWins,
                (SCREEN_WIDTH - metrics.stringWidth(crossWins + "           " + draws + "           " + noughtWins))/2, SCREEN_HEIGHT - 20);
    }


    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        draw(graphics);
    }

    // Checking is cell empty
    private boolean checkCanDraw(short row, short col) {
        return row >= 0 && row < GRID_SIZE && col >= 0 && col < GRID_SIZE && board[row][col] == -1;
    }

    // Check if somebody won
    private boolean checkWinner(short row, short col) {
        short player = board[row][col];

        for (short i = 0; i < 3; i++) {
            if (board[i][0] == player && board[i][0] == board[i][1] && board[i][0] == board[i][2]) {
                winningCombination = new short[]{(short) (i * 3), (short) (i * 3 + 1), (short) (i * 3 + 2)};
                return true;
            }

            if (board[0][i] == player && board[0][i] == board[1][i] && board[0][i] == board[2][i]) {
                winningCombination = new short[]{i, (short) (i + 3), (short) (i + 6)};
                return true;
            }
        }

        if (board[0][0] == player && board[0][0] == board[1][1] && board[0][0] == board[2][2]) {
            winningCombination = new short[]{0, 4, 8};
            return true;
        }

        if (board[0][2] == player && board[0][2] == board[1][1] && board[0][2] == board[2][0]) {
            winningCombination = new short[]{2, 4, 6};
            return true;
        }

        return false;
    }

    // Get mouse clicks
    private class NewMouseAdapter extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent mouseEvent) {
            if (gameStatus == GameStatus.PLAY) {

                short col = (short) (mouseEvent.getX() / CELL_SIZE);
                short row = (short) (mouseEvent.getY() / CELL_SIZE);

                if (checkCanDraw(row, col)) {
                    board[row][col] = (short) (stepCount % 2);
                    stepCount++;
                    repaint();

                    boolean getWinner = checkWinner(row, col);
                    if (getWinner && stepCount >= 5) {

                        if (board[row][col] == 0) {
                            gameStatus = GameStatus.NOUGHT;
                            noughtWins++;

                        } else {
                            gameStatus = GameStatus.CROSS;
                            crossWins++;
                        }

                    } else if (!getWinner && stepCount == GRID_SIZE * GRID_SIZE + 1) {
                        gameStatus = GameStatus.DRAW;
                        draws++;
                    }
                }
            } else  {
                if (mouseEvent.getButton() == MouseEvent.BUTTON3) {
                    resetGame();
                }
            }
        }
    }

    // Start new game
    private void resetGame() {
        stepCount = 1;
        gameStatus = GameStatus.PLAY;
        winningCombination = null;

        resetBoard();
        repaint();
    }

    // Message after finishing game
    private void gameOver(Graphics graphics) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    // reset board for new game
    private void resetBoard(){
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                board[i][j] = -1;
            }
        }
    }

    // game status for end game or restart
    private enum GameStatus {
        CROSS,
        NOUGHT,
        DRAW,
        PLAY
    }
}
