package artifice;
/* Circle.java
 * 
 * Models a Circle with floating point precision.
 * Used by IntersectionSimulation.java
 * 
 * @see http://reas.com/iperimage.php?section=works&work=preprocess_s&images=4&id=1&bgcolor=FFFFFF
 * @see http://www.complexification.net/gallery/machines/interAggregate/index.php
 * 
 * @author Travis Fischer (tfischer)
 * @version January 5, 2006
 */
import static artifice.ArtificeConstants.*;
import static fisch.Utilities.random;

import java.awt.*;

public class Circle {
    protected int _radius, _radiusSquared, _diameter;
    protected float _x, _y;
    protected float _dX, _dY;
    protected int _color;
    
    public Circle() {
        this(random(MIN_CIRCLE_RADIUS, MAX_CIRCLE_RADIUS));
    }
    
    public Circle(int radius) {
        this(random(0, SIMULATION_WIDTH - 1), random(0, SIMULATION_HEIGHT - 1), 
                radius);
    }
    
    public Circle(int x, int y, int radius) {
        this(x, y, radius, new Color(255, 255, 255, 18), 0);
    }
    
    public Circle(int x, int y, int radius, Color color, int movement) {
        super();
        
        _x = x;
        _y = y;
        _color  = color.getRGB();
        
        this.setRadius(radius);
        this.setMovement(movement);
    }
    
    public final void setRadius(int radius) {
        _radius   = radius;
        _radiusSquared = radius * radius;
        _diameter = radius << 1;
    }
    
    public final void setMovement(int movement) {
        _dX = 0;
        _dY = 0;
        
        if (movement != 2) {
            _dX = 0.5f + random();
            _dX *= (randomBoolean() ? 1 : -1);
        }
        
        if (movement != 1) {
            _dY = 0.5f + random();
            _dY *= (randomBoolean() ? 1 : -1);
        }
    }
    
    public void update() {
        _x += _dX;
        _y += _dY;
        
        int rand = random(0, 2);
        
        // Wrap circle around the edge of the Simulation
        if (_x + _radius < 0 && _dX < 0)
            _x = SIMULATION_WIDTH + _radius + rand;
        else if (_x - _radius >= SIMULATION_WIDTH && _dX > 0)
            _x = -_radius - rand;
        
        if (_y + _radius < 0 && _dY < 0)
            _y = SIMULATION_HEIGHT + _radius + rand;
        else if (_y - _radius >= SIMULATION_HEIGHT && _dY > 0)
            _y = -_radius - rand;
    }
    
    // Accessors for instance vars
    public float getX()      { return _x; }
    public float getY()      { return _y; }
    public int getRadius()   { return _radius; }
    public int getRadiusSquared() { return _radiusSquared; }
    public int getDiameter() { return _diameter; }
    public int getColor()    { return _color; }
    
    public boolean intersects(Circle circle) {
        int rad    = circle.getRadius();
        float xDif = circle.getX() - _x;
        float yDif = circle.getY() - _y;
        float dist = (float)Math.sqrt(xDif * xDif + yDif * yDif);
        
        // Reject if dist btwn circles is greater than their radii combined
        if (dist > _radius + rad)
            return false;
        
        // Reject if one circle is inside of the other
        return (dist >= Math.abs(rad - _radius));
    }
    
    public Point[] getIntersection(Circle circle) {
        int rad  = circle.getRadius();
        float cirX = circle.getX();
        float cirY = circle.getY();
        float xDif = cirX - _x;
        float yDif = cirY - _y;
        float distSquared = xDif * xDif + yDif * yDif;
        float dist = (float)Math.sqrt(distSquared);
        
        // Reject if dist btwn circles is greater than their radii combined
        if (dist > _radius + rad)
            return null;
        
        // Reject if one circle is inside of the other
        if (dist < Math.abs(rad - _radius))
            return null;
        
        xDif /= dist;
        yDif /= dist;
        
        // Distance from this circle to line cutting through intersections
        float a = (_radiusSquared - circle.getRadiusSquared() + distSquared) / (2 * dist);
        
        // Coordinates of midpoint of intersection
        float pX = _x + a * xDif; 
        float pY = _y + a * yDif;

        // The distance from (x, y) to either of the intersection points
        float h = (float)Math.sqrt(_radiusSquared - a * a);
        
        // Check if there's only one intersection
        if (h == 0)
            return new Point[] { new Point((int)pX, (int)pY) };
        
        xDif *= h;
        yDif *= h;
        int x1 = (int)(pX + yDif);
        int y1 = (int)(pY - xDif);
        int x2 = (int)(pX - yDif);
        int y2 = (int)(pY + xDif);
        
        // There's (at least) two intersections
        return new Point[] { new Point(x1, y1), new Point(x2, y2) };
    }
    
    public void paint(Graphics2D brush) {
//        setAntialiasing(brush, true);
//        brush.setStroke(new BasicStroke(1));
//        brush.setColor(_color);
        
        brush.drawOval((int)_x - _radius, (int)_y - _radius, _diameter, _diameter);
    }
}
