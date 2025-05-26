import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.highgui.HighGui;
import java.util.*;

public class ImageTesting {
    static { System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
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

            // Use empty background color (#1d2445 in BGR: 69, 36, 29)
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
        } else {
            System.out.println("No grid detected.");
        }

        Size newSize = new Size(img.width() * 0.5, img.height() * 0.5);
        Mat resized = new Mat();
        Imgproc.resize(img, resized, newSize);
        HighGui.imshow("Detected Grid with Fill Status (scaled down)", resized);
        HighGui.waitKey(0);
        System.exit(0);
    }
}