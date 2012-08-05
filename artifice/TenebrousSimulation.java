package artifice;
/* TenebrousSimulation.java
 * 
 * Curved trees that recursively spawn more curved 
 * trees (branches) until they become too small.
 * 
 * @inspiration http://flickr.com/photos/98177330@N00/335363769/in/pool-processing/
 * 
 * @author Travis Fischer (tfischer)
 * @version January 7, 2006
 */
import static artifice.ArtificeConstants.*;
import java.awt.geom.Ellipse2D;
import gfx.ImageUtils;

import java.util.Vector;
import java.awt.event.MouseEvent;
import java.awt.image.*;
import java.awt.*;

public class TenebrousSimulation extends Simulation {
    private static final Color TREE_STROKE_COLOR = new Color(127, 127, 127, 50);
    private static final int NO_INITIAL_TREES = 5;
    private static final int MAX_TREE_SIZE    = 60;
    private static final float MIN_TREE_SIZE = (FULL_SCREEN ? 1.5f : 0.9f);
    
    // Collection of Trees that constitute the Simulation
    private Vector<Tree> _trees;
    
    // Shared by all Tree Objects in _trees; used for painting
    private Ellipse2D.Float _ellipse;
    
    // User-customizable options via TenebrousControl
    private int _variation;
    private float _speed;
    private int _coloredType;
    
    public TenebrousSimulation(DrawingPanel dp, SimulationTimer timer) {
        super(dp, timer);
        
        // ArtificeConstants.extractColorsFromImage(_dp, 
        //"Images/shiftingLinesPalette.png");
        _ellipse = new Ellipse2D.Float();
        _trees   = new Vector<Tree>();
        _variation = 0;
        _speed     = TENEBROUS_DEFAULT_SPEED;
        _coloredType = 0;
    }
    
    public final void reset() {
        _trees.clear();
        for(int i = 0; i < NO_INITIAL_TREES; i++) {
            _trees.add(new Tree());
        }
        
        // Clear the offscreen buffer to white
        Graphics2D brush = (Graphics2D)_offscreen.getGraphics();
        brush.setColor(Color.WHITE);
        brush.fillRect(0, 0, SIMULATION_WIDTH, SIMULATION_HEIGHT);
        
//        BufferedImage back = ImageUtils.getBufferedImage(_dp, "Images/vanish.jpg");
//        if (back != null)
//            brush.drawImage(back, 0, 0, SIMULATION_WIDTH, SIMULATION_HEIGHT, _dp);
    }
    
    public void update() {
        Graphics2D brush = (Graphics2D) _offscreen.getGraphics();
        brush.setStroke(new BasicStroke(1));
        setAntialiasing(brush, true);
        
        // Update and Paint all of the Trees
        for(int i = 0; i < _trees.size(); i++) {
            Tree curTree = _trees.elementAt(i);
            
            // Check if the current Tree died during its update
            if (!curTree.update()) {
                _trees.removeElementAt(i--);
            } else //if (_variation != 2)
                curTree.paint(brush);
        }
        
        if (FULL_SCREEN) {
            if (_trees.size() <= 0 || Math.random() > 0.98f)
                this.repaint();
        }else this.repaint();
    }
    
    // Add a new Tree at the spot where the user clicked
    public void mouseClicked(MouseEvent e) {
        _trees.add(new Tree(e.getX(), e.getY()));
    }
    
    public void paintSimulation(Graphics2D brush) {
        if (_variation == 2) {
            setAntialiasing(brush, true);
            
            // Show underlying structure instead of aggregate _offscreen image
            for(int i = 0; i < _trees.size(); i++)
                _trees.elementAt(i).paint(brush);
        } else brush.drawImage(_offscreen, 0, 0, _dp);
    }
    
    // Adjust options via TenebrousControl Panel
    public void setVariation(int variation) {
        _variation = variation;
    }
    
    public void setSpeed(float speed) {
        _speed = speed;
    }

    public void setColoredType(int colored) {
        _coloredType = colored;
    }
    
    private class Tree {
        // Position of this tree
        private float _x, _y;
        
        // Direction
        public float _theta, _dTheta;
        
        // current Thickness of this Tree
        private float _size, _radius;
        
        // No frames until this Tree will branch next
        private int _length;
        
        private Color _color;
        
        private boolean _isColored;
        
        public Tree() {
            this(random(0, SIMULATION_WIDTH - 1), 
                 random(0, SIMULATION_HEIGHT - 1));
        }
        
        public Tree(float x, float y) {
            this(x, y, TWO_PI * random(), random(30, MAX_TREE_SIZE));
        }
        
