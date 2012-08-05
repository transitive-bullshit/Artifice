package artifice;
import static artifice.ArtificeConstants.*;

import java.awt.image.*;
import java.awt.*;

/**
 * @author Travis Fischer
 * 
 * Concept and code base by J. Tarbell
 * <a href="http://www.complexification.net/gallery/machines/tremaSpike/">Complexification</a>
 */
public abstract class TremaSimulation extends Simulation {
    protected gfx.Shape _curShape;
    protected double _dimLog;
    protected int _opacity;
    
    public TremaSimulation(gfx.Shape shape, DrawingPanel dp, SimulationTimer timer) {
        super(dp, timer, BufferedImage.TYPE_INT_RGB);
        
        _opacity  = 255; // opaque by default
        _dimLog = Math.log(SIMULATION_WIDTH);
        
        this.setShape(shape);
    }
    
    public void setOpacity(int opacity) {
        _opacity = opacity;
        
        if (_curShape != null)
            _curShape.setColor(new Color(255, 255, 255, _opacity));
    }
    
    public void update() {
        this.removeTrema();
        this.repaint();
    }
    
    public final void setShape(gfx.Shape s) {
        _curShape = s;
        
        if (_curShape != null) {
            _curShape.setAntialiasing(true);
            _curShape.setColor(new Color(255, 255, 255, _opacity));
        }
    }
    
    protected void removeTrema() {
        Graphics2D brush = (Graphics2D) _offscreen.getGraphics();
        
        double scale = 1 + 5 * (_dimLog - Math.log(1 + SIMULATION_WIDTH * random()));
        int x = random(0, SIMULATION_WIDTH);
        int y = random(0, SIMULATION_HEIGHT);
        
        double width = scale, height = scale;
        _curShape.setLocation(x - width / 2, y - height / 2);
        _curShape.setSize(width, height);
        
        _curShape.paint((Graphics2D)_offscreen.getGraphics());
    }
}
