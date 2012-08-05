package gfx;

public class Ellipse extends Shape {

    /* Creates default Ellipse initialized to location (0,0) and size (0,0) */
    public Ellipse(javax.swing.JPanel dp) {
        super(dp, new java.awt.geom.Ellipse2D.Double());
    }

    /*
     * Constructs and initializes a specialized Ellipse2D from the given
     * coordinates
     */
    public Ellipse(javax.swing.JPanel dp, double x, double y, double w, double h) {
        super(dp, new java.awt.geom.Ellipse2D.Double(x, y, w, h));
    }
}
