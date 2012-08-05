package artifice;
import static artifice.ArtificeConstants.*;

import javax.swing.event.*;
import javax.swing.*;

import java.awt.event.*;
import java.awt.*;

public class IntersectionControl extends ControlPanel implements ActionListener, ChangeListener {
    private static final String VARIATION_ONE   = "Default";
    private static final String VARIATION_TWO   = "Second";
    private static final String VARIATION_THREE = "Underlying";
    private static final String VARIATION_FOUR = "Black/White";
    private static final String[] VARIATIONS = {
        VARIATION_ONE, VARIATION_TWO, VARIATION_THREE, VARIATION_FOUR, 
    };
    private static final String MOVEMENT_DEFEAULT   = "Default";
    private static final String MOVEMENT_HORIZONTAL = "Horizontal";
    private static final String MOVEMENT_VERTICAL   = "Vertical";
    private static final String[] MOVEMENTS = {
        MOVEMENT_DEFEAULT, MOVEMENT_HORIZONTAL, MOVEMENT_VERTICAL,  
    };
    private JComboBox _variations, _movements;
    private IntersectionSimulation _simulation;
    private JCheckBox _random;
    private JSlider _radius;
    
    public IntersectionControl(IntersectionSimulation simulation) {
        super(new GridLayout(0, 1), simulation);
        
        _simulation = simulation;
        
        JPanel p = new JPanel(), p2 = new JPanel(new GridLayout(0, 1));
        p.add(new JLabel("Circle Radius:"));
        
        p2.add(_random = new JCheckBox("Random", true));
        _random.addActionListener(this);
        p2.add(_radius = new JSlider(MIN_CIRCLE_RADIUS, MAX_CIRCLE_RADIUS, 
                DEFAULT_CIRCLE_RADIUS));
        _radius.setEnabled(false);
        _radius.addChangeListener(this);
        
        p.add(p2);
        this.add(p);
        
        p = new JPanel();
        
        // Various variation options of IntersectionSimulation
        JPanel variationsPanel = new JPanel();
        
        _variations = new JComboBox(VARIATIONS);
        _variations.setSelectedItem(VARIATION_ONE);
        _variations.addActionListener(this);
        
        variationsPanel.add(new JLabel("Drawing Variation:"));
        variationsPanel.add(_variations);
        
        p.add(variationsPanel);
        
        p2 = new JPanel();
        
        _movements = new JComboBox(MOVEMENTS);
        _movements.setSelectedItem(MOVEMENT_DEFEAULT);
        _movements.addActionListener(this);
        
        p2.add(new JLabel("Circle Movement:"));
        p2.add(_movements);
        
        p.add(p2);
        this.add(p);
    }
    
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        if (source == _random) {
            boolean selected = _random.isSelected();
            
            _radius.setEnabled(!selected);
            
            if (selected)
                _simulation.setRandomRadius();
            else _simulation.setRadius(_radius.getValue());
        } else if (source == _variations) {
            _simulation.setVariation(_variations.getSelectedIndex());
        } else if (source == _movements) {
            _simulation.setMovement(_movements.getSelectedIndex());
        }
    }
    
    public void stateChanged(ChangeEvent e) {
        Object source = e.getSource();
        
        if (source == _radius)
            _simulation.setRadius(_radius.getValue());
    }
}
