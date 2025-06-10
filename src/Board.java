import java.util.BitSet;

import org.opencv.core.Mat;

public class Board {
    private int ROWS = 8;
    private int COLS = 8;
    private BitSet board;
    private static final BitSet[] LINE_CLEARS = {
        BitSet.valueOf(new long[]{0xff}), // 8th row
        BitSet.valueOf(new long[]{0xff00}), // 7th row
        BitSet.valueOf(new long[]{0xff0000}), // 6th row
        BitSet.valueOf(new long[]{0xff000000}), // 5th row
        BitSet.valueOf(new long[]{0xff00000000L}), // 4th row
        BitSet.valueOf(new long[]{0xff0000000000L}), // 3rd row
        BitSet.valueOf(new long[]{0xff000000000000L}), // 2nd row
        BitSet.valueOf(new long[]{0xff00000000000000L}), // 1st row
        BitSet.valueOf(new long[]{0x101010101010101L}), // 1st column
        BitSet.valueOf(new long[]{0x202020202020202L}), // 2nd column
        BitSet.valueOf(new long[]{0x404040404040404L}), // 3rd column
        BitSet.valueOf(new long[]{0x808080808080808L}), // 4th column
        BitSet.valueOf(new long[]{0x1010101010101010L}), // 5th column
        BitSet.valueOf(new long[]{0x2020202020202020L}), // 6th column
        BitSet.valueOf(new long[]{0x4040404040404040L}), // 7th column
        BitSet.valueOf(new long[]{0x8080808080808080L}) // 8th column
    };

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

    public int getRows() {
        return ROWS;
    }

    public int getCols() {
        return COLS;
    }

    public Board combine(Board other) {
        BitSet combined = (BitSet) this.board.clone();
        combined.or(other.getBoard());
        return new Board(combined);
    }

    public void detectClears() {
        BitSet clearedLines = new BitSet();
        for (BitSet lineClear : LINE_CLEARS) {
            BitSet checkBoard = (BitSet) board.clone();
            checkBoard.and(lineClear);
            if (checkBoard.equals(lineClear)) {
                clearedLines.or(lineClear);
            }
        }
        if (!clearedLines.isEmpty()) {
            board.andNot(clearedLines);
        }
    }

    public int numFilledCells() {
        return board.cardinality();
    }
}
