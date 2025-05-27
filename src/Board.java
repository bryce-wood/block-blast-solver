import java.util.BitSet;

import org.opencv.core.Mat;

public class Board {
    private int ROWS = 8;
    private int COLS = 8;
    private BitSet board;

    public Board(Mat screenshot) {
        board = GridAndPieceDetection.imageToBoard(screenshot);
    }

    public Board(BitSet board) {
        this.board = board;
    }

    public Board() {
        board = new BitSet(ROWS * COLS);
    }

    public Board(int rows, int cols) {
        ROWS = rows;
        COLS = cols;
        // Create a BitSet with rows * cols bits
        board = new BitSet(rows * cols);
    }

    // Convert (row, col) to a BitSet index
    private int getIndex(int row, int col) {
        return row * COLS + col;
    }

    // Set the state of a cell at (row, col) to true
    public void setCell(int row, int col) {
        board.set(getIndex(row, col));
    }

    // Clear the state of a cell at (row, col) (set to false)
    public void clearCell(int row, int col) {
        board.clear(getIndex(row, col));
    }

    // Check the state of a cell at (row, col)
    public boolean getCell(int row, int col) {
        return board.get(getIndex(row, col));
    }

    // Print the board state for visualization
    public void printBoard() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                System.out.print(getCell(r, c) ? "1 " : "0 ");
            }
            System.out.println();
        }
    }

    public BitSet getBoard() {
        return board;
    }
}
