package gfx;
import java.awt.*;

/*
 * CompositeGfx extends Shape to provide a maximum degree of flexibility with
 * polymorphism. With this design, composite graphics don't just have to be made
 * up of shapes; they can be made up of other composite graphics, which in turn
 * can be made up of any number of shapes and/or composite shapes
 * 
 * The keyword this, represents the composite graphic. Ex: this.getX() would
 * return the x coordinate of the bounding rectangle which all subshapes are
 * contained within.
 * 
 * Many of superclass Shape's mutators are overridden because in a composite
 * shape, all of the components need to be mutated as well (using iteration)
 */

// Bounded rectangle with same properties as Shapecontaining an arbitrary number
// of component shapes
public class CompositeGfx extends Shape {
    private ComponentList _components;

    // private double _scaleX, _scaleY;
    // protected static final double FIXED = 1;

    /*
     * Creates a new CompositeGfx at location (x,y) Note: Graphic's size will be
     * determined by the size of its containing graphics
     */
    public CompositeGfx(javax.swing.JPanel dp, double x, double y) {
        super(dp, new java.awt.geom.Rectangle2D.Double(x, y, FIXED, FIXED));

        // _scaleX = FIXED;
        // _scaleY = FIXED;

        _components = new ComponentList();
    }

    public void add(Shape s) {
        // System.out.println("x "+s.getX()+" y = "+s.getY()+" w =
        // "+s.getWidth()+" h = "+s.getHeight());
        this.addComponent(new Component(this, s, s.getX(), s.getY(), s
                .getWidth(), s.getHeight()));
    }

    // For convenience; adds a rectangular shape who's location and size have
    // yet to be initialized
    public void add(Shape s, double x1, double y1, double x2, double y2) {
        this.addComponent(new Component(this, s, x1, y1, x2 - x1, y2 - y1));
    }

    public void addComponent(Component c) {
        // Initialize this component relative to this composite graphic's
        // location and size
        c.update(_scaleX, _scaleY);

        // Add this component to the list of component shapes contained within
        // this composite graphic
        _components.add(c);

        // Update Composite's size without updating the scale
        this.setOriginalSize(_components.getCompositeWidth(this.getX()),
                _components.getCompositeHeight(this.getY()));
    }

    /** Move all components (subshapes) in relation to composite object * */
    public void updateComponents() {
        _components.update(_scaleX, _scaleY);
    }

    /** Paint all subshapes * */
    public void paint(Graphics2D brush) {
        if (this.getVisible()) {
            // java.awt.geom.AffineTransform newTransform =
            // (java.awt.geom.AffineTransform)brush.getTransform().clone();
            double _rotationAngle = this.getRotation() % (2 * Math.PI);
            boolean isRotated = (_rotationAngle != 0);
            boolean isAntialiasing = this.getAntialiasing();
            double compositeCenterX = this.getCenterX();
            double compositeCenterY = this.getCenterY();
            Object oldAntialiasing = brush
                    .getRenderingHint(RenderingHints.KEY_ANTIALIASING);

            if (isRotated)
                brush
                        .rotate(_rotationAngle, compositeCenterX,
                                compositeCenterY);

            if (isAntialiasing)
                brush.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

            _components.paint(brush);

            brush.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    oldAntialiasing);

            // brush.setTransform(oldTransform);
            if (isRotated)
                brush.rotate(-_rotationAngle, compositeCenterX,
                        compositeCenterY);
        }
    }

    /** Move composite object with absolute positioning and wrapping, if set * */
    public void setLocation(double x, double y) {
        /**
         * Wrapping is done at composite level, because if each component shape
         * were wrapped, the composite shape could potentially break apart, with
         * part of it wrapped and part of it not wrapped yet. Wrapping at the
         * component level would also make the rotation calculations much harder
         * (since composite shape wouldn't always be "together").
         */

        super.setLocation(x, y);
        this.updateComponents(); // Update components (subshapes) in relation
                                    // to composite object
    }

    // Override in subclasses to extend functionality
    public void move() {
        this.updateComponents();
    }

    /*
     * Sets the composite graphic's bounding rectangle width and height. Note:
     * Changing the composite graphic's size (widthxheight) will scale the
     * individual shapes within the graphic accordingly
     */
    public void setSize(double width, double height) {
        // _scaleX *= width / this.getWidth();
        // _scaleY *= height / this.getHeight();

        super.setSize(width, height);
        this.updateComponents();
    }

    public void setScaledSize(double scaleX, double scaleY) {
        super.setScaledSize(scaleX, scaleY);
        this.updateComponents();
    }

    /** Returns the center x coordinate of the composite graphic */
    public double getCenterX() {
        return this.getX() + _components.getCompositeWidth(this.getX()) / 2;
    }

    /** Returns the center y coordinate of the composite graphic */
    public double getCenterY() {
        return this.getY() + _components.getCompositeHeight(this.getY()) / 2;
    }

    /** Set the color of each component shape */
    public void setColor(java.awt.Color c) {
        super.setColor(c);
        _components.setColor(c);
    }

    /** Set how thick the shapes outline will be */
    public void setBorderWidth(int width) {
        super.setBorderWidth(width);
        _components.setBorderWidth(width);
    }

    public void setBorderColor(java.awt.Color aColor) {
        super.setBorderColor(aColor);
        _components.setBorderColor(aColor);
    }

    public void setFillColor(java.awt.Color aColor) {
        super.setFillColor(aColor);
        _components.setFillColor(aColor);
    }

    /** Set each of the component shape's transparency levels */
    public void setTransparency(int newAlpha) {
        super.setTransparency(newAlpha);
        _components.setTransparency(newAlpha);
    }

    // Do not want to check if bounding rectangle contains point
    // Instead, check if any of the subshapes contain the point
    public boolean contains(java.awt.Point p) {
        return _components.contains(p);
    }

    /** Methods which override Shape superclass methods */
}
