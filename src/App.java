import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class App {
    
    static {
        System.load("C:/opencv-4.11.0/build/java/x64/opencv_java4110.dll");
    }
    public static void main(String[] args) throws Exception {
        // (0) Get data to process
        // get a screenshot of the game
        String imagePath = "C:/Users/Bryce/.vscode/Projects/block-blast-solver/src/screenshots/IMG_5926.PNG";
        Mat screenshot = Imgcodecs.imread(imagePath);

        // (1) Initialize the board based on what is already there
        // determine where the board is in the screenshot
        // fill the board with the pieces that are already there

        Board board = new Board(screenshot);

        // (2) Retrieve the piece options
        // determine where the pieces are
        // draw out each of the pieces, fitting as far into the top left as possible
        // note that there may be less than three pieces if one of the pieces has already been played

        Piece[] pieces = GridAndPieceDetection.imageToPieces(screenshot);

        // (3) Calculate the optimal moves for the three pieces
        // start by determining every possible set of moves for the pieces, if a previous move makes a future piece unplayable, prune it
        // from each set of valid moves, calculate the points earned for each, immediately prune any that are less than the best score so far

        findBestMoves(board, pieces);

        // (4) Await the user doing the moves, then loop
        // have the user enter some input to indicate that they have made the moves
    }

    // returns an array of three boards, each representing the best move for each piece
    public static Board[] findBestMoves(Board board, Piece[] pieces) {
        List<Board[]> allBoards = findAllPossibleMoves(board, pieces);
        if (allBoards.isEmpty()) {
            System.out.println("No valid moves found.");
            return null;
        }
        Board[] firstBoards = allBoards.get(0);
        for (Board board1 : firstBoards) {
            board1.printBoard();
            System.out.println();
        }
        return null;
    }

    public static List<Board[]> findAllPossibleMoves(Board board, Piece[] pieces) {
        List<Board[]> allBoards = new ArrayList<>();
        for (int i = 0; i < pieces.length; i++) {
            // first piece played
            Board[] positions = pieces[i].getValidPositions(board);
            Board[] positionsCopy = new Board[positions.length];
            for (int j = 0; j < positions.length; j++) {
                positionsCopy[j] = new Board(positions[j].getBoard());
            }
            for (int j = 0; j < positions.length; j++) {
                positions[j] = positions[j].combine(board);
                positions[j].detectClears();
            }
            for (int j = 0; j < positions.length; j++) {
                for (int k = 0; k < pieces.length && k != i; k++) {
                    // second piece played
                    Board[] secondPositions = pieces[k].getValidPositions(positions[j]);
                    Board[] secondPositionsCopy = new Board[secondPositions.length];
                    for (int l = 0; l < secondPositions.length; l++) {
                        secondPositionsCopy[l] = new Board(secondPositions[l].getBoard());
                    }
                    for (int l = 0; l < secondPositions.length; l++) {
                        secondPositions[l] = secondPositions[l].combine(positions[j]);
                        secondPositions[l].detectClears();
                    }
                    for (int l = 0; l < secondPositions.length; l++) {
                        for (int m = 0; m < pieces.length && m != i && m != k; m++) {
                            // third piece played
                            Board[] thirdPositions = pieces[m].getValidPositions(secondPositions[l]);
                            Board[] thirdPositionsCopy = new Board[thirdPositions.length];
                            for (int n = 0; n < thirdPositions.length; n++) {
                                thirdPositionsCopy[n] = new Board(thirdPositions[n].getBoard());
                            }
                            for (int n = 0; n < thirdPositions.length; n++) {
                                thirdPositions[n] = thirdPositions[n].combine(secondPositions[l]);
                                thirdPositions[n].detectClears();
                                allBoards.add(new Board[]{positions[j], secondPositions[l], thirdPositions[n], positionsCopy[j], secondPositionsCopy[l], thirdPositionsCopy[n]});
                            }
                        }
                    }
                }
            }
        }
        return allBoards;
    }
}