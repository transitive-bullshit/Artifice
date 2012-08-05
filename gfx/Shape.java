package gfx;
import java.awt.*;

/**
 * Welcome to the beginning of your very own graphics package! This graphics
 * package will be used in most of your assignments from now on.
 * 
 * This should look A LOT like the code you've seen in lecture (HINT HINT). At
 * first glance this class looks really dense, but don't worry most of the
 * methods you have to fill in wont be very long.
 * 
 * REMEMBER most of the code you will write here will be code you've seen before
 * (WINK WINK).
 * 
 * Feel free to add other functionality, but keep in mind this is a shape and
 * shouldn't have capabilities that are more specific to say bees or something.
 * 
 * If you try compiling this before you change anything it won't work because
 * some methods have return types but haven't been set to return anything.
 * 
 * Some bells and whistles you might want to add: - set transparency (look at
 * java.awt.Color in the docs) - anti aliasing (getting rid of jaggies...) -
 * changing border width
 * 
 */
public abstract class Shape implements Mover {

    /** Used to store some geometrical data for this shape. */
    protected java.awt.geom.RectangularShape _shape;

    /** Reference to containing subclass of JPanel. */
    protected javax.swing.JPanel           _container;

    /** Border and Fill Colors. */
    private java.awt.Color                 _borderColor, _fillColor;

    /** Rotation (must be in radians). */
    private double                         _rotationAngle;

    /** Indicates whether or not the shape should wrap. */
    protected boolean                      _wrapping;

    /** Whether or not the shape should paint itself. */
    private boolean                        _isVisible;

    /** Size of the shape's border color. */
    private int                            _borderWidth;

    /** Alpha transparency level of shape's color. */
    private int                            _alphaLevel;
    public static final int                OPAQUE = 255;

    /** Whether or not antialiasing is enabled */
    private boolean                        _antialiasing;

    protected static final double          FIXED  = 1;
    protected double                       _scaleX, _scaleY;

    private Paint                          _paint;
    private boolean                        _hasSize;

    /**
     * Initialize all instance variables here. You'll need to store the
     * containing subclass of JPanel to deal with wrapping and some of the extra
     * credit stuff.
     */
    public Shape(javax.swing.JPanel container, java.awt.geom.RectangularShape s) {
        // Store associated container and RectangularShape
        _container = container;
        _shape = s;

        // Initialize default Shape properties (instance variables)
        _borderColor = java.awt.Color.black;
        _fillColor = java.awt.Color.black;
        _paint = _fillColor;
        // By default, shape is not rotated
        _rotationAngle = 0;
        _wrapping = false;
        _isVisible = true;
        _borderWidth = 0;
        // Default no transparency (255)
        _alphaLevel = OPAQUE;
        // Default antialiasing turned off (too computationally expensive)
        _antialiasing = false;

        _scaleX = FIXED;
        _scaleY = FIXED;

        if (_shape != null)
            _hasSize = (this.getWidth() + this.getHeight() > 0);
    }

    /** Returns the x location of the top left corner of shapes bounding box */
    public double getX() {
        return _shape.getX();
    }

    /** Returns the y location of the top left corner of shapes bounding box */
    public double getY() {
        return _shape.getY();
    }

    /** Returns the height of shapes bounding box */
    public double getHeight() {
        return _shape.getHeight();
    }

    /** Returns width of shapes bounding box */
    public double getWidth() {
        return _shape.getWidth();
    }

    /** Returns the border color you are storing */
    public java.awt.Color getBorderColor() {
        return _borderColor;
    }

    /** Returns the fill color you are storing */
    public java.awt.Color getFillColor() {
        return _fillColor;
    }

    /** Returns the rotation you are storing */
    public double getRotation() {
        return _rotationAngle;
    }

    /** Returns the width of the brush stroke for the outline of your shape */
    public int getBorderWidth() {
        return _borderWidth;
    }

    /** Returns whether or not the shape is wrapping */
    public boolean getWrapping() {
        return _wrapping;
    }

    /** Returns whether or not the shape is visible */
    public boolean getVisible() {
        return _isVisible;
    }

    /** Returns the current alpha transparency level of the shapes color */
    public int getTransparency() {
        return _alphaLevel;
    }

    /** Returns whether or not antialiasing is enabled for this shape */
    public boolean getAntialiasing() {
        return _antialiasing;
    }

    public java.awt.geom.RectangularShape getGeomShape() {
        return _shape;
    }

    public double getScaleX() {
        return _scaleX;
    }

