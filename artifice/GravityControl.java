package artifice;
import static artifice.ArtificeConstants.*;

import javax.swing.event.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class GravityControl extends ControlPanel implements ActionListener, ChangeListener {
    private static final String VARIATION_DEFEAULT  = "Clean";
    private static final String VARIATION_AGGREGATE = "Aggregate";
    private static final String[] VARIATIONS = {
        VARIATION_DEFEAULT, VARIATION_AGGREGATE, 
    };
    private static final String STATE_DEFEAULT   = "Circular";
    private static final String STATE_RANDOM     = "Random";
    private static final String[] INITIAL_STATES = {
        STATE_DEFEAULT, STATE_RANDOM, 
    };
    
    private JComboBox _variations, _initialStates;
    private GravitySimulation _simulation;
    private JSlider _speed, _noParticles;
    
    public GravityControl(GravitySimulation simulation) {
        super(new GridLayout(0, 1), simulation);
        
        _simulation = simulation;
        
        // Various variation options of GravitySimulation
        JPanel variationsPanel = new JPanel();
        JPanel p = new JPanel(), p3 = new JPanel(new GridLayout(1, 0));
        
        _variations = new JComboBox(VARIATIONS);
        _variations.setSelectedItem(VARIATION_DEFEAULT);
        _variations.addActionListener(this);
        
        variationsPanel.add(new JLabel("Drawing Variation:"));
        variationsPanel.add(_variations);
        
        _initialStates = new JComboBox(INITIAL_STATES);
        _initialStates.setSelectedItem(STATE_DEFEAULT);
        _initialStates.addActionListener(this);
        p.add(new JLabel("Initial State:"));
        p.add(_initialStates);
        
        p3.add(variationsPanel);
        p3.add(p);
        
        JPanel p0 = new JPanel(new GridLayout(1, 0));
        JPanel p1 = new JPanel(), p2 = new JPanel();
        p1.add(new JLabel("Particle Speed:"));
        p1.add(_speed = new JSlider(1, MAX_GRAVITY_SPEED - 1, DEFAULT_GRAVITY_SPEED));
        _speed.addChangeListener(this);
        
        p2.add(new JLabel("No Particles:"));
        p2.add(_noParticles = new JSlider(2, MAX_GRAVITY_NO_PARTICLES, DEFAULT_GRAVITY_NO_PARTICLES));
        _noParticles.addChangeListener(this);
        
        p0.add(p1);
        p0.add(p2);
        
        this.add(p3);
        this.add(p0);
    }
    
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        String str = "";
        
        if (source == _variations)
            str = (String) _variations.getSelectedItem();
        else if (source == _initialStates)
            str = (String) _initialStates.getSelectedItem();
        
        if (str == VARIATION_DEFEAULT)
            _simulation.setVariation(0);
        else if (str == VARIATION_AGGREGATE)
            _simulation.setVariation(1);
        else if (str == STATE_DEFEAULT)
            _simulation.setInitialState(0);
        else if (str == STATE_RANDOM)
            _simulation.setInitialState(1);
    }
    
    public void stateChanged(ChangeEvent e) {
        Object source = e.getSource();
        
        if (source == _speed)
            _simulation.setSpeed(_speed.getValue());
        else if (source == _noParticles)
            _simulation.setNoParticles(_noParticles.getValue());
    }
}