        public Tree(Tree trunk) {
            this(trunk, randomBoolean());
        }
        
        // Spawns a branch given a main trunk
        public Tree(Tree trunk, boolean sign) {
            this(trunk, 
                 trunk._theta + (sign ? 1 : -1) * 
                 (10 + random() * (MAX_TREE_SIZE - 10)) * 180 / PI);
        }
        
        public Tree(Tree trunk, float theta) {
            this(trunk._x, trunk._y, theta, trunk._size - random() * (trunk._size * 0.5f));
        }
        
        public Tree(float x, float y, float theta, float size) {
            super();
            
            _x = x;
            _y = y;
            _theta  = theta;
            _size   = size;
            _radius = size / 2;
            
            _color  = randomColor(TENEBROUS_PALETTE, 0xff);
            _isColored = randomBoolean();
            
//            _initialTheta = theta % TWO_PI;
            _dTheta = random() * 0.005f;
            if (randomBoolean())
                _dTheta = -_dTheta;
            
            this.resetLength();
        }
        
        // Number of frames to go until this Tree spawns another Branch
        private final void resetLength() {
            _length = random(40, 180);//(int)(_size * random());
        }
        
        public boolean update() {
//            float thetaDiff = Math.signum(_theta - _initialTheta);
            
            _theta += _dTheta;
            _size -= (_size / (MAX_TREE_SIZE << 4));// 0.001f;
            
            // Stop growing if this Tree grows too small
            if (_size <= MIN_TREE_SIZE)// || thetaDiff != Math.signum(_theta - _initialTheta))
                return false;
            
            _x += _speed * (float)Math.cos(_theta);
            _y += _speed * (float)Math.sin(_theta);
            
            _radius = _size / 2;
            if (_x + _radius < 0 || _x - _radius >= SIMULATION_WIDTH || 
                _y + _radius < 0 || _y - _radius >= SIMULATION_HEIGHT)
                return false;
            
            if (_length-- <= 0) { // spawn a new branch
                _trees.add(new Tree(this));
                
                this.resetLength();
            } else {
                float rand = random();
                
                if (rand < 0.001f) {
                    // Split into two branches and stop this Tree's growth
                    _trees.add(new Tree(this, true));
                    _trees.add(new Tree(this, false));
                    
                    return false;
                } else if (rand > 0.995f) {
                    // possible to change direction
                    _dTheta = -_dTheta;
                }
            }
            
            return true;
        }
        
        public void paint(Graphics2D brush) {
            float half = _radius / 2;
            
            _ellipse.setFrame(_x - half, _y - half, _radius, _radius);
            
            if (_variation != 1) {
                brush.setColor(TREE_STROKE_COLOR);
                brush.draw(_ellipse);
            }
            
            if ((_coloredType == 0 && _isColored) || _coloredType == 1)
                brush.setColor(_color);
            else brush.setColor(Color.BLACK);
            
            brush.fill(_ellipse);
        }
    }

