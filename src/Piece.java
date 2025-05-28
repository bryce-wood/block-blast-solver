import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

public class Piece {
    private Board pieceBoard;
    private int height;
    private int width;
    private Board[] positions;

    // takes in a board, and assumes that everything in the board is part of the piece
    public Piece(Board board) {
        // Find the bounds of the piece
        int minRow = Integer.MAX_VALUE, maxRow = Integer.MIN_VALUE;
        int minCol = Integer.MAX_VALUE, maxCol = Integer.MIN_VALUE;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (board.getCell(r, c)) {
                    minRow = Math.min(minRow, r);
                    maxRow = Math.max(maxRow, r);
                    minCol = Math.min(minCol, c);
                    maxCol = Math.max(maxCol, c);
                }
            }
        }

        // Calculate the height and width of the piece
        height = maxRow - minRow + 1;
        width = maxCol - minCol + 1;

        // Create a new board for the piece
        pieceBoard = new Board(height, width);
        for (int r = minRow; r <= maxRow; r++) {
            for (int c = minCol; c <= maxCol; c++) {
                if (board.getCell(r, c)) {
                    pieceBoard.setCell(r - minRow, c - minCol);
                }
            }
        }
    }

    public void printPiece() {
        pieceBoard.printBoard();
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    private Board[] generateAllPiecePositions() {
        int x = 8 - height + 1;
        int y = 8 - width + 1;
        Board[] allPositions = new Board[x * y];

        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                Board newPosition = new Board();
                for (int r = 0; r < height; r++) {
                    for (int c = 0; c < width; c++) {
                        if (pieceBoard.getCell(r, c)) {
                            newPosition.setCell(r + i, c + j);
                        }
                    }
                }
                allPositions[i * y + j] = newPosition;
            }
        }
        positions = allPositions;
        return positions;
    }

    public Board[] getPositions() {
        if (positions == null) {
            positions = generateAllPiecePositions();
        }
        return positions;
    }

    public Board[] getValidPositions(Board board) {
        Board[] allPositions = getPositions();
        List<Board> validPositions = new ArrayList<>();
        for (Board position : allPositions) {
            if (isValidPosition(board, position)) {
                validPositions.add(position);
            }
        }

        return validPositions.toArray(new Board[0]);
    }

    private boolean isValidPosition(Board board, Board position) {
        BitSet bitBoard = board.getBoard();
        BitSet bitPosition = position.getBoard();
        BitSet combined = (BitSet) bitBoard.clone();
        combined.or(bitPosition);

        // (combined NAND position) NAND board should be empty
        // combined NAND position
        combined.xor(bitPosition);
        combined.xor(bitBoard);

        return combined.isEmpty();
    }
}
