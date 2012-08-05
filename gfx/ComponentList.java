package gfx;
import java.awt.*;

/*
 * Contains methods for performing an operation on all of the components of a
 * Composite graphic
 */
public class ComponentList extends java.util.Vector<Component> {

    public ComponentList() {
        super();
    }

    public Shape shapeAt(int c) {
        Component component = this.elementAt(c);

        return component.getShape();
    }

    public void paint(Graphics2D brush) {
        Component component;
        int c;

        for(c = 0; c < this.elementCount; c++) {
            component = this.elementAt(c);
            component.paint(brush);
        }
    }

    public void update(double scaleX, double scaleY) {
        Component component;
        int c;

        for(c = 0; c < this.elementCount; c++) {
            component = this.elementAt(c);

            component.update(scaleX, scaleY);
        }
    }

    public double getCompositeWidth(double compositeX) {
        double currentWidth, largestWidth = 0;
        Shape shape;
        int c;

        for(c = 0; c < this.elementCount; c++) {
            shape = shapeAt(c);

            currentWidth = shape.getX() + shape.getWidth();
            if (currentWidth > largestWidth)
                largestWidth = currentWidth;
        }

        return largestWidth - compositeX;
    }

    public double getCompositeHeight(double compositeY) {
        double currentHeight, largestHeight = 0;
        Shape shape;
        int c;

        for(c = 0; c < this.elementCount; c++) {
            shape = shapeAt(c);

            currentHeight = shape.getY() + shape.getHeight();
            if (currentHeight > largestHeight)
                largestHeight = currentHeight;
        }

        return largestHeight - compositeY;
    }

    public void setBorderWidth(int width) {
        Shape shape;
        int c;

        for(c = 0; c < this.elementCount; c++) {
            shape = shapeAt(c);

            shape.setBorderWidth(width);
        }
    }

    public void setColor(java.awt.Color aColor) {
        Shape shape;
        int c;

        for(c = 0; c < this.elementCount; c++) {
            shape = shapeAt(c);

            shape.setColor(aColor);
        }
    }

    public void setBorderColor(java.awt.Color aColor) {
        Shape shape;
        int c;

        for(c = 0; c < this.elementCount; c++) {
            shape = shapeAt(c);

            shape.setBorderColor(aColor);
        }
    }

    public void setFillColor(java.awt.Color aColor) {
        Shape shape;
        int c;

        for(c = 0; c < this.elementCount; c++) {
            shape = shapeAt(c);

            shape.setFillColor(aColor);
        }
    }

    /*
     * public void setWrapping(boolean wrap) { Shape shape; int c;
     * 
     * for(c = 0; c < this.elementCount; c++) { shape = shapeAt(c);
     * 
     * shape.setWrapping(wrap); } }
     */

    public void setTransparency(int alphaLevel) {
        Shape shape;
        int c;

        for(c = 0; c < this.elementCount; c++) {
            shape = shapeAt(c);

            shape.setTransparency(alphaLevel);
        }
    }

    public boolean contains(Point p) {
        boolean Contains = false;

        for(int c = 0; c < this.elementCount; c++) {
            if (this.elementAt(c).contains(p))
                Contains = true;
        }

        return Contains;
    }
}
