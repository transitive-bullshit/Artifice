package artifice;

import static artifice.ArtificeConstants.*;
import java.awt.Graphics2D;

public class TremaSpike extends TremaEllipse {
    public TremaSpike(DrawingPanel dp, SimulationTimer timer) {
        super(dp, timer);
    }
    
    public void removeTrema() {
        Graphics2D brush = (Graphics2D) _offscreen.getGraphics();
        
        double scale = 1 + 5 * (_dimLog - Math.log(1 + SIMULATION_WIDTH * random()));
        int x = random(0, SIMULATION_WIDTH - 1);
        int y = random(0, SIMULATION_HEIGHT - 1);
        
        double width = scale * 8, height = scale / 4;
        _curShape.setLocation(x - width / 2, y - height / 2);
        _curShape.setSize(width, height);
        _curShape.setRotation(random(0, 359));
        
        _curShape.paint((Graphics2D)_offscreen.getGraphics());
    }
}
