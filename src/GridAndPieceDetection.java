import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.highgui.HighGui;
import java.util.*;

public class GridAndPieceDetection {
    static {
        System.load("C:/opencv-4.11.0/build/java/x64/opencv_java4110.dll");
    }

    public static void main(String[] args) {
        String imagePath = "C:/Users/Bryce/.vscode/Projects/block-blast-solver/src/screenshots/IMG_5928.PNG";
        Mat img = Imgcodecs.imread(imagePath);
        if (img.empty()) { System.out.println("Could not read the image."); return; }

        Mat gray = new Mat();
        Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);
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
            Imgproc.rectangle(img, largestRect, new Scalar(0,0,255), 3);
            System.out.println("Grid detected at: " + largestRect);

            int cellWidth = largestRect.width / 8, cellHeight = largestRect.height / 8;
            boolean[][] filledGrid = new boolean[8][8];
            double[] emptyColor = {69, 36, 29};

            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    int cx = largestRect.x + col * cellWidth + cellWidth / 2;
                    int cy = largestRect.y + row * cellHeight + cellHeight / 2;
                    double[] pixel = img.get(cy, cx);
                    double diff = 0;
                    for (int i = 0; i < 3; i++) diff += Math.abs(pixel[i] - emptyColor[i]);
                    filledGrid[row][col] = diff > 30;
                }
            }

            System.out.println("Grid Fill Status (true=filled, false=empty):");
            for (boolean[] row : filledGrid) System.out.println(Arrays.toString(row));

            // Draw green rectangles on filled cells
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (filledGrid[row][col]) {
                        int x = largestRect.x + col * cellWidth;
                        int y = largestRect.y + row * cellHeight;
                        Imgproc.rectangle(img, new Point(x, y), new Point(x + cellWidth, y + cellHeight), new Scalar(0, 255, 0), 2);
                    }
                }
            }

            // Now isolate and detect pieces using grid bottom edge
            int piecePanelY = largestRect.y + largestRect.height + 10; // adjust offset as needed
            int piecePanelHeight = largestRect.height / 2; // estimate height of piece panel
            Rect piecePanelRect = new Rect(0, piecePanelY, img.cols(), piecePanelHeight);
            Mat piecePanel = new Mat(img, piecePanelRect);
    
            // Convert to HSV and use Value channel for bright block detection
            Mat pieceHSV = new Mat();
            Imgproc.cvtColor(piecePanel, pieceHSV, Imgproc.COLOR_BGR2HSV);
            List<Mat> hsvChannels = new ArrayList<>();
            Core.split(pieceHSV, hsvChannels);
            Mat valueChannel = hsvChannels.get(2);

            // Threshold to highlight bright blocks
            Mat binary2 = new Mat();
            Imgproc.threshold(valueChannel, binary2, 180, 255, Imgproc.THRESH_BINARY);

            // Find outer piece contours
            List<MatOfPoint> pieceContours = new ArrayList<>();
            Imgproc.findContours(binary2, pieceContours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
            pieceContours.sort(Comparator.comparingDouble(cnt -> Imgproc.boundingRect(cnt).x));

            // For each piece, detect inner blocks
            for (int i = 0; i < pieceContours.size(); i++) {
                Rect pieceRect = Imgproc.boundingRect(pieceContours.get(i));
                Imgproc.rectangle(piecePanel, pieceRect, new Scalar(255, 0, 0), 2);

                // Extract the piece region
                Mat pieceROI = new Mat(piecePanel, pieceRect);

                // Use same HSV Value threshold on piece ROI to isolate blocks
                Mat pieceROIHSV = new Mat();
                Imgproc.cvtColor(pieceROI, pieceROIHSV, Imgproc.COLOR_BGR2HSV);
                List<Mat> roiChannels = new ArrayList<>();
                Core.split(pieceROIHSV, roiChannels);
                Mat roiValue = roiChannels.get(2);

                Mat blockBinary = new Mat();
                Imgproc.threshold(roiValue, blockBinary, 180, 255, Imgproc.THRESH_BINARY);

                // Find small block contours inside the piece
                List<MatOfPoint> blockContours = new ArrayList<>();
                Imgproc.findContours(blockBinary, blockContours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

                for (MatOfPoint blockContour : blockContours) {
                    Rect blockRect = Imgproc.boundingRect(blockContour);
                    // Filter by approximate square shape and size
                    if (blockRect.width > 10 && blockRect.height > 10 &&
                        Math.abs(blockRect.width - blockRect.height) < 10) {
                        Imgproc.rectangle(pieceROI, blockRect, new Scalar(0, 255, 0), 2);
                    }
                }
            }
    
            // Display detected pieces
            Size newSize = new Size(piecePanel.width() * 0.8, piecePanel.height() * 0.8);
            Mat resizedPanel = new Mat();
            Imgproc.resize(piecePanel, resizedPanel, newSize);
            HighGui.imshow("Detected Pieces and Blocks", resizedPanel);
    
            Size newSizeImg = new Size(img.width() * 0.5, img.height() * 0.5);
            Mat resizedImg = new Mat();
            Imgproc.resize(img, resizedImg, newSizeImg);
            HighGui.imshow("Grid and Pieces", resizedImg);
            HighGui.waitKey(0);
            System.exit(0);
        }
    }
}
