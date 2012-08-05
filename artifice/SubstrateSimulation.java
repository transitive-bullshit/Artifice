package artifice;
import static artifice.ArtificeConstants.*;

import java.util.*;
import java.awt.geom.Line2D;
import java.awt.image.*;
import java.awt.event.*;
import java.awt.*;

/**
 * @author Travis Fischer
 *
 * Concept and code base by J. Tarbell
 * @see http://www.complexification.net/
 */
public class SubstrateSimulation extends Simulation {
    private static final int MAX_ACTIVE_CRACKS = 100;
    private int _noInitialLines;
    private LinkedList<Crack> _activeCracks;
    private Vector<Crack> _inactiveCracks;
    private int _activeLength, _inactiveLength;
    private int _spawnType, _growth; // Options from ControlPanel
    
    public SubstrateSimulation(DrawingPanel dp, SimulationTimer timer) {
        super(dp, timer, BufferedImage.TYPE_INT_RGB);
        
        _noInitialLines = 4;
        _activeCracks   = new LinkedList<Crack>();
        _inactiveCracks = new Vector<Crack>();
        _spawnType      = SPAWN_LINEAR;
        _growth         = DEFAULT_GROWTH;
        
//        this.reset();
    }
    
    public void update() {
        ListIterator<Crack> iter = _activeCracks.listIterator();
        while(iter.hasNext()) {
            Crack cur = iter.next();
            int oldLength = cur.getLength();
            
            // Update the position of the current Crack and cleanup if it becomes inactive
            if (!cur.update()) {
                _activeLength -= cur.getLength();
                iter.remove();
                
                _inactiveLength += cur.getLength();
                _inactiveCracks.add(cur);
                
                boolean exp = true;
                for(int i = 0; i < _growth; i++) {
                    if (!(exp = randomBoolean())) 
                        break;
                }
                int stop = (exp ? 2 : 1);//random(1, random(1, random(1, 2)));
                
                for(int i = 0; i < stop; i++) {
                    Crack newCrack = this.spawnNewCrack();
                    if (newCrack != null) {
                        _activeLength += newCrack.getLength();
                        iter.add(newCrack);
                    }
                }
            } else _activeLength += cur.getLength() - oldLength;
        }
        
        //this.paintImmediately(0, 0, SUBSTRATE_WIDTH, SUBSTRATE_HEIGHT);
        this.repaint();
    }
    
    public Crack spawnNewCrack() {
//        int x, y, timeout = 0;
//        
//        while(timeout++ < 4000) {
//            x = random(0, SUBSTRATE_WIDTH - 1);
//            y = random(0, SUBSTRATE_HEIGHT - 1);
//            
//            if (_setCracks[y][x] < 360) {
//                return new Crack(this, x, y, 
//                   _setCracks[y][x] + (randomBoolean() ? 90 : 270));
//            }
//        }
//        
//        return null;
        
        if (_activeCracks.size() > MAX_ACTIVE_CRACKS)
            return null;
        
        long randomCrack  = random(0, _activeLength + _inactiveLength - 1);
        int length = 0;
        
        if (randomCrack < _activeLength) {
            ListIterator<Crack> iter = _activeCracks.listIterator();
            
            while(iter.hasNext()) {
                Crack cur = iter.next();
                
                length += cur.getLength();
                if (length > randomCrack)
                    return cur.spawnNewCrack(_spawnType);
            }
            
            return null;
        }
        
        randomCrack -= _activeLength;
        ListIterator<Crack> iter = _inactiveCracks.listIterator();
        
        while(iter.hasNext()) {
            Crack cur = iter.next();
            
            length += cur.getLength();
            if (length > randomCrack)
                return cur.spawnNewCrack(_spawnType);
        }
        
        return null;
    }
    
    // Initializes and starts the Simulation
    public void reset() {
        _timer.stop();
        
        Crack.setCanvas(_offscreen);
        _activeCracks.clear();
        _inactiveCracks.clear();
        
        _dragged        = false;
        _paused         = false;
        _activeLength   = 0;
        _inactiveLength = 0;

        // Clear the screen to white
        Graphics2D offBrush = (Graphics2D) _offscreen.getGraphics();
        offBrush.setColor(Color.WHITE);
        offBrush.fillRect(0, 0, SIMULATION_WIDTH, SIMULATION_HEIGHT);
        
        for(int i = 0; i < SIMULATION_HEIGHT; i++)
            for(int j = 0; j < SIMULATION_WIDTH; j++)
                Crack._setCracks[i][j] = 1000;
        
        // Create and initialize default cracks
        for(int i = 0; i < _noInitialLines; i++) {
            Crack newCrack = new Crack(this);
            
            _activeCracks.add(newCrack);
        }
    }
    
    /**
     * @return Returns the _noInitialLines.
     */
    public int getNoInitialLines() {
        return _noInitialLines;
    }

    /**
     * @param initialLines The _noInitialLines to set.
     */
    public void setNoInitialLines(int initialLines) {
        _noInitialLines = initialLines;
    }
    
    public void paint(Graphics2D brush) {
        // If user is dragging mouse, show a preview-line
        if (_dragged) {
            double newX  = _mouseDraggedX;
            double newY  = _mouseDraggedY;
            
            // If shift is pressed, align preview-line to 90 degree increments
            if (_shiftDragged) {
                int dx    = _mouseDraggedX - _mouseDownX;
                int dy    = _mouseDraggedY - _mouseDownY;
                
                double theta = this.getTheta(dx, dy);
                double dist  = Math.sqrt(dx * dx + dy * dy);
                
                newX  = _mouseDownX + dist * Math.cos(theta);
                newY  = _mouseDownY + dist * Math.sin(theta);
            }
            
            // Draw a variable-length line in direction new Crack would go in
            brush.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
//            brush.setStroke(new BasicStroke(1));
            brush.setPaint(new Color(0, 0, 0, 127));
            brush.drawLine((int)_mouseDownX, (int)_mouseDownY, (int)newX, (int)newY);
        }
    }
    
    private double getTheta(int dx, int dy) {
        double theta = Math.atan2(dy, dx);
        
        if (_shiftDragged) {
            boolean top = (theta < 0);
            if (top) theta = -theta;
            
            if (theta < Math.PI / 4)
                theta = 0;
            else if (theta < 3 * Math.PI / 4 && !top)
                theta = Math.PI / 2;
            else if (theta < 5 * Math.PI / 4 && (!top || theta > 3 * Math.PI / 4))
                theta = Math.PI;
            else if (theta < 7 * Math.PI / 4)
                theta = 3 * Math.PI / 2;
        }
        
        return theta;
    }
    
    // Allow user to add Cracks at his/her discretion via mouse input
    public void mouseReleased(MouseEvent e) {
        if (_dragged) {
            _dragged = false;
            
            int x = e.getX(), y = e.getY();
            int dx = x - _mouseDownX;
            int dy = y - _mouseDownY;
            
            int theta = (int)(180 * this.getTheta(dx, dy) / Math.PI);
            this.addInputCrack(theta);
            
            if (_paused)
                this.repaint();
        }
    }
    
    public void mouseClicked(MouseEvent e) {
        this.addInputCrack(random(0, 359));
    }
    
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        
        if (_paused)
            this.repaint();
    }
    
    public void addInputCrack(int theta) {
        //System.out.println(theta);
        _activeCracks.add(new Crack(_mouseDownX, _mouseDownY, theta));
    }

    public int getSpawnType() {
        return _spawnType;
    }
    
    public void setSpawnType(int type) {
        _spawnType = type;
    }
    
    public void setGrowth(int growth) {
        _growth = growth;
    }
}
