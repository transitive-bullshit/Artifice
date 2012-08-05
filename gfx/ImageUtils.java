package gfx;
import java.awt.*;
import java.awt.image.*;
import java.net.URL;

public class ImageUtils {
    public static BufferedImage getBufferedImage(java.awt.Component c,
            String aFile) {
        return ImageUtils
                .getBufferedImage(c, aFile, BufferedImage.TYPE_INT_RGB);
    }

    public static BufferedImage getBufferedImage(java.awt.Component c,
            String aFile, int aType) {
        // Image image = c.getToolkit().
        // System.out.println("Load file: " + aFile);
        URL completeImageURL = ImageUtils.completeURL(c.getClass(), aFile);

        // Image image = java.awt.Toolkit.getDefaultToolkit().getImage(aFile);
        // Image image =
        // Toolkit.getDefaultToolkit().createImage(completeFileName);
        // System.out.println(aFile + " " + c.getToolkit() + " " +
        // c.getToolkit().createImage(completeFileName));

        Image image = c.getToolkit().createImage(completeImageURL);
        // System.out.println(completeFileName);
        
        if (!waitForImage(c, image, aFile))
            return null;

        BufferedImage bufferedImage = new BufferedImage(image.getWidth(c),
                image.getHeight(c), aType);
        Graphics2D temp = bufferedImage.createGraphics();
        temp.drawImage(image, 0, 0, c);

        bufferedImage.flush();
        return bufferedImage;
    }
    
    public static BufferedImage getBufferedImageAbsolute(java.awt.Component c,
            String aFile) {
        return ImageUtils
                .getBufferedImageAbsolute(c, aFile, BufferedImage.TYPE_INT_RGB);
    }
    
    public static BufferedImage getBufferedImageAbsolute(java.awt.Component c,
            String aFile, int aType) {
        Image image = c.getToolkit().createImage(aFile);
        // System.out.println(completeFileName);

        if (!waitForImage(c, image, aFile))
            return null;

        BufferedImage bufferedImage = new BufferedImage(image.getWidth(c),
                image.getHeight(c), aType);
        Graphics2D temp = bufferedImage.createGraphics();
        temp.drawImage(image, 0, 0, c);

        bufferedImage.flush();
        return bufferedImage;
    }

    public static boolean waitForImage(java.awt.Component c, Image image,
            String aFile) {
        MediaTracker tracker = new MediaTracker(c);
        tracker.addImage(image, 0);

        try {
            tracker.waitForAll();
        } catch (InterruptedException e) {
        }

        if (tracker.isErrorAny()) {
            System.err.println("Error loading " + aFile);
            return false;
        }

        return true;
    }
    
    // Returns the filename relative to a given class' location
    public static URL completeURL(Class classIndex, String relativeFileName) {
        URL url = classIndex.getResource(relativeFileName);

        // System.out.println("Class: " + classIndex.getName());
        // System.out.println("classloader: " + classIndex.getClassLoader());

        // System.out.println("Filename: " + relativeFileName);
        // System.out.println("URL: " + url);

        return url;
    }

    public static String completePath(Class classIndex, String relativeFileName) {
        URL url = ImageUtils.completeURL(classIndex, relativeFileName);

        if (url == null)
            return "";
        else return url.getFile();
    }

    /** Convenience Methods */
    public static TexturePaint getTexturePaint(java.awt.Component c,
            String aFile, double x, double y, double width, double height) {
        BufferedImage image = getBufferedImage(c, aFile);

        return new TexturePaint(image, new java.awt.geom.Rectangle2D.Double(x,
                y, width, height));
    }

    public static TexturePaint getTexturePaint(java.awt.Component c,
            String aFile) {
        BufferedImage image = getBufferedImage(c, aFile);

        return new TexturePaint(image, new java.awt.geom.Rectangle2D.Double(0,
                0, image.getWidth(), image.getHeight()));
    }

    public static TexturePaint getTexturePaint(java.awt.Component c,
            String aFile, double x, double y, float scaleX, float scaleY) {
        BufferedImage image = getBufferedImage(c, aFile);

        return new TexturePaint(image, new java.awt.geom.Rectangle2D.Double(x,
                y, image.getWidth() * scaleX, image.getHeight() * scaleY));
    }
    // public java.awt.geom.Rectangle2D.Double getBounds() {
    // return _bounds;
    // }
    // rawImage.getScaledInstance(
    // 200,-1,Image.SCALE_AREA_AVERAGING);

}
