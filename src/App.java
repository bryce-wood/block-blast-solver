public class App {
    public static void main(String[] args) throws Exception {
        // (0) Get data to process
        // get a screenshot of the game

        // (1) Initialize the board based on what is already there
        // determine where the board is in the screenshot
        // fill the board with the pieces that are already there

        // (2) Retrieve the piece options
        // determine where the pieces are
        // draw out each of the pieces, fitting as far into the top left as possible
        // note that there may be less than three pieces if one of the pieces has already been played

        // (3) Calculate the optimal moves for the three pieces
        // start by determining every possible set of moves for the pieces, if a previous move makes a future piece unplayable, prune it
        // from each set of valid moves, calculate the points earned for each, immediately prune any that are less than the best score so far

        // (4) Await the user doing the moves, then loop
        // have the user enter some input to indicate that they have made the moves
    }
}
