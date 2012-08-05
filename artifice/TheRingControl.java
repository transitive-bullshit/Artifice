package artifice;
import static artifice.ArtificeConstants.*;

import java.awt.event.*;

import javax.swing.event.*;
import javax.swing.*;

import java.awt.*;

public class TheRingControl extends ControlPanel implements ChangeListener {
    private TheRingSimulation _simulation;
    private JSlider _originRadius, _noParticles;
    
    public TheRingControl(TheRingSimulation simulation) {
        super(new GridLayout(0, 1), simulation);
        
        _simulation = simulation;
        
        JPanel originRadius = new JPanel();
        
        // Radius of Circlular Ring around which, Particles are spawned
        originRadius.add(new JLabel("Ring Radius:"));
        originRadius.add(_originRadius = new JSlider(5, 100, 30));
        _originRadius.addChangeListener(this);
        
        JPanel noParticles = new JPanel();
        
        // Number of particles in The Ring
        noParticles.add(new JLabel("No Particles:"));
        noParticles.add(_noParticles = new JSlider(100, 8000, 5000));
        _noParticles.addChangeListener(this);
        
        this.add(originRadius);
        this.add(noParticles);
        this.add(new JLabel("Click anywhere above to toggle blackout periods."));
    }
    
    public void stateChanged(ChangeEvent e) {
        Object source = e.getSource();
        
        if (source == _originRadius)
            _simulation.setOriginRadius(_originRadius.getValue());
        else if (source == _noParticles)
            _simulation.setNoParticles(_noParticles.getValue());
    }
}
