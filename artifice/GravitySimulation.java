package artifice;
/* GravitySimulation.java
 * 
 * N-Body simulation where every body enacts a force 
 * on every other body in the system.
 * 
 * @see http://freespace.virgin.net/hugo.elias/models/
 * 
 * @author Travis Fischer (tfischer)
 * @version Dec 29, 2006
 */
import static artifice.ArtificeConstants.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.*;

public class GravitySimulation extends Simulation {
    private Body[] _bodies;
    
    // Scaling factor that affects the mass of the Bodies, effectively scaling 
    // the force a Body enacts on another Body
    private int _simulationSpeed;
    
    // Drawing variation (either clean or aggregate)
    private int _variation;
    
    // Bodies in the system are spawned in either a radial or random fashion
    private int _initialState;
    
    // Number of particles the system is currently recognizing/updating
    private int _noParticles;
    
    public GravitySimulation(DrawingPanel dp, SimulationTimer timer) {
        super(dp, timer, BufferedImage.TYPE_INT_RGB);
        
        _bodies = new Body[MAX_GRAVITY_NO_PARTICLES];
        _simulationSpeed = DEFAULT_GRAVITY_SPEED;
        _noParticles     = DEFAULT_GRAVITY_NO_PARTICLES;
        _variation       = 0;
        _initialState    = 0;
    }
    
    public final void reset() {
//        _bodies[0] = new Body(PARTICLE_ORIGIN_X, PARTICLE_ORIGIN_Y, 100);
        
        if (_initialState == 1) {
            // Initial State of Bodies arranged randomly
            for(int i = 0; i < _bodies.length; i++)
                _bodies[i] = new Body();
        } else {
            // Initial State of Bodies arranged in a Circular fashion
            for(int i = 0; i < _bodies.length; i++) {
                float theta = i * TWO_PI / _noParticles;
                float x = PARTICLE_ORIGIN_X + 150f * (float)Math.cos(theta);
                float y = PARTICLE_ORIGIN_Y + 150f * (float)Math.sin(theta);
                
                _bodies[i] = new Body(x, y);
            }
        }
        
        // Clear the offscreen buffer to white
        Graphics2D brush = (Graphics2D)_offscreen.getGraphics();
        brush.setColor(Color.WHITE);
        brush.fillRect(0, 0, SIMULATION_WIDTH, SIMULATION_HEIGHT);
    }
    
    public void update() {
        // For each body in the system, enact a force on every 
        // other body in the system; Running time O(_noParticles ^ 2)
        for(int i = 0; i < _noParticles; i++) {
            
            for(int j = i + 1; j < _noParticles; j++) {
                float xDif = _bodies[j]._x - _bodies[i]._x;
                float yDif = _bodies[j]._y - _bodies[i]._y;
                float invDistSquared = 1 / (xDif * xDif + yDif * yDif);
                
//                if (invDistSquared > 0.1f)
//                    invDistSquared = 1 / invDistSquared;
//                else invDistSquared = 0;
                
                // Force is inversely proportional to the distance squared
                xDif *= invDistSquared;
                yDif *= invDistSquared;
                
                _bodies[i].addAcceleration(xDif * _bodies[j]._mass, 
                                           yDif * _bodies[j]._mass);
                _bodies[j].addAcceleration(-xDif * _bodies[i]._mass, 
                                           -yDif * _bodies[i]._mass);
            }
            
//            if (i > 0) {
                boolean result = _bodies[i].update();
//              if (!result)
//              _bodies[i] = new Body();
//            }
        }
        
        this.repaint();
    }

    public void paintSimulation(Graphics2D brush) {
        if (_variation == 1 && _offscreen != null) {
            // Draw an aggregate image _offscreen of the bodies for each frame
            brush.drawImage(_offscreen, 0, 0, _dp);
            
            brush = (Graphics2D)_offscreen.getGraphics();
        } // else paint with the default brush, effectively painting the bodies
        // onto a clean canvas each frame
        
        for(int i = 0; i < _noParticles; i++) {
            _bodies[i].paint(brush);
        }
    }
    
