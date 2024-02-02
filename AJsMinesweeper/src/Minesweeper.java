import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class Minesweeper extends JFrame {
    private JButton[][] buttons;
    private boolean[][] mineLocations;
    private boolean[][] revealed;
    private boolean gameover;
    private int rows;
    private int columns;
    private int minesCount;

    public Minesweeper(int rows, int columns, int minesCount) {
        this.rows = rows;
        this.columns = columns;
        this.minesCount = minesCount;

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setTitle("Minesweeper");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        buttons = new JButton[rows][columns];
        mineLocations = new boolean[rows][columns];
        revealed = new boolean[rows][columns];
        gameover = false;

        initializeGame();
        setLayout(new GridLayout(rows, columns));
        createButtons();
    }

    private void initializeGame() {
        Random rand = new Random();
        for (int i = 0; i < minesCount; i++) {
            int row, col;
            do {
                row = rand.nextInt(rows);
                col = rand.nextInt(columns);
            } while (mineLocations[row][col]);
            mineLocations[row][col] = true;
        }
    }

    private void createButtons() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setMargin(new Insets(0, 0, 0, 0));
                buttons[i][j].addMouseListener(new ButtonClickListener(i, j));
                add(buttons[i][j]);
            }
        }
    }

    private class ButtonClickListener extends MouseAdapter {
        private final int row;
        private final int col;

        public ButtonClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (!gameover) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    handleLeftClick(row, col);
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    handleRightClick(row, col);
                }
                checkForWin();
            }
        }
    }

    private void handleLeftClick(int row, int col) {
        if (!revealed[row][col]) {
            if (mineLocations[row][col]) {
                gameOver();
            } else {
                int minesAround = countMinesAround(row, col);
                revealed[row][col] = true;
                buttons[row][col].setEnabled(false);
                buttons[row][col].setText(minesAround > 0 ? String.valueOf(minesAround) : "");
                if (minesAround == 0) {
                    revealEmptyCells(row, col);
                }
            }
        }
    }

    private void handleRightClick(int row, int col) {
        if (!revealed[row][col]) {
            buttons[row][col].setText("F");
        }
    }

    private int countMinesAround(int row, int col) {
        int count = 0;
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (i >= 0 && i < rows && j >= 0 && j < columns && mineLocations[i][j]) {
                    count++;
                }
            }
        }
        return count;
    }

    private void revealEmptyCells(int row, int col) {
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (i >= 0 && i < rows && j >= 0 && j < columns && !revealed[i][j]) {
                    handleLeftClick(i, j);
                }
            }
        }
    }

    private void gameOver() {
        gameover = true;
        GameOverScreen gameOverScreen = new GameOverScreen(this);
        gameOverScreen.setVisible(true);
        dispose();
    }

    private void checkForWin() {
        boolean win = true;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (!revealed[i][j] && !mineLocations[i][j]) {
                    win = false;
                    break;
                }
            }
        }
        if (win) {
            gameover = true;
            int option = JOptionPane.showOptionDialog(this,
                    "Congratulations! You win!",
                    "Game Over",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    new Object[]{"OK"},
                    "OK");
    
            if (option == 0) {
                // User clicked "OK", exit the program
                System.exit(0);
            }
        }
    }
    

    public void restartGame() {
        DifficultySelectionScreen difficultySelectionScreen = new DifficultySelectionScreen();
        difficultySelectionScreen.setVisible(true);
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StartScreen startScreen = new StartScreen();
            startScreen.setVisible(true);
        });
    }
}

class StartScreen extends JFrame {
    public StartScreen() {
        setTitle("Minesweeper");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel headingLabel = new JLabel("Minesweeper!", SwingConstants.CENTER);
        headingLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(headingLabel, BorderLayout.CENTER);

        JLabel instructionLabel = new JLabel("Press any button to continue", SwingConstants.CENTER);
        panel.add(instructionLabel, BorderLayout.SOUTH);

        add(panel);

        addKeyListener(new StartKeyListener(this));
        panel.addMouseListener(new StartMouseListener(this));
    }

    public void showDifficultySelectionScreen() {
        DifficultySelectionScreen difficultySelectionScreen = new DifficultySelectionScreen();
        difficultySelectionScreen.setVisible(true);
        dispose();
    }
}

class DifficultySelectionScreen extends JFrame {
    public DifficultySelectionScreen() {
        setTitle("Choose Difficulty");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        String[] difficulties = {"Easy", "Medium", "Hard"};
        JComboBox<String> difficultyComboBox = new JComboBox<>(difficulties);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.add(difficultyComboBox);

        panel.add(centerPanel, BorderLayout.CENTER);

        JButton startButton = new JButton("Start");
        startButton.addActionListener(new StartButtonListener(this, difficultyComboBox));
        panel.add(startButton, BorderLayout.SOUTH);

        add(panel);
    }

    public void startGame(int rows, int columns, int minesCount) {
        Minesweeper game = new Minesweeper(rows, columns, minesCount);
        game.setVisible(true);
        dispose();
    }
}

class StartKeyListener extends KeyAdapter {
    private final StartScreen startScreen;

    public StartKeyListener(StartScreen startScreen) {
        this.startScreen = startScreen;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        startScreen.showDifficultySelectionScreen();
    }
}

class StartMouseListener extends MouseAdapter {
    private final StartScreen startScreen;

    public StartMouseListener(StartScreen startScreen) {
        this.startScreen = startScreen;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        startScreen.showDifficultySelectionScreen();
    }
}

class StartButtonListener implements ActionListener {
    private final DifficultySelectionScreen difficultySelectionScreen;
    private final JComboBox<String> difficultyComboBox;

    public StartButtonListener(DifficultySelectionScreen difficultySelectionScreen, JComboBox<String> difficultyComboBox) {
        this.difficultySelectionScreen = difficultySelectionScreen;
        this.difficultyComboBox = difficultyComboBox;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String selectedDifficulty = (String) difficultyComboBox.getSelectedItem();
        int rows, columns, minesCount;

        switch (selectedDifficulty) {
            case "Easy":
                rows = 8;
                columns = 8;
                minesCount = 10;
                break;
            case "Medium":
                rows = 14;
                columns = 14;
                minesCount = 40;
                break;
            case "Hard":
                rows = 20;
                columns = 20;
                minesCount = 99;
                break;
            default:
                throw new IllegalArgumentException("Invalid difficulty");
        }

        difficultySelectionScreen.startGame(rows, columns, minesCount);
    }
}

class GameOverScreen extends JFrame {
    public GameOverScreen(Minesweeper game) {
        setTitle("Game Over");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel gameOverLabel = new JLabel("Game Over!", SwingConstants.CENTER);
        gameOverLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(gameOverLabel, BorderLayout.CENTER);

        JButton playAgainButton = new JButton("Play Again");
        JButton exitButton = new JButton("Exit");

        playAgainButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.restartGame();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(playAgainButton);
        buttonPanel.add(exitButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);
    }
}
