package primordial;
/**
 * @author Travis Fischer
 * @date   12/25/2006
 * 
 *   Models a single-celled organism moving around randomly 
 * in a given container.
 */
import static fisch.Utilities.*;
import javax.swing.JPanel;
import java.awt.*;

public class Cell extends gfx.Ellipse implements Organism {
    // Cartesian Position of center of Cell within Container
    public float _x, _y;
    protected float _size, _girth;
    
    // Direction specified in radians
    protected float _theta;
    
    // Speed of Cell in current direction
    protected float _speed;
    
    // Friction with water
    protected float _viscosity;
    
    // The velocity at which the Cell is currently turning (modifies _theta)
    protected float _rotationalVelocity;
    
    // Constraints placed on the turning velocity of the Cell
    protected float _minRotationalVelocity, _maxRotationalVelocity;
    
    // Constraints placed on the speed of the Cell
    protected float _minSpeed, _maxSpeed;
    
    public Cell(JPanel container) {
        this(container, random(8, 50));
    }
    
    public Cell(JPanel container, Variation variation) {
        this(container, 0, 0, variation);
    }
    
    public Cell(JPanel container, float size) {
        this(container, 0, 0, size);
    }
    
    public Cell(JPanel container, float x, float y, float size) {
        this(container, x, y, size, 0);
    }
    
    public Cell(JPanel container, float x, float y, Variation variation) {
        this(container, x, y, random(8, 50), 0);
        
//        this.setVariation(variation);//TODO Fix
    }
    
    public Cell(JPanel container, float x, float y, float size, float girth) {
        super(container, x - size / 2, y - size / 2, size, size);
        //(char)(random('a', 'z')) + "", 
        
        _x     = x;
        _y     = y;
        _size  = size / 2;
        _girth = girth;
        
        // Friction with water
        _viscosity = 0.9f + 0.1f * random();
        
        _minRotationalVelocity = -0.06f;
        _maxRotationalVelocity =  0.06f;
        
        _minSpeed = 2;
        _maxSpeed = 10;
        
        _speed = 0;//random(_minSpeed, _maxSpeed);
        _theta = TWO_PI * random();
        
        // Setup Visual Style of Cell
        // --------------------------
        this.setWrapping(true);
        this.setAntialiasing(true);
        
        this.setPaint(new Color(127,127,127,58));//255,255,255,18));
//        this.setBorderWidth(1);
//        this.setBorderColor(new Color(0, 0, 0, 28));//0,0,0,48
    }
    
    public boolean update() {
        // Randomly update speed of organism
        // ---------------------------------
        _speed += 0.03f - 0.06f * random();

        // Ensure organism doesn't move too fast
        _speed = this.cap(_speed, _minSpeed, _maxSpeed);
        
        // Randomly update direction of organism
        // -------------------------------------
        _rotationalVelocity += randomSigned(0.015f);// - 0.03f * random();
        
        // Ensure organism doesn't turn too fast
        _rotationalVelocity = this.cap(_rotationalVelocity, _minRotationalVelocity, 
                _maxRotationalVelocity);
        
        // Organism will swim around randomly
        _theta += _rotationalVelocity;
        _rotationalVelocity *= _viscosity; // friction with water
        
//        this.setAngle(_theta * 180 / Math.PI - 120, 240);
        
        // Update organism's position (determined by its 'head')
        _x += (float)(_speed * Math.cos(_theta)); 
        _y += (float)(_speed * Math.sin(_theta));
        
        return true;
    }
    
    public float cap(float value, float min, float max) {
        if (value < min)
            value = min;
        else if (value > max)
            value = max;
        
        return value;
    }
    
    public float getSize() {
        return _size;
    }
    
    public void setSize(float size) {
        float sizeDif = (_size * 2 - size) / 2;
        _x += sizeDif;
        _y += sizeDif;//TODO test/check this method
        
        super.setSize(size, size);
        
        _size = size / 2;
    }
    
    public float getGirth() {
        return _girth;
    }
    
    public void setGirth(float girth) {
        _girth = girth;
    }
    
    public final void setRotationalBounds(float min, float max) {
        _minRotationalVelocity = min;
        _maxRotationalVelocity = max;
    }
    
    public final void setSpeedBounds(int length) {
        _minSpeed = 1 + (length - 16) * 0.25f;
        _maxSpeed = 7 - (30 - length) * 0.25f;
    }
    
    /* @overridden */
    public void setLocation(float x, float y) {
        _x = x;
        _y = y;
    }
    
    /* @overridden */
    public void setLocation(double x, double y) {
        _x = (float)x;
        _y = (float)y;
    }
    
//    public void setScreenLocation(double x, double y) {
//        double newX = x;
//        double newY = y;
//        
//        if (_wrapping) {
//            float width  = (_container.getWidth());// + _size * 4);
//            float height = (_container.getHeight());// + _size * 4);
//            
//            if (_container.getWidth() > 0)
//                newX = Math.abs(newX) % width;
//            if (_container.getHeight() > 0)
//                newY = Math.abs(newY) % height;
//            
//            //newX -= _size * 2; newY -= _size * 2;
//            
//            // Properly wrap negative locations
//            if (x < 0)
//                newX = width - newX;
//            if (y < 0)
//                newY = height - newY;
//        }
//        
//        // Move the shape to the newly calculated newX and newY coordinates
//        _shape.setFrame(newX, newY, _shape.getWidth(), _shape.getHeight());
//    }
//    
    public void setVariation(Variation variation) {
        if (variation == Variation.PRIMORDIAL_DEFAULT) {
            this.setPaint(new Color(127, 127, 127, 58));
            this.setBorderWidth(0);
        } else if (variation == Variation.PRIMORDIAL_SNAKESKIN) {
            this.setPaint(new Color(255, 255, 255, 18));
            this.setBorderWidth(1);
            this.setBorderColor(new Color(0, 0, 0, 48));
        }
    }
    
    public void paint(Graphics2D brush) {
        super.setLocation(_x - _size, _y - _size);
        super.paint(brush);
    }
}