    public double getScaleY() {
        return _scaleY;
    }

    public void setPaint(Paint paint) {
        _paint = paint;
    }

    /**
     * Set the location of shape, including wrapping if set
     */
    public void setLocation(double x, double y) {
        double newX = x;
        double newY = y;

        if (_wrapping) {
            if (_container.getWidth() > 0)
                newX = Math.abs(newX) % _container.getWidth();
            if (_container.getHeight() > 0)
                newY = Math.abs(newY) % _container.getHeight();

            // System.out.println(_container.getWidth() + " " +
            // _container.getHeight());

            // Properly wrap negative locations
            if (x < 0)
                newX = _container.getWidth() - newX;
            if (y < 0)
                newY = _container.getHeight() - newY;
        }

        // Move the shape to the newly calculated newX and newY coordinates
        _shape.setFrame(newX, newY, _shape.getWidth(), _shape.getHeight());
    }

    /** Sets the size of shape, remembering the amount this shape has been scaled */
    /*
     * public void setNewSize(double width, double height) { _scaleX *= width /
     * this.getWidth(); _scaleY *= height / this.getHeight();
     * 
     * _shape.setFrame(_shape.getX(), _shape.getY(), width, height); }
     */

    /** Sets the size of shape, remembering the amount this shape has been scaled */
    public void setSize(double width, double height) {
        if (_hasSize) {
            _scaleX *= width / this.getWidth();
            _scaleY *= height / this.getHeight();
        } else {
            _scaleX = FIXED;
            _scaleY = FIXED;
            _hasSize = true;
        }

        _shape.setFrame(_shape.getX(), _shape.getY(), width, height);
    }

    /**
     * Sets the original size of this shape, where the scale is at 100% (no
     * scaling)
     */
    public void setOriginalSize(double width, double height) {
        _scaleX = FIXED;
        _scaleY = FIXED;

        _shape.setFrame(_shape.getX(), _shape.getY(), width, height);
    }

    public void setScaledSize(double scaleX, double scaleY) {
        double width = this.getWidth() * scaleX / _scaleX;
        double height = this.getHeight() * scaleY / _scaleY;
        _scaleX = scaleX;
        _scaleY = scaleY;

        _shape.setFrame(_shape.getX(), _shape.getY(), width, height);
    }

    /** Set the border color */
    public void setBorderColor(java.awt.Color c) {
        _borderColor = c;
    }

    /** Set the fill color */
    public void setFillColor(java.awt.Color c) {
        _fillColor = c;

        this.setPaint(c);// this.transparentColor(c));
    }

    /** Set the color of the whole shape */
    public void setColor(java.awt.Color c) {
        this.setBorderColor(c);
        this.setFillColor(c);
    }

    /** Set the rotation of the shape */
    public void setRotation(int degrees) {
        _rotationAngle = degrees * Math.PI / 180;
    }

    /** For convenience, rotates shape relative to current rotation */
    public void rotate(int degrees) {
        _rotationAngle += degrees * Math.PI / 180;
    }

    /** Set how thick the shapes outline will be */
    public void setBorderWidth(int width) {
        if (width >= 0)
            _borderWidth = width;
    }

    /** Set whether or not the shape should wrap */
    public void setWrapping(boolean wrap) {
        _wrapping = wrap;
    }

    /** Set whether or not the shape should paint itself */
    public void setVisible(boolean visible) {
        _isVisible = visible;
    }

    /** Set the shapes transparency level */
    public void setTransparency(int newAlpha) {
        if (newAlpha < 0)
            newAlpha = 0;
        else if (newAlpha > OPAQUE)
            newAlpha = OPAQUE;

        _alphaLevel = newAlpha;
    }

    /** Enable/Disable the shapes antialiasing option */
    public void setAntialiasing(boolean antialiased) {
        _antialiasing = antialiased;
    }

    public void setShape(java.awt.geom.RectangularShape newShape) {
        _shape = newShape;
        _hasSize = (this.getWidth() + this.getHeight() > 0);
    }

    /** Adds the current alpha transparency level to a given color */
    public java.awt.Color transparentColor(java.awt.Color color) {
        if (_alphaLevel == OPAQUE)
            return color;

        return new java.awt.Color(color.getRed(), color.getGreen(), color
                .getBlue(), _alphaLevel);
    }

    /** Moves the shape relative to its current position */
    public void move(double dX, double dY) {
        this.setLocation(_shape.getX() + dX, _shape.getY() + dY);
    }

