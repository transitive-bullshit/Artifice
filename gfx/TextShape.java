package gfx;
import java.awt.*;
import javax.swing.*;

public class TextShape extends Shape {
    private java.awt.geom.Rectangle2D.Double _bounds;
    private String                           _text;
    private Font                             _font;
    private JPanel                           _dp;
    private FontMetrics                      _fm;

    public TextShape(JPanel dp) {
        this(dp, "");
    }

    public TextShape(JPanel dp, String text) {
        this(dp, text, 0, 0, null);
    }

    public TextShape(JPanel dp, String text, double x, double y) {
        this(dp, text, x, y, null);
    }

    public TextShape(JPanel dp, String text, double x, double y, Font font) {
        super(dp, null);
        
        this.setText(dp, text, x, y, font);
    }

    public final void setText(String text, boolean isCentered) {
        _text = text;

        _fm = _dp.getFontMetrics(_font);
        
        double x = this.getX();
        if (isCentered)
            x = (_dp.getWidth() - _dp.getFontMetrics(_font).stringWidth(_text)) / 2;
        _bounds = new java.awt.geom.Rectangle2D.Double(x, this.getY(), _fm
                .stringWidth(_text), _fm.getHeight());

        this.setShape(_bounds);
    }

    public void setText(String text) {
        this.setText(text, false);
    }

    public void setText(JPanel dp, String text, double x, double y, Font font) {
        _dp = dp;
        _text = text;

        // most linux shells use SansSerif by default
        if (font != null)
            _font = font;
        else _font = new Font("SansSerif", Font.PLAIN, 20);
        
        _fm = dp.getFontMetrics(_font);
        _bounds = new java.awt.geom.Rectangle2D.Double(x, y, _fm
                .stringWidth(_text), _fm.getHeight());

        this.setShape(_bounds);
    }

    public void setFont(Font newFont) {
        _font = newFont;

        // Reset shape's size with a change of Font
        _fm = _dp.getFontMetrics(_font);
        this.setOriginalSize(_fm.stringWidth(_text), _fm.getHeight());
    }

    public void setCentered() {
        double x = (_dp.getWidth() - _fm.stringWidth(_text)) / 2;
        double y = (_dp.getHeight() - _fm.getHeight()) / 2;

        this.setLocation(x, y);
    }

    public Font getFont() {
        return _font;
    }

    public double getWidth() {
        return _fm.stringWidth(_text);
    }

    public double getHeight() {
        return _fm.getHeight();
    }

    public void setSize(double width, double height) {
    }

    public void setScaledSize(double scaleX, double scaleY) {
    }

    public void paint(Graphics2D brush) {
        if (!this.getVisible())
            return;

        boolean isRotated = (this.getRotation() % (2 * Math.PI) != 0);
        java.awt.geom.RectangularShape shape = _bounds;
        Object oldAntialiasing = brush
                .getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        Paint oldPaint = brush.getPaint();
        Font oldFont = brush.getFont();

        // Setup graphics brush to draw the string
        if (isRotated)
            brush.rotate(this.getRotation(), shape.getCenterX(), shape
                    .getCenterY());

        if (this.getAntialiasing())
            brush.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

        brush.setPaint(this.getPaint());
        brush.setFont(_font);

        // Draw the Text String at the appropriate (x,y) location
        brush.drawString(_text, (int) this.getX(), (int) this.getY());

        // Restore old brush attributes
        brush.setFont(oldFont);
        brush.setPaint(oldPaint);
        brush
                .setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        oldAntialiasing);
        if (isRotated) // unrotate the graphics "canvas"
            brush.rotate(-this.getRotation(), shape.getCenterX(), shape
                    .getCenterY());
    }
}
