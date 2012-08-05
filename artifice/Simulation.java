package artifice;

import static artifice.ArtificeConstants.*;
import static fisch.Utilities.random;

import javax.swing.event.MouseInputListener;

import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.*;

public abstract class Simulation implements Runnable, MouseInputListener {
    protected BufferedImage _offscreen;
    protected SimulationTimer _timer;
    protected DrawingPanel _dp;
    protected boolean _paused;
    private int _bufferType;
    private int _timerDelay;
    
    protected int _mouseDownX, _mouseDownY;
    protected int _mouseDraggedX, _mouseDraggedY;
    protected boolean _dragged, _shiftDragged, _initialRun;
    
    public Simulation(DrawingPanel dp, SimulationTimer timer) {
        this(dp, timer, BufferedImage.TYPE_INT_RGB, 0);
    }
    
    public Simulation(DrawingPanel dp, SimulationTimer timer, int bufferType) {
        this(dp, timer, bufferType, 0);
    }
    
    public Simulation(DrawingPanel dp, SimulationTimer timer, int bufferType, int timerDelay) {
        super();
        
        _dp     = dp;
        _timer  = timer;
        _paused = false;
        _bufferType = bufferType;
        _timerDelay = timerDelay;
        
        _initialRun = true;
    }
    
    /* Start rendering the simulation in the DrawingPanel
     * Initiates the simulation
     * 
     * @see java.lang.Runnable#run()
     */
    public void run() {
        _timer.stop();
        
        /* Store whether or not it's this Simulation's first run, so as to 
         * only load the simulation if/when it's requested by the user
         * (to mitigate a noticeable lag I was experiencing upon startup due to 
         * all of the Simulations being initialized consecutively since several 
         * precompute and/or load a lot at startup)
         */
        if (_initialRun) {
            this.clearDBuffer();
            this.initialize();
            
            _initialRun = false;
        }
        
        _dp.setSimulation(this);
        _timer.setSimulation(this);
        _timer.setDelay(_timerDelay);
        
        _timer.start();
    }

    // Called on clock ticks -- main graphical/algorithmic updates 
    // should take place here
    public abstract void update();
    
    public void reset() { }
    
    public void restart() {
        _timer.stop();
        _paused = false;
        
        this.clearDBuffer();
        this.reset();
        
        _timer.start();
    }
    
    public void initialize() {
        this.reset();
    }
    
    public void setPaused(boolean paused) {
        _paused = paused;
        
        if (_paused)
            _timer.stop();
        else _timer.start();
    }
    
    public boolean isPaused() {
        return _paused;
    }
    
    public void mouseReleased(MouseEvent e) { }
    public void mouseEntered(MouseEvent arg0) { }
    public void mouseExited(MouseEvent arg0) { }
    public void mouseMoved(MouseEvent e) { }
    
    public void mouseClicked(MouseEvent e) {
        this.restart();
    }
    
    public void mousePressed(MouseEvent e) {
        _mouseDownX = e.getX();
        _mouseDownY = e.getY();
        _dragged    = false;
    }
    
    public void mouseDragged(MouseEvent e) {
        _mouseDraggedX = e.getX();
        _mouseDraggedY = e.getY();
        _dragged = true;
        
        _shiftDragged = e.isShiftDown();
    }
    
    public int getWidth() {
        return _dp.getWidth();
    }
    
    public int getHeight() {
        return _dp.getHeight();
    }
    
    public void setDelay(int delay) {
        _timerDelay = delay;
        
        _timer.setDelay(delay);
    }
    
    public int getDelay() {
        return _timerDelay;
    }
    
    protected boolean isValid(int x, int y) {
        return (x >= 0 && x < SIMULATION_WIDTH && 
                y >= 0 && y < SIMULATION_HEIGHT);
    }

    public void paint(Graphics2D brush) { }
    
    public void paintSimulation(Graphics2D brush) {
        // Copy double buffer to the Panel's actual canvas
        if (_offscreen != null)
//            brush.drawRenderedImage(_offscreen, null);
            brush.drawImage(_offscreen, 0, 0, _dp);
        
        this.paint(brush);
    }
    
    public void repaint() {
        _dp.repaint();
    }
    
    public final void clearDBuffer() {
        _offscreen = new BufferedImage(SIMULATION_WIDTH, SIMULATION_HEIGHT, _bufferType);
//        BufferedImage.TYPE_INT_RGB
//        Graphics2D offBrush = (Graphics2D) _offscreen.getGraphics();
//        super.paintComponent(offBrush);
//        
//        offBrush.dispose();
    }
    
    public BufferedImage getDBuffer() {
        return _offscreen;
    }
    
    public Color randomColor(int[] palette, int alpha) {
        // Select a random color from within a predefined color palette
        int offset = 3 * random(0, (palette.length - 1) / 3);
        return new Color(palette[offset],  
                palette[offset + 1], 
                palette[offset + 2], alpha);
    }
    
    // setPixel is state-based
    protected static int _curRed, _curGreen, _curBlue, _curK;
    
