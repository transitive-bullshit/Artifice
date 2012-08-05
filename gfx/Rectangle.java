package gfx;

public class Rectangle extends Shape {

    /* Creates default Rectangle initialized to location (0,0) and size (0,0) */
    public Rectangle(javax.swing.JPanel dp) {
        super(dp, new java.awt.geom.Rectangle2D.Double());
    }

    /*
     * Constructs and initializes a specialized Rectangle2D from the given
     * coordinates
     */
    public Rectangle(javax.swing.JPanel dp, double x, double y, double width,
            double height) {
        super(dp, new java.awt.geom.Rectangle2D.Double(x, y, width, height));
    }
}
