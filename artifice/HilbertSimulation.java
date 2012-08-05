package artifice;
/* HilbertSimulation.java
 * 
 * Hilbert recursive space-filling L-System
 * 
 * @author Travis Fischer (tfischer)
 * @version Dec 30, 2006
 */
import static artifice.ArtificeConstants.*;

import java.awt.image.*;
import java.awt.*;

public class HilbertSimulation extends Simulation {
    private HilbertSystem _hilbertSystem;
    private boolean _done;
    private int _counter, _depth;
    
    public HilbertSimulation(DrawingPanel dp, SimulationTimer timer) {
        super(dp, timer, BufferedImage.TYPE_INT_RGB);
        
        _hilbertSystem = new HilbertSystem();
    }
    
    public final void reset() {
        _done = true;
        _counter = 0;
        _depth = 1;
        
//        extractColorsFromImage(_dp, "Images/palette1.png");
        
        // Clear the offscreen buffer to white
        Graphics2D brush = (Graphics2D)_offscreen.getGraphics();
        brush.setColor(Color.WHITE);
        brush.fillRect(0, 0, SIMULATION_WIDTH, SIMULATION_HEIGHT);
    }
    
    public void update() {
        if (_done) {
            if (_depth < 8)
                _hilbertSystem.generate(_offscreen, _depth++);
            _counter = 0;
            _done = false;
        } else {
            _done = !_hilbertSystem.update(_counter++);
        }
        
        
        this.repaint();
    }
    
    public void setVariation(int variation) {
        _hilbertSystem.setVariation(variation);
    }
    
    public void setColor(int color) {
        _hilbertSystem.setColor(color);
    }
    
//    public void paintSimulation(Graphics brush) {
//        _hilbertSystem.drawDirty(brush);
//    }
}
