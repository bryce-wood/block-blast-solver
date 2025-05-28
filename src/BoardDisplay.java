import javax.swing.*;
import java.awt.*;

public class BoardDisplay extends JFrame {
    private static final int CELL_SIZE = 30;
    private static final int BOARD_SIZE = 8;

    public BoardDisplay(Board[] boards) {
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
}