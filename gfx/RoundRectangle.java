package gfx;

public class RoundRectangle extends Shape {
    private java.awt.geom.RoundRectangle2D.Double _rect;

    /*
     * Creates a RoundRectangle2D, initialized to location (0.0, 0), size (0.0,
     * 0.0), and corner arcs of radius 0.0.
     */
    public RoundRectangle(javax.swing.JPanel dp) {
        super(dp, null);

        _rect = new java.awt.geom.RoundRectangle2D.Double();
        this.setShape(_rect);
    }

    /* Creates a RoundRectangle2D from the specified coordinates */
    public RoundRectangle(javax.swing.JPanel dp, double arcw, double arch) {
        super(dp, null);

        _rect = new java.awt.geom.RoundRectangle2D.Double(0, 0, 0, 0, arcw,
                arch);
        this.setShape(_rect);
    }

    public RoundRectangle(javax.swing.JPanel dp, double x, double y,
            double width, double height, double arcw, double arch) {
        super(dp, null);

        _rect = new java.awt.geom.RoundRectangle2D.Double(x, y, width, height,
                arcw, arch);
        this.setShape(_rect);
    }

    /*
     * Returns the corner arc's width, designated by the shape's geometrical
     * data
     */
    public double getArcWidth() {
        return _rect.getArcWidth();
    }

    /* Returns the corner arc's height */
    public double getArcHeight() {
        return _rect.getArcHeight();
    }

    /* Sets the corner arc's width in the geometrical data object _rect */
    public void setArcWidth(double arcw) {
        _rect.setRoundRect(this.getX(), this.getY(), this.getWidth(), this
                .getHeight(), arcw, _rect.getArcHeight());
    }

    /* Sets the corner arc's height in the geometrical data object _rect */
    public void setArcHeight(double arch) {
        _rect.setRoundRect(this.getX(), this.getY(), this.getWidth(), this
                .getHeight(), _rect.getArcWidth(), arch);
    }

    /*
     * Sets the corner arc's width and height in the geometrical data object
     * _rect
     */
    public void setArcSize(double arcw, double arch) {
        _rect.setRoundRect(this.getX(), this.getY(), this.getWidth(), this
                .getHeight(), arcw, arch);
    }
}