    /**
     * This method is best explained in pseudo code: If shape is visible get the
     * properties of the brush that you'll be changing rotate graphics set the
     * brush stroke (width) of the graphics (optional) set the color of the
     * graphics to the fill color of the shape fill the shape set the color of
     * the graphics to the border color of the shape draw the shape unrotate the
     * graphics reset the remaining properties of the brush that you changed
     */
    public void paint(java.awt.Graphics2D brush) {
        if (!_isVisible)
            return;

        /* Store brush attributes that we're going to change */
        boolean isRotated = (_rotationAngle % (2 * Math.PI) != 0);
        Object oldAntialiasing = brush
                .getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        Stroke oldStroke = brush.getStroke();
        BasicStroke newStroke = new BasicStroke(_borderWidth);
        Paint oldPaint = brush.getPaint();
        java.awt.Color oldColor = brush.getColor();
        java.awt.Color color;

        /* Modify brush attributes according to this shape's properties */
        if (isRotated) // rotate the graphics "canvas"
            brush.rotate(_rotationAngle, _shape.getCenterX(), _shape
                    .getCenterY());

        if (_antialiasing) // Enable Antialiasing
            brush.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

        brush.setStroke(newStroke); // brush's stroke with is now same as
                                    // _borderWidth

        /* Main draw and fill routines with added transparency */
        // Shape's fill (inside)
        brush.setPaint(this.getPaint());
        brush.fill(_shape);

        // Shape's outline
        if (_borderWidth > 0) {
            color = transparentColor(_borderColor);
            brush.setColor(color);
            brush.draw(_shape);
        }

        /* Restore saved brush attributes */
        brush
                .setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        oldAntialiasing);
        brush.setColor(oldColor);
        brush.setPaint(oldPaint);
        brush.setStroke(oldStroke);

        if (isRotated) // unrotate the graphics "canvas"
            brush.rotate(-_rotationAngle, _shape.getCenterX(), _shape
                    .getCenterY());
    }

    public Paint getPaint() {
        return this.getUpdatedPaint(_paint);
    }

    /**
     * Returns a Paint with same subclass of aPaint, only updated with current
     * position/size of Shape
     */
    public Paint getUpdatedPaint(Paint aPaint) {

        // Check if aPaint is a GradientPaint
        if (aPaint instanceof java.awt.GradientPaint) {
            GradientPaint p = (GradientPaint) aPaint;
            java.awt.geom.Point2D p1 = p.getPoint1(), p2 = p.getPoint2();
            double x1 = p1.getX() + this.getX();
            double y1 = p1.getY() + this.getY();
            double x2 = x1 + p2.getX() * this.getScaleX();
            double y2 = y1 + p2.getY() * this.getScaleY();

            return new GradientPaint((float) x1, (float) y1, p.getColor1(),
                    (float) x2, (float) y2, p.getColor2(), p.isCyclic());
        }

        // Check if aPaint is a TexturePaint
        if (aPaint instanceof java.awt.TexturePaint) {
            TexturePaint tp = (TexturePaint) aPaint;

            java.awt.geom.Rectangle2D r = tp.getAnchorRect();
            r.setRect(new java.awt.geom.Rectangle2D.Double(r.getX()
                    + this.getX(), r.getY() + this.getY(), r.getWidth()
                    * this.getScaleX(), r.getHeight() * this.getScaleY()));

            return new TexturePaint(tp.getImage(), r);
        }

        // if (aPaint instanceof java.awt.Color) if it's not a gradient or a
        // texture, then it has to be a color
        return this.transparentColor((Color) aPaint);
    }

    /**
     * Should return true if the point is within the shape. There's a special
     * case for when the shape is rotated which you will hear about in section.
     * This doesn't need to be done for Cartoon, but it will be rquired for
     * Swarm.
     */
    public boolean contains(java.awt.Point p) {
        if (_rotationAngle % (2 * Math.PI) != 0) {
            java.awt.geom.AffineTransform trans = java.awt.geom.AffineTransform
                    .getRotateInstance(_rotationAngle, _shape.getCenterX(),
                            _shape.getCenterY());

            java.awt.Shape s = trans.createTransformedShape(_shape);

            return s.contains(p);
        }

        return _shape.contains(p);
    }

    /**
     * This should be called when the shape is clicked. You'll want to overwrite
     * this in subclasses to do something useful. Should stay empty in this
     * class
     */
    public void react() {
    }

    public void move() {
    }
}
