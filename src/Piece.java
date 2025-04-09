public class Piece {
    private Board pieceBoard;
    private int height;
    private int width;

    // takes in a board, and assumes that everything in the board is part of the piece
    public Piece(Board board) {
        // Find the bounds of the piece
        int minRow = Integer.MAX_VALUE, maxRow = Integer.MIN_VALUE;
        int minCol = Integer.MAX_VALUE, maxCol = Integer.MIN_VALUE;

        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
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
}