    // Blends a src ARGB pixel onto a destination RGB pixel
    protected void setPixel(int x, int y) {
        int oldColor = _offscreen.getRGB(x, y);
        
        // Blend the two colors together, weighting their relative contributions
        // according to the new color's alpha value
//        int k = (255 - alpha);
        int r = ((_curRed   + _curK * ((oldColor & 0x00FF0000) >> 16)) << 8) & 0x00FF0000;
        int g =  (_curGreen + _curK * ((oldColor & 0x0000FF00) >>  8)) & 0x0000FF00;
        int b =  (_curBlue  + _curK * ((oldColor & 0x000000FF)      )) >> 8;
        
        _offscreen.setRGB(x, y, (r | g | b));
    }
    
    // Cohen-Sutherland Outcodes
    protected static final byte OUT_ABOVE = 0x8;
    protected static final byte OUT_BELOW = 0x4;
    protected static final byte OUT_LEFT  = 0x2;
    protected static final byte OUT_RIGHT = 0x1;
    
    // Utility method used by the Cohen-Sutherland Algorithm (drawClippedLine)
    public static final byte clip(int x, int y) {
        byte 
        code  = ((y < 0) ? OUT_ABOVE : ((y >= SIMULATION_HEIGHT) ? OUT_BELOW : 0)); 
        code |= ((x < 0) ? OUT_LEFT  : ((x >= SIMULATION_WIDTH ) ? OUT_RIGHT : 0));
        
//        if (y < 0)
//            code |= OUT_ABOVE;
//        else if (y >= SIMULATION_HEIGHT)
//            code |= OUT_BELOW;
//        
//        if (x < 0)
//            code |= OUT_LEFT;
//        else if (x >= SIMULATION_WIDTH)
//            code |= OUT_RIGHT;
        
        return code;
    }
    
    /*  An implementation of the Cohen-Sutherland line clipping algorithm.
     *  Loops over the line until it can be either trivially rejected or trivially
     *  accepted. If it is neither rejected nor accepted, subdivide it into two
     *  segments, one of which can be rejected.
     */
    protected final void drawClippedLine(int x1, int y1, int x2, int y2, int color) {
        byte code0 = clip(x1, y1);
        byte code1 = clip(x2, y2);
        
        while((code0 | code1) != 0) {  // Trivially accept
            if ((code0 & code1) != 0)  // Trivially reject
                return;
            
            // Pick one endpoint to clip on this pass
            byte outcode = (code0 != 0 ? code0 : code1);
            int x = x1, y = y1;
            
            if ((outcode & OUT_ABOVE) != 0) {
                if (y2 != y1)
                    x += (x2 - x1) * (0 - y1) / (y2 - y1);
                
                y = 0;
            }
            else if ((outcode & OUT_BELOW) != 0) {
                if (y2 != y1)
                    x += (x2 - x1) * ((SIMULATION_HEIGHT - 1) - y1) / (y2 - y1);
                
                y = SIMULATION_HEIGHT - 1;
            }
            else if ((outcode & OUT_LEFT) != 0) {
                if (x2 != x1)
                    y += (y2 - y1) * (0 - x1) / (x2 - x1);
                
                x = 0;
            }
            else {  /* outcode & RIGHT */
                if (x2 != x1)
                    y += (y2 - y1) * ((SIMULATION_WIDTH - 1) - x1) / (x2 - x1);
                
                x = (SIMULATION_WIDTH - 1);
            }
            
            if (outcode == code0) {
                x1 = x;
                y1 = y;
                code0 = clip(x1, y1);
            } else {
                x2 = x;
                y2 = y;
                code1 = clip(x2, y2);
            }
        }
        
        drawLine(x1, y1, x2, y2, color);
    }
    
    // Extremely Fast Line Algorithm Var E (Addition Fixed Point PreCalc ModeX)
    // Copyright 2001-2, By Po-Han Lin
    // Visit http://www.edepot.com for more info
    // Optimizations by Fisch
    protected final void drawLine(int x, int y, int x2, int y2, int color) {
        int yLen = y2 - y;
        int xLen = x2 - x;
        
        int alpha = (color >>> 24);
        _curRed   = alpha * ((color & 0x00FF0000) >> 16);
        _curGreen = alpha * ((color & 0x0000FF00) >>  8);
        _curBlue  = alpha *  (color & 0x000000FF);
        _curK     = 255 - alpha;
        
        if (Math.abs(yLen) > Math.abs(xLen)) { // y-Axis Dominant
            int decInc = (yLen == 0) ? 0 : (xLen << 16) / yLen;
            int j = 0x8000 + (x << 16);
            
            if (yLen > 0) {
                yLen += y;
                
                for(; y <= yLen; ++y) {
                    setPixel(j >> 16, y);
                    j += decInc;
                }
                return;
            }
            
            yLen += y;
            for(; y >= yLen; --y) {
                setPixel(j >> 16, y);
                j -= decInc;
            }
            
            return;
        }
        
        // x-Axis Dominant
        int decInc = (xLen == 0) ? 0 : (yLen << 16) / xLen;
        int j = 0x8000 + (y << 16);
        
        if (xLen > 0) {
            xLen += x;
            
            for(; x <= xLen; ++x) {
                setPixel(x, j >> 16);
                j += decInc;
            }
            return;
        }
        
        xLen += x;
        for(; x >= xLen; --x) {
            setPixel(x, j >> 16);
            j -= decInc;
        }
    }
}
