package gfx;

// type (closure) is either java.awt.geom.Arc2D. CHORD, OPEN, or PIE

public class Arc extends Shape {
    private java.awt.geom.Arc2D.Double _arc;

    public Arc(javax.swing.JPanel dp) {
        super(dp, null);

        _arc = new java.awt.geom.Arc2D.Double();
        this.setShape(_arc);
    }

    public Arc(javax.swing.JPanel dp, double aStart, double anExtent, int aType) {
        super(dp, null);

        _arc = new java.awt.geom.Arc2D.Double(0, 0, 0, 0, aStart, anExtent,
                aType);
        this.setShape(_arc);
        // java.awt.geom.Arc2D.PIE
    }

    public Arc(javax.swing.JPanel dp, double x, double y, double w, double h,
            double aStart, double anExtent, int aType) {
        super(dp, null);

        _arc = new java.awt.geom.Arc2D.Double(x, y, w, h, aStart, anExtent,
                aType);
        this.setShape(_arc);
    }

    public Arc(javax.swing.JPanel dp, int aType) {
        super(dp, null);

        _arc = new java.awt.geom.Arc2D.Double(aType);
        this.setShape(_arc);
    }

    public double getAngleStart() {
        return _arc.getAngleStart();
    }

    public double getAngleExtent() {
        return _arc.getAngleExtent();
    }

    public void setAngleStart(double aStart) {
        _arc.setAngleStart(aStart);
    }

    public void setAngleExtent(double anExtent) {
        _arc.setAngleExtent(anExtent);
    }

    public void setAngle(double aStart, double anExtent) {
        _arc.setAngleStart(aStart);
        _arc.setAngleExtent(anExtent);
    }

    public void setAngleType(int aType) {
        _arc.setArc(this.getX(), this.getY(), this.getWidth(),
                this.getHeight(), this.getAngleStart(), this.getAngleExtent(),
                aType);
    }
}
