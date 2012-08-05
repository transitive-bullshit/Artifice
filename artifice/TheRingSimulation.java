package artifice;
import static artifice.ArtificeConstants.*;

import java.util.*;
import java.awt.image.*;
import java.awt.event.*;
import java.awt.*;

/**
 * @author Travis Fischer
 * 
 * Concept and code base by J. Tarbell
 * <a href="http://www.complexification.net/gallery/machines/binaryRing/">Complexification</a>
 */
public class TheRingSimulation extends Simulation {
    private static final int BLOOD_COLOR = 0x18BF2107;  // dark red
    
//    private static final Color BLOOD = new Color(0xBF, 0x21, 0x07, 0x18);
//    private static final Color BLACK = new Color(0, 0, 0, 0x18);
//    private static final Color WHITE = new Color(0xFF, 0xFF, 0xFF, 0x18);
//    private Graphics2D _brush;
    
    private int _noRingParticles;
    private int _originRadius;
    private Vector<RingParticle> _ringParticles;
    // Color that new particles will be drawn when (re)spawned
    private boolean _blackout;
    
    public TheRingSimulation(DrawingPanel dp, SimulationTimer timer) {
        super(dp, timer, BufferedImage.TYPE_INT_RGB);
        
        _noRingParticles = 5000;
        _originRadius    = 30;
        _ringParticles   = new Vector<RingParticle>(_noRingParticles);
    }
    
    // Initializes and starts the Simulation
    public void reset() {
        _blackout = false;
        
//        _brush = (Graphics2D) _offscreen.getGraphics();
        
        double theta = TWO_PI * Math.random();
        double thetaAdd = (Math.random() * 4 + TWO_PI) / _noRingParticles;
        _ringParticles.clear();
        
        // Initial particles sling-shot around ring origin
        for(int i = 0; i < _noRingParticles; i++, theta += thetaAdd) {
            double emitX = PARTICLE_ORIGIN_X + _originRadius * Math.sin(theta);
            double emitY = PARTICLE_ORIGIN_Y + _originRadius * Math.cos(theta);
            
            _ringParticles.add(new RingParticle(emitX, emitY, theta / 2));
        }
    }
    
    
    public void update() {
        // Move and Draw all of the RingParticles
        for(Enumeration<RingParticle> e = _ringParticles.elements(); e.hasMoreElements();)
            e.nextElement().move();
//        for(int i = 0; i < _noRingParticles; i++)
//            _ringParticles.elementAt(i).move();
        
        // Randomly switch between blackout periods
        if (Math.random() > 0.995)
            _blackout = !_blackout;
        
        this.repaint();
    }
    
    private class RingParticle {
        private double _dX, _dY;
        private double _x, _y;
        private int _color;
        private int _age;
        
        public RingParticle(double x, double y, double theta) {
            // Position
            _x = x;
            _y = y;
            
            // Velocity
            _dX = 2 * Math.cos(theta);
            _dY = 2 * Math.sin(theta);
            
            // Black or White w/ alpha value of 24
            _color = (_blackout ? 0x18000000 : 0x18ffffff);
            
//            _color = _blackout;
            _age = random(0, 200);
        }
        
        public void move() {
            // Record Particle's Old Position
            int oldX = (int)_x, oldY = (int)_y;
            
            // Update Particle's Position
            _x += _dX;
            _y += _dY;
            
            // Apply slight, random changes to Particle's Velocity
//            _dX += (random(0, 100) - random(0, 100)) * 0.005f;
//            _dY += (random(0, 100) - random(0, 100)) * 0.005f;
            
            _dX += (Math.random() - Math.random()) * 0.5f;
            _dY += (Math.random() - Math.random()) * 0.5f;
            
//            if (oldX != (int)_x || oldY != (int)_y) {
//            Color color = (_color == 0x180000 ? ((Math.random() > 0.85) ? 
//                    BLOOD : BLACK) : WHITE);
//            
//            Graphics2D brush = (Graphics2D) _offscreen.getGraphics();
//            brush.setColor(color);
//            brush.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//                    RenderingHints.VALUE_ANTIALIAS_OFF);
//            brush.drawLine(oldX, oldY, (int)_x, (int)_y);
//            brush.dispose();
//            }
            
            if (oldX != (int)_x || oldY != (int)_y) {
                // Randomly intersperse blood color with Black
                int color = ((0x18000000 == _color && Math.random() > 0.85) ? 
                        BLOOD_COLOR : _color);
                
                // Draw a line connecting old particle's Position to new Position
                
                drawClippedLine(oldX, oldY, (int)_x, (int)_y, color);
                
                if (_x < 0 || _x >= SIMULATION_WIDTH || 
                        _y < 0 || _y >= SIMULATION_HEIGHT || 
                        ++_age > 200)
                    this.respawn();
            }
        }
        
        // Die and be reborn
        private void respawn() {
            double theta = TWO_PI * Math.random();
            
            // Initial Position of new Particle on Radius of Origin
            _x = PARTICLE_ORIGIN_X + _originRadius * Math.sin(theta);
            _y = PARTICLE_ORIGIN_Y + _originRadius * Math.cos(theta);
            
            // Reset initial velocity to zero
            _dX = 0;
            _dY = 0;
            
            _age   = 0;
//            _color = _blackout;
            _color = (_blackout ? 0x18000000 : 0x18ffffff);
        }
    }
    
    public void mouseClicked(MouseEvent e) {
        _blackout = !_blackout;
        
        //this.restart();
    }
    
    public void setOriginRadius(int newRadius) {
        if (newRadius > 0)
            _originRadius = newRadius;
    }
    
    public void setNoParticles(int noParticles) {
        if (noParticles > _noRingParticles) {
            int diff = noParticles - _noRingParticles;
            double thetaAdd = TWO_PI / diff;
            double theta    = 0;
            
            // Add particles to the Simulation
            for(int i = 0; i < diff; i++, theta += thetaAdd) {
                 double emitX = PARTICLE_ORIGIN_X + _originRadius * Math.sin(theta);
                 double emitY = PARTICLE_ORIGIN_Y + _originRadius * Math.cos(theta);
                    
                _ringParticles.add(new RingParticle(emitX, emitY, theta / 2));
            }
        } else _ringParticles.setSize(noParticles);
        
        _noRingParticles = noParticles;
    }
    
//    // Blends a src ARGB pixel onto a destination ARGB pixel
//    private final void setPixel(int x, int y) {
//        int oldColor = _offscreen.getRGB(x, y);
//        
//        int r = ((_currentRedMul   + 231 * ((oldColor & 0x00FF0000) >> 16)) << 8) & 0x00FF0000;
//        int g =  (_currentGreenMul + 231 * ((oldColor & 0x0000FF00) >>  8)) & 0x0000FF00;
//        int b =  (_currentBlueMul  + 231 * ((oldColor & 0x000000FF)      )) >> 8;
//        
//        _offscreen.setRGB(x, y, (r | g | b));
//    }
}
