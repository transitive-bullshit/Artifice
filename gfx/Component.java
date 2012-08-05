package gfx;
import java.awt.*;

public class Component {
    private double           _relativeWidth, _relativeHeight;
    private double           _relativeX, _relativeY;
    private gfx.CompositeGfx _composite;
    private gfx.Shape        _shape;

    public Component(gfx.CompositeGfx composite, gfx.Shape s, double relativeX,
            double relativeY, double relativeWidth, double relativeHeight) {
        _composite = composite; // This component's containing composite shape
        _shape = s; // This component's shape

        // All of component's attributes are relative (and subject to change),
        // according
        // to the composite graphic's size
        _relativeX = relativeX;
        _relativeY = relativeY;
        _relativeWidth = relativeWidth;
        _relativeHeight = relativeHeight;
    }

    /**
     * Sets component's location and size relative to containing composite
     * graphic
     */
    public void update(double scaleX, double scaleY) {

        _shape.setLocation(_composite.getX() + _relativeX * scaleX, _composite
                .getY()
                + _relativeY * scaleY);
        _shape.setSize(_relativeWidth * scaleX, _relativeHeight * scaleY);
        // Had some trouble getting the scales correct at first..
        // System.out.println("scale: " + scaleX + " ; " + scaleY);
        // System.out.println(_shape.getWidth() + " ; " + _shape.getX());
    }

    /** Returns this component's Shape */
    public Shape getShape() {
        return _shape;
    }

    public boolean contains(Point p) {
        double rotationAngle = _composite.getRotation() % (2 * Math.PI);
        // System.out.println("contains invoked" + _shape.contains(p) + " rot "
        // + rotationAngle);
        if (rotationAngle != 0) {
            java.awt.geom.AffineTransform trans = java.awt.geom.AffineTransform
                    .getRotateInstance(rotationAngle, _composite.getCenterX(),
                            _composite.getCenterY());

            java.awt.Shape s = trans.createTransformedShape(_shape
                    .getGeomShape());

            return s.contains(p);
        }

        return _shape.contains(p);
    }

    /** Paints this component's Shape */
    public void paint(Graphics2D brush) {
        _shape.paint(brush);
    }
}
