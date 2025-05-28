import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import org.opencv.core.Mat;
import org.opencv.core.CvType;
import org.opencv.imgcodecs.Imgcodecs;

public class ScreenCapture {
    private static final int TARGET_WIDTH = 1179/2 - 65;
    private static final int TARGET_HEIGHT = 2556/2 - 133;
    
    private Robot robot;
    private GraphicsDevice primaryMonitor;

    public ScreenCapture() throws Exception {
        this.robot = new Robot();
        this.primaryMonitor = GraphicsEnvironment
            .getLocalGraphicsEnvironment()
            .getScreenDevices()[0];
    }

    public Mat captureScreen() {
        try {
            Rectangle bounds = primaryMonitor.getDefaultConfiguration().getBounds();
            
            // Calculate capture area on second monitor (x is negative)
            int captureX = bounds.x + (bounds.width / 2) - (TARGET_WIDTH / 2);
            int captureY = bounds.height / 2 - TARGET_HEIGHT / 2 - 61;
            
            Rectangle captureBounds = new Rectangle(
                captureX,  // This will be negative since it's on the second monitor
                captureY,
                TARGET_WIDTH,
                TARGET_HEIGHT
            );
            
            BufferedImage screenshot = robot.createScreenCapture(captureBounds);
            
            // Save debug screenshot
            ImageIO.write(screenshot, "PNG", new File("debug_screenshot.png"));
            Mat img = Imgcodecs.imread("debug_screenshot.png");
            return img;
        } catch (Exception e) {
            System.err.println("Error during screen capture: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
