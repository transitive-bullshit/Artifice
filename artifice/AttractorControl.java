package artifice;
import static artifice.ArtificeConstants.*;

import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

public class AttractorControl extends ControlPanel implements ActionListener {
    private static final String[] VARIATIONS = {
        "White on Black", "Black on White", 
    };
    private static final String[] ATTRACTOR_TYPES = {
        "DeJong", "Ring", "Clifford", "Lissajous", 
        "Opposing Ikeda", "Julia", "Henon Phase", //"Lorenz",  
    };
    private JComboBox _variations, _attractorTypes;
    private AttractorSimulation _simulation;
    
    public AttractorControl(AttractorSimulation simulation) {
        super(new GridLayout(0, 1), simulation);
        
        _simulation = simulation;
        
        // Various types of mathematical Attractors available
        JPanel attractorTypesPanel = new JPanel();
        
        _attractorTypes = new JComboBox(ATTRACTOR_TYPES);
        _attractorTypes.setSelectedIndex(0);
        _attractorTypes.addActionListener(this);
        
        attractorTypesPanel.add(new JLabel("Attractor Type"));
        attractorTypesPanel.add(_attractorTypes);
        
        // Various variation options of AttractorSimulation
        JPanel variationsPanel = new JPanel();
        
        _variations = new JComboBox(VARIATIONS);
        _variations.setSelectedIndex(0);
        _variations.addActionListener(this);
        
        variationsPanel.add(new JLabel("Drawing Variation:"));
        variationsPanel.add(_variations);
        
        this.add(attractorTypesPanel);
        this.add(variationsPanel);
    }
    
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        if (source == _variations)
            _simulation.setVariation(_variations.getSelectedIndex());
        else if (source == _attractorTypes)
            _simulation.setAttractorType(_attractorTypes.getSelectedIndex());
    }
}
