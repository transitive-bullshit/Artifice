package artifice;
import static artifice.ArtificeConstants.*;
import javax.swing.event.MouseInputListener;

import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import java.awt.*;

/**
 * @author Travis Fischer
 *
 */
public class DrawingPanel extends JPanel {
    private Simulation _currentSimulation;
    
    public DrawingPanel() {
        super(false);
        
        _currentSimulation = null;
        
        Dimension d = new Dimension(SIMULATION_WIDTH, SIMULATION_HEIGHT);
        this.setSize(d);
        this.setPreferredSize(d);
        this.setOpaque(false);
    }
    
    public void setSimulation(Simulation newSim) {
        // Changing Simulation, so remove old Simulation's hooks 
        if (_currentSimulation != null) {
            this.removeMouseListener(_currentSimulation);
            this.removeMouseMotionListener(_currentSimulation);
        }
        
        _currentSimulation = newSim;
        this.addMouseListener(newSim);
        this.addMouseMotionListener(newSim);
    }
    
    public void paintComponent(Graphics g) {
        _currentSimulation.paintSimulation((Graphics2D) g);
    }
}