    public void setNoParticles(int noParticles) {
        _noParticles = noParticles;
    }
    
    public void setSpeed(int speed) {
        if (speed != _simulationSpeed) {
            boolean running = _timer.isRunning();
            _timer.stop();
            _simulationSpeed = speed;
            
            for(int i = 0; i < _bodies.length; i++)
                _bodies[i].resetMass();
            
            this.restart();
//            if (running)
//                _timer.start();
        }
    }
    
    public void setVariation(int variation) {
        _variation = variation;
    }
    
    public void setInitialState(int state) {
        if (_initialState != state) {
            _initialState = state;
            
            this.restart();
        }
    }
    
    private class Body extends gfx.Ellipse {
        // Position
        private float _x;
        private float _y;
        
        // Velocity vector <_dX, _dY>
        private float _dX, _dY;
        private float _mass;
        private int _size;
        
        /*
         * Creates a Body at a random location
         */
        public Body() {
            this(random(SIMULATION_WIDTH / 4, 3 * SIMULATION_WIDTH / 4), 
                 random(SIMULATION_HEIGHT / 4, 3 * SIMULATION_HEIGHT / 4));
        }
        
        /*
         * Creates a body at a given location with a random mass
         */
        public Body(float x, float y) {
            this(x, y, random(10, 45));
        }
        
        /*
         * Creates a body at the given location with the given mass
         */
        public Body(float x, float y, int mass) {
            super(_dp, x, y, mass, mass);
            
            _x = x;
            _y = y;
            _size = mass;
            
            _dX = 0;
            _dY = 0;
            
            this.resetMass();
            this.setAntialiasing(true);

            // Select a random color from within a predefined color palette
            int offset = 3 * random(0, (SUBSTRATE_COLORS.length - 1) / 3);
            this.setColor(new Color(SUBSTRATE_COLORS[offset],  
                    SUBSTRATE_COLORS[offset + 1], 
                    SUBSTRATE_COLORS[offset + 2], 48));
            
            this.setBorderColor(new Color(0, 0, 0, 78));
            this.setBorderWidth(random(1, 3));
        }
        
        /* _mass effects how fast bodies will be pulled towards each other, and 
         * by globally scaling the masses of all of the bodies in the system
         * (with _simulationSpeed), the 'speed' of the simulation can be changed 
         */ 
        public final void resetMass() {
            _mass = ((float)_size) / (MAX_GRAVITY_SPEED - _simulationSpeed);
        }
        
        public void addAcceleration(float accelX, float accelY) {
            _dX += accelX;
            _dY += accelY;
        }
        
        public boolean update() {
            _x += _dX;
            _y += _dY;
            
//            int size = (_size >> 1);
//            float x = _x - size, y = _y - size;
//            if (x < 0 || y < 0 || x >= SIMULATION_WIDTH || 
//                    y >= SIMULATION_HEIGHT) {
//                // Limit the velocity of this Body w/ some friction
//                _dX *= 0.9999f;
//                _dY *= 0.9999f;
//            }
            
            return true;
        }
        
        // Returns whether or not this Body intersects the given Body (currently unused)
        public boolean intersects(Body body) {
            int radius = (_size >> 1);
            int rad    = (body._size >> 1);
            float xDif = body._x - _x;
            float yDif = body._y - _y;
            float dist = (float)Math.sqrt(xDif * xDif + yDif * yDif);
            
            // Reject if dist btwn circles is greater than their radii combined
            if (dist > radius + rad)
                return false;
            
            // Reject if one circle is inside of the other
            return (dist >= Math.abs(rad - radius));
        }
        
        public void paint(Graphics2D brush) {
            int size = (_size >> 1);
            this.setLocation(_x - size, _y - size);
            
            super.paint(brush);
        }
    }
}