    /* RGB triples constituting a list of predefined colors
     * taken from "Images/shiftingLinesPalette.png"
     * (mainly blue and green hues)
     * 
     * @see method Simulation.randomColor
     */
    private static final int[] TENEBROUS_PALETTE = {
        59, 70, 76, 69, 88, 90, 75, 90, 91, 84, 93, 99, 83, 100, 99, 
        79, 104, 102, 84, 108, 116, 85, 112, 117, 94, 120, 107, 92, 115, 114, 
        98, 114, 114, 99, 122, 115, 104, 118, 121, 99, 122, 122, 99, 123, 130, 
        100, 124, 139, 104, 124, 147, 108, 131, 122, 106, 130, 117, 108, 130, 138, 
        109, 133, 136, 112, 141, 134, 116, 148, 145, 119, 153, 152, 117, 143, 170, 
        120, 143, 177, 125, 153, 188, 126, 154, 193, 129, 158, 200, 140, 171, 214, 
        145, 177, 219, 91, 114, 107, 107, 131, 136, 118, 144, 173, 120, 145, 174, 
        125, 152, 182, 141, 172, 215, 146, 177, 221, 59, 74, 77, 74, 86, 83, 
        83, 94, 105, 81, 99, 107, 79, 104, 111, 85, 102, 112, 89, 103, 112, 
        92, 117, 123, 97, 110, 121, 105, 125, 114, 106, 125, 156, 106, 131, 129, 
        107, 137, 132, 109, 138, 134, 113, 141, 139, 117, 146, 145, 120, 150, 150, 
        117, 142, 173, 124, 160, 158, 127, 160, 176, 130, 160, 197, 141, 174, 217, 
        149, 181, 225, 62, 76, 83, 76, 86, 92, 88, 94, 101, 78, 102, 112, 
        90, 108, 114, 95, 121, 102, 94, 120, 115, 106, 121, 123, 105, 123, 130, 
        107, 125, 137, 109, 138, 136, 117, 136, 138, 119, 142, 146, 120, 146, 151, 
        119, 142, 176, 128, 161, 178, 133, 162, 201, 75, 86, 96, 112, 126, 131, 
        112, 132, 120, 110, 134, 117, 113, 136, 137, 120, 135, 138, 120, 141, 146, 
        120, 145, 152, 120, 145, 177, 126, 162, 160, 151, 184, 227, 61, 80, 78, 
        75, 92, 86, 82, 99, 92, 94, 121, 123, 102, 128, 147, 108, 131, 144, 
        120, 136, 139, 120, 146, 146, 120, 153, 150, 120, 143, 180, 120, 145, 185, 
        125, 156, 191, 134, 166, 205, 148, 179, 222, 151, 183, 228, 61, 80, 83, 
        77, 99, 91, 78, 98, 99, 104, 118, 128, 102, 128, 150, 107, 131, 147, 
        117, 135, 142, 118, 147, 149, 118, 145, 180, 125, 158, 197, 129, 161, 200, 
        152, 183, 229, 85, 112, 103, 89, 109, 107, 96, 109, 101, 96, 109, 107, 
        84, 110, 129, 92, 117, 131, 99, 129, 124, 95, 128, 111, 103, 130, 154, 
        106, 130, 149, 112, 134, 147, 122, 156, 154, 117, 145, 163, 118, 146, 171, 
        126, 159, 200, 136, 165, 204, 153, 185, 229, 64, 78, 86, 83, 106, 99, 
        85, 109, 105, 85, 112, 107, 85, 112, 120, 97, 109, 114, 101, 128, 109, 
        112, 136, 124, 108, 131, 150, 112, 134, 152, 112, 138, 157, 113, 141, 158, 
        126, 160, 198, 149, 180, 223, 80, 86, 86, 80, 92, 86, 86, 113, 129, 
        109, 133, 161, 82, 87, 90, 81, 91, 90, 80, 96, 86, 90, 113, 102, 
        105, 128, 110, 101, 131, 129, 107, 130, 156, 109, 133, 152, 115, 139, 147, 
        111, 136, 160, 110, 134, 168, 120, 145, 171, 122, 149, 176, 127, 160, 201, 
        130, 162, 204, 134, 165, 208, 37, 45, 57, 0, 0, 0, 97, 116, 132, 
        98, 118, 139, 115, 139, 149, 110, 134, 167, 124, 150, 176, 127, 157, 193, 
        65, 79, 88, 89, 99, 100, 92, 116, 137, 101, 128, 114, 110, 136, 151, 
        113, 137, 153, 110, 136, 166, 124, 150, 173, 126, 152, 175, 128, 154, 181, 
        131, 160, 194, 135, 168, 210, 64, 80, 78, 89, 100, 105, 96, 114, 102, 
        100, 116, 122, 92, 120, 131, 110, 136, 154, 119, 141, 153, 113, 138, 162, 
        110, 136, 168, 117, 145, 177, 119, 149, 179, 128, 153, 186, 132, 159, 196, 
        136, 166, 209, 88, 101, 94, 119, 142, 151, 72, 86, 79, 70, 89, 96, 
        89, 105, 100, 97, 117, 107, 98, 121, 108, 93, 120, 139, 111, 144, 134, 
        112, 144, 133, 112, 134, 165, 128, 160, 159, 132, 164, 182, 138, 169, 210, 
        67, 84, 82, 97, 120, 102, 111, 144, 136, 112, 144, 137, 139, 170, 213, 
        88, 105, 94, 94, 122, 144, 99, 118, 145, 114, 145, 140, 112, 135, 169, 
        128, 163, 160, 133, 167, 185, 39, 48, 59, 65, 84, 89, 73, 92, 99, 
        99, 121, 146, 112, 132, 133, 115, 147, 144, 112, 137, 165, 122, 148, 182, 
        124, 150, 184, 134, 163, 199, 113, 129, 133, 128, 152, 192, 134, 163, 204, 
        39, 48, 60, 69, 89, 85, 75, 95, 104, 77, 100, 108, 104, 124, 109, 
        102, 123, 152, 113, 132, 138, 114, 139, 141, 118, 152, 149, 115, 144, 162, 
        114, 139, 171, 124, 150, 180, 129, 157, 195, 144, 175, 220,   
    };
}
