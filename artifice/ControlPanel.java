package artifice;

import java.awt.*;
import javax.swing.*;

/**
 * @author Travis Fischer
 *
 */
public class ControlPanel extends JPanel {
    private Simulation _simulation;
    
    public ControlPanel(Simulation sim) {
        this(new GridLayout(0, 1), sim);
    }
    
    public ControlPanel(LayoutManager layout, Simulation sim) {
        super(layout);
        
        _simulation = sim;
    }
    
    public Simulation getSimulation() {
        return _simulation;
    }
    
    public void setSimulation(Simulation sim) {
        _simulation = sim;
    }
//    
//    // Initiates the simulation
//    public void run() {
//        _simulation.run();
//    }
}
