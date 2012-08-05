package artifice;
/* StrokeSimulation.java
 * 
 * 
 * 
 * @author Travis Fischer (tfischer)
 * @date January 13, 2006
 */
import static artifice.ArtificeConstants.*;
import java.awt.*;

public class StrokeSimulation extends Simulation {
    
    public StrokeSimulation(DrawingPanel dp, SimulationTimer timer) {
        super(dp, timer);
        
    }
    
    public final void reset() {
        
        
        // Clear the offscreen buffer to black
        Graphics2D brush = (Graphics2D)_offscreen.getGraphics();
        brush.setColor(Color.BLACK);
        brush.fillRect(0, 0, SIMULATION_WIDTH, SIMULATION_HEIGHT);
    }
    
    public void update() {
        
        
        this.repaint();
    }
}