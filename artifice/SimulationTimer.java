package artifice;
import javax.swing.Timer;
import java.awt.event.*;

public class SimulationTimer extends Timer implements ActionListener {
    private Simulation _simulation;
    
    public SimulationTimer() {
        this(null);
    }
    
    public SimulationTimer(Simulation simulation) {
        super(0, null);
        
        _simulation = simulation;
        this.addActionListener(this);
    }
    
    public void setSimulation(Simulation newSimulation) {
        _simulation = newSimulation;
    }
    
    public void actionPerformed(ActionEvent e) {
        if (_simulation != null)
            _simulation.update();
    }
}
