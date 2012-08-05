package artifice;
/* Particle.java
 * 
 * Models a colored Particle revolving around another 
 * moving Particle (which may or may not be itself).
 * Used by OrbitalSimulation.java
 * 
 * @author J. Tarbell
 * @port by Travis Fischer (tfischer)
 * @see http://www.complexification.net/
 */
import static artifice.ArtificeConstants.*;

import java.awt.image.*;
import java.awt.*;
import java.util.Vector;

public class Particle {
    private static BufferedImage _container;
    public static int ZOOM = SIMULATION_WIDTH;
    
    // Position
    private float _x;
    private float _y;
    
    // Direction specified in radians
    private float _theta, _dTheta, _radius, _radiusRandom;
    
    private int _color, _id;
    private int _depth;
    
    private Particle _center;
    
    public static void setCanvas(BufferedImage canvas) {
        _container = canvas;
    }
    
    public Particle(int id) {
        super();
        
        _id = id;
        _depth = 0;
    }
    
    public void initialize(float x, float y, Particle center) {
        // Particle which this Particle will orbit around
        _center = center;
        
        if (_id == _center.getID()) {
            _radius = _theta = _dTheta = 0;
            _x      = PARTICLE_ORIGIN_X + 8 - random(0, 16);
            _y      = PARTICLE_ORIGIN_Y + 8 - random(0, 16);
            
//            float theta = random() * TWO_PI;
//            _x = PARTICLE_ORIGIN_X + 80 * (float)Math.sin(theta);
//            _y = PARTICLE_ORIGIN_Y + 80 * (float)Math.cos(theta);
        } else {
            _x      = x;
            _y      = y;
            
            _depth  = _center.getDepth() + 1;
            
            // radius inversely proportional to depth
            _radiusRandom = random();
            this.resetRadius();
            
            _theta  = TWO_PI * random();//(float)(-Math.PI / 2);
//            _radius = (float)SIMULATION_HEIGHT / (8 * (_id + 1));
            _dTheta = .0003f + random() * .0199f / (_depth + 1);
            if (randomBoolean()) _dTheta *= -1;
        }
        
        // Initialize Particle to a random color within the predefined color Palette
//        _color = _colorPalette.elementAt(random(0, _colorPalette.size() - 1));
//        _color = ORBITAL_PALETTE[random(0, ORBITAL_PALETTE.length - 1)];
        
        int offset = 3 * random(0, (SUBSTRATE_COLORS.length - 1) / 3);
        _color = ((SUBSTRATE_COLORS[offset] & 0xFF) << 16) | 
                 ((SUBSTRATE_COLORS[offset + 1] & 0xFF) << 8) | 
                  (SUBSTRATE_COLORS[offset + 2] & 0xFF);
    }
    
    public final void resetRadius() {
        _radius = 1 + _radiusRandom * ((0.4f * ZOOM) / _depth);
    }
    
    public boolean update() {
        _theta += _dTheta;
        
//        _dTheta *= 0.9996; // Make particles slow down gradually
        
        // Update particle's position
        _x = _center._x + (float)(_radius * Math.cos(_theta));
        _y = _center._y + (float)(_radius * Math.sin(_theta));
        
        int realX = Math.round(_x), realY = Math.round(_y);
        
        // Make line "fuzzy" to prevent the uniform 
        // aliases which are otherwise produced
        int x = (int)(_x + FUZZ - Math.random() * TWICE_FUZZ);
        int y = (int)(_y + FUZZ - Math.random() * TWICE_FUZZ);
        
        // Check if it's time for this particle to die
        if (!this.isValid(x, y) || !this.isValid(realX, realY))
            return false;
        
        this.setPixel(x, y, 0x42000000 | _color);
        
        if (realX != x || realY != y)
            this.setPixel(realX, realY, 0x42000000 | _color);
//        _container.setRGB(x, y, 0xff000000); // Black
//        _container.setRGB(realX, realY, 0xff000000);
        
        
        // draw orbit path
        float theta = TWO_PI * random();
        x = (int)(_center._x + _radius * Math.cos(theta));
        y = (int)(_center._y + _radius * Math.sin(theta));
        if (this.isValid(x, y))
            this.setPixel(x, y, 0x18000000 | _color);
        
        // draw parent line
        theta = random(); // 0 to 1
        x = (int)(_x + theta * (_center._x - _x));
        y = (int)(_y + theta * (_center._y - _y));
        if (this.isValid(x, y))
            this.setPixel(x, y, 0x18000000 | _color);
        
        return true;
    }
    
//    // Blends a src ARGB pixel onto a destination RGB pixel
    private static void setPixel(int x, int y, int color) {
        int oldColor = _container.getRGB(x, y);
        int alpha = (color >>> 24);
        
        // Blend the two colors together, weighting their relative contributions
        // according to the new color's alpha value
        int k = (255 - alpha);  // difference in alpha values
        int r = ((alpha * ((color & 0x00FF0000) >>> 16) + k * ((oldColor & 0x00FF0000) >> 16)) << 8) & 0x00FF0000;
        int g =  (alpha * ((color & 0x0000FF00) >>>  8) + k * ((oldColor & 0x0000FF00) >>  8)) & 0x0000FF00;
        int b =  (alpha *  (color & 0x000000FF)         + k * ((oldColor & 0x000000FF)      )) >> 8;
        
        _container.setRGB(x, y, (r | g | b));
    }
    
    protected boolean isValid(int x, int y) {
        return (x >= 0 && x < SIMULATION_WIDTH && 
                y >= 0 && y < SIMULATION_HEIGHT);
    }
    
    private int getID() {
        return _id;
    }
    
    private int getDepth() {
        return _depth;
    }
}
