package gfx;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

public class ImageShape extends Shape {
    private java.awt.geom.Rectangle2D.Double _bounds;
    BufferedImage                            _image;
    JPanel                                   _dp;

    public ImageShape(JPanel dp) {
        super(dp, null);

        _dp = dp;
        _image = null;
        _bounds = new java.awt.geom.Rectangle2D.Double();
    }

    public ImageShape(JPanel dp, String aFile) {
        super(dp, null);

        _dp = dp;
        _image = ImageUtils.getBufferedImage(dp, aFile,
                Transparency.TRANSLUCENT);
        _bounds = new java.awt.geom.Rectangle2D.Double(0, 0, _image.getWidth(),
                _image.getHeight());

        this.setShape(_bounds);
    }

    public ImageShape(JPanel dp, String aFile, double x, double y) {
        super(dp, null);

        _dp = dp;
        _image = ImageUtils.getBufferedImage(dp, aFile,
                Transparency.TRANSLUCENT);
        _bounds = new java.awt.geom.Rectangle2D.Double(x, y, _image.getWidth(),
                _image.getHeight());

        this.setShape(_bounds);
    }

    public ImageShape(JPanel dp, double x, double y) {
        super(dp, null);

        _dp = dp;
        _image = null;
        _bounds = new java.awt.geom.Rectangle2D.Double(x, y, 0, 0);

        this.setShape(_bounds);
    }

    public BufferedImage getBufferedImage() {
        return _image;
    }

    public void setImage(String aFile) {
        this.setImage(ImageUtils.getBufferedImage(_dp, aFile));
    }

    public void setImage(BufferedImage anImage) {
        _image = anImage;
        _bounds = new java.awt.geom.Rectangle2D.Double(this.getX(),
                this.getY(), _image.getWidth(), _image.getHeight());

        this.setShape(_bounds);
    }

    public void paint(Graphics2D brush) { // overrides Shape paint method
        if (!this.getVisible())
            return;

        boolean isRotated = (this.getRotation() % (2 * Math.PI) != 0);
        java.awt.geom.RectangularShape shape = this.getGeomShape();

        if (isRotated)
            brush.rotate(this.getRotation(), shape.getCenterX(), shape
                    .getCenterY());

        Composite oldComposite = brush.getComposite();
        if (this.getTransparency() != Shape.OPAQUE)
            brush.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER,
                    ((float) this.getTransparency() / Shape.OPAQUE)));

        // System.out.println(this.getFillColor());
        brush.drawImage(_image, (int) this.getX(), (int) this.getY(),
                (int) this.getWidth(), (int) this.getHeight(), _dp);

        brush.setComposite(oldComposite);

        if (isRotated) // unrotate the graphics "canvas"
            brush.rotate(-this.getRotation(), shape.getCenterX(), shape
                    .getCenterY());
    }
}
