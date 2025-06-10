import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.*;

public class GridAndPieceDetection {
    static {
        System.load("C:/opencv-4.11.0/build/java/x64/opencv_java4110.dll");
    }

    public static final double[] EMPTY_COLOR = {62, 23, 18};

    public static BitSet imageToBoard(Mat image) {
        if (image.empty()) { System.out.println("Could not read the image."); return null; }

        Board board = new Board();

        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(gray, gray, new Size(5, 5), 0);
        Mat binary = new Mat();
        Imgproc.adaptiveThreshold(gray, binary, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 11, 2);

        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(binary, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        double maxArea = 0; Rect largestRect = null;
        for (MatOfPoint contour : contours) {
            Rect rect = Imgproc.boundingRect(contour);
            double area = rect.area();
            if (area > maxArea) { maxArea = area; largestRect = rect; }
        }

        if (largestRect != null) {
            Imgproc.rectangle(image, largestRect, new Scalar(0,0,255), 3);

            int cellWidth = largestRect.width / 8, cellHeight = largestRect.height / 8;
            boolean[][] filledGrid = new boolean[8][8];
            double[] emptyColor = EMPTY_COLOR;

            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    int cx = largestRect.x + col * cellWidth + cellWidth / 2;
                    int cy = largestRect.y + row * cellHeight + cellHeight / 2;
                    double[] pixel = image.get(cy, cx);
                    double diff = 0;
                    for (int i = 0; i < 3; i++) diff += Math.abs(pixel[i] - emptyColor[i]);
                    filledGrid[row][col] = diff > 30;
                }
            }

            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (filledGrid[row][col]) {
                        board.setCell(row, col);
                    }
                }
            }
        }

        return board.getBoard();
    }

    public static Piece[] imageToPieces(Mat image) {
        if (image.empty()) {
            System.out.println("Could not read the image.");
            return null;
        }

        // Load block templates from the "block blast images" folder
        String blockImagesPath = "C:/Users/Bryce/.vscode/Projects/block-blast-solver/src/block blast images/";
        File folder = new File(blockImagesPath);
        File[] blockImages = folder.listFiles((dir, name) -> name.endsWith(".png") || name.endsWith(".jpg"));
        if (blockImages == null || blockImages.length == 0) {
            System.out.println("No block images found in the folder.");
            return null;
        }

        List<Mat> blockTemplates = new ArrayList<>();
        for (File blockImage : blockImages) {
            blockTemplates.add(Imgcodecs.imread(blockImage.getAbsolutePath(), Imgcodecs.IMREAD_COLOR));
        }

        // Define the region of interest (ROI) for pieces
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(gray, gray, new Size(5, 5), 0);
        Mat binary = new Mat();
        Imgproc.adaptiveThreshold(gray, binary, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 11, 2);
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(binary, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        double maxArea = 0;
        Rect largestRect = null;
        for (MatOfPoint contour : contours) {
            Rect rect = Imgproc.boundingRect(contour);
            double area = rect.area();
            if (area > maxArea) {
                maxArea = area;
                largestRect = rect;
            }
        }

        if (largestRect == null) {
            System.out.println("No grid detected.");
            return null;
        }

        int piecePanelY = Math.min(largestRect.y + largestRect.height + 10, image.rows() - 1);
        int piecePanelHeight = Math.min(largestRect.height / 2, image.rows() - piecePanelY);
        if (piecePanelHeight <= 0 || piecePanelY >= image.rows()) {
            System.out.println("Piece panel exceeds image bounds.");
            return null;
        }

        Rect piecePanelRect = new Rect(largestRect.x, piecePanelY, largestRect.width, piecePanelHeight);
        Mat piecePanel = new Mat(image, piecePanelRect);

        // Detect blocks using template matching
        List<Rect> detectedBlocks = new ArrayList<>();
        for (Mat blockTemplate : blockTemplates) {
            Mat result = new Mat();
            Imgproc.matchTemplate(piecePanel, blockTemplate, result, Imgproc.TM_CCOEFF_NORMED);

            // Threshold for template matching confidence
            double threshold = 0.9;
            Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
            while (mmr.maxVal >= threshold) {
                Point matchLoc = mmr.maxLoc;
                Rect blockRect = new Rect((int) matchLoc.x, (int) matchLoc.y, blockTemplate.width(), blockTemplate.height());
                detectedBlocks.add(blockRect);

                // Suppress overlapping matches
                Imgproc.rectangle(result, blockRect, new Scalar(0), -1);
                mmr = Core.minMaxLoc(result);
            }
        }

        // Group blocks into pieces based on proximity
        List<List<Rect>> pieces = new ArrayList<>();
        while (!detectedBlocks.isEmpty()) {
            List<Rect> piece = new ArrayList<>();
            piece.add(detectedBlocks.remove(0));

            boolean added;
            do {
                added = false;
                for (int i = 0; i < detectedBlocks.size(); i++) {
                    Rect block = detectedBlocks.get(i);
                    for (Rect existingBlock : piece) {
                        if (Math.abs(block.x - existingBlock.x) <= block.width * 1.25 &&
                            Math.abs(block.y - existingBlock.y) <= block.height * 1.25) {
                            piece.add(detectedBlocks.remove(i));
                            added = true;
                            break;
                        }
                    }
                    if (added) break;
                }
            } while (added);

            pieces.add(piece);
        }

        // Convert pieces into Piece objects
        Piece[] resultPieces = new Piece[pieces.size()];
        for (int i = 0; i < pieces.size(); i++) {
            List<Rect> pieceBlocks = pieces.get(i);

            // Normalize coordinates
            int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
            for (Rect block : pieceBlocks) {
                minX = Math.min(minX, block.x);
                minY = Math.min(minY, block.y);
            }

            Board pieceBoard = new Board(8, 8);
            for (Rect block : pieceBlocks) {
                int normalizedX = (block.x - minX) / block.width;
                int normalizedY = (block.y - minY) / block.height;
                pieceBoard.setCell(normalizedY, normalizedX);
            }

            resultPieces[i] = new Piece(pieceBoard);
        }

        return resultPieces;
    }
}
