package artifice;

import static artifice.ArtificeConstants.*;
import java.awt.*;

public class TremaWhiteout extends TremaSimulation {
    private static final int MAX_TREMA_SIZE = 6;
    private boolean _finished;
    private int _tremaSize;
    private int _counter;
    private int _color;
    
    public TremaWhiteout(DrawingPanel dp, SimulationTimer timer) {
        super(null, dp, timer);
        
        this.reset();
    }
    
    // Initializes and starts the Simulation
    public final void reset() {
        _tremaSize = MAX_TREMA_SIZE;
        _counter   = (MAX_TREMA_SIZE - _tremaSize);
        _counter   = (int)Math.pow(_counter, _counter);
        _finished  = false;
        
        _color = 0;
    }
    
    public void update() {
        if (!_finished) {
            this.removeTrema(_tremaSize);
            
            if (_counter-- <= 0) {
                if (--_tremaSize <= 0) {
                    _finished = true;
                    _timer.stop();
                } else {
                    _counter   = (MAX_TREMA_SIZE - _tremaSize);
                    _counter   = (int)Math.pow(_counter, _counter);
                    
                    _color = 255 - 255 * _tremaSize / MAX_TREMA_SIZE;
                }
            }
        }
        
        this.repaint();
    }
    
    protected Graphics2D getBrush() {
        Graphics2D brush = (Graphics2D) _offscreen.getGraphics();
        brush.setPaint(new Color(_color, _color, _color, _opacity));
        brush.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        return brush;
    }
    
    protected void removeTrema(float scale) {
        Graphics2D brush = this.getBrush();
        
        scale += random();
        scale *= scale;
        int w = (int)(scale * 54);
        int h = (int)(scale * 0.7f);
        
        int x = random(0, SIMULATION_WIDTH);
        int y = random(0, SIMULATION_HEIGHT);
        
        float theta = TWO_PI * random();
        brush.rotate(theta, x, y);
        
        brush.fillRect(x - (w >> 1), y - (h >> 1), w, h);
    }
}
