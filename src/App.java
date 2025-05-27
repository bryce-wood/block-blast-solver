import java.util.BitSet;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class App {
    
    static {
        System.load("C:/opencv-4.11.0/build/java/x64/opencv_java4110.dll");
    }
    public static void main(String[] args) throws Exception {
        // (0) Get data to process
        // get a screenshot of the game
        String imagePath = "C:/Users/Bryce/.vscode/Projects/block-blast-solver/src/screenshots/IMG_5924.PNG";
        Mat screenshot = Imgcodecs.imread(imagePath);

        // (1) Initialize the board based on what is already there
        // determine where the board is in the screenshot
        // fill the board with the pieces that are already there

        Board board = new Board(screenshot);
        board.printBoard();

        System.out.println();

        // (2) Retrieve the piece options
        // determine where the pieces are
        // draw out each of the pieces, fitting as far into the top left as possible
        // note that there may be less than three pieces if one of the pieces has already been played

        Piece[] pieces = GridAndPieceDetection.imageToPieces(screenshot);
        for (Piece piece : pieces) {
            piece.printPiece();
            System.out.println();
        }

        // (3) Calculate the optimal moves for the three pieces
        // start by determining every possible set of moves for the pieces, if a previous move makes a future piece unplayable, prune it
        // from each set of valid moves, calculate the points earned for each, immediately prune any that are less than the best score so far

        // (4) Await the user doing the moves, then loop
        // have the user enter some input to indicate that they have made the moves
    }
}