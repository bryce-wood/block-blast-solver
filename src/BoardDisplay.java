import javax.swing.*;

import org.opencv.core.Mat;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class BoardDisplay extends JFrame {
    private static final int CELL_SIZE = 30;
    private static final int BOARD_SIZE = 8;
    private Board[] boards;

    public BoardDisplay(Board[] boards) {
        this.boards = boards;
        setTitle("Block Blast Moves");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add the boards with overlaid pieces, shifted left by one
        mainPanel.add(createBoardPanel(boards[6], boards[3], "Move 1")); // Initial board with first piece
        mainPanel.add(createBoardPanel(boards[0], boards[4], "Move 2")); // First move with second piece
        mainPanel.add(createBoardPanel(boards[1], boards[5], "Move 3")); // Second move with third piece

        add(mainPanel);
        pack();
        setLocationRelativeTo(null);

        // Add key listener for space bar
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    restartProcess();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {}
        });

        // Ensure the window is focused to capture key events
        setFocusable(true);
        requestFocusInWindow();
    }

    private JPanel createBoardPanel(Board board, Board overlay, String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));

        JPanel boardPanel = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                JPanel cell = new JPanel();
                cell.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
                cell.setBorder(BorderFactory.createLineBorder(Color.BLACK));

                if (overlay.getCell(r, c)) {
                    // Show piece overlay in green
                    cell.setBackground(Color.GREEN);
                } else if (board.getCell(r, c)) {
                    // Show board state in blue
                    cell.setBackground(Color.BLUE);
                } else {
                    // Empty cell in white
                    cell.setBackground(Color.WHITE);
                }

                boardPanel.add(cell);
            }
        }
        panel.add(boardPanel, BorderLayout.CENTER);
        return panel;
    }

    private void restartProcess() {
        try {
            // Capture a new screenshot
            ScreenCapture capture = new ScreenCapture();
            Mat screenshot = capture.captureScreen();

            if (screenshot == null || screenshot.empty()) {
                JOptionPane.showMessageDialog(this, "Failed to capture screen or convert to Mat", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Initialize the board and pieces
            Board board = new Board(screenshot);
            Piece[] pieces = GridAndPieceDetection.imageToPieces(screenshot);

            // Find best moves and update the GUI
            Board[] newBoards = App.findBestMoves(board, pieces);
            if (newBoards != null) {
                dispose(); // Close the current window
                SwingUtilities.invokeLater(() -> {
                    BoardDisplay newDisplay = new BoardDisplay(newBoards);
                    newDisplay.setVisible(true);
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}