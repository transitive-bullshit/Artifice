package artifice;
import static artifice.ArtificeConstants.*;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import primordial.Variation;
import java.awt.event.*;
import javax.swing.*;

import java.awt.*;

public class PrimordialControl extends ControlPanel implements ActionListener, ChangeListener {
    private static final String VARIATION_DEFEAULT  = "Petri Dish";
    private static final String VARIATION_SNAKESKIN = "Snakeskin";
    private static final String[] VARIATIONS = {
        VARIATION_DEFEAULT, VARIATION_SNAKESKIN, 
    };
    private static final String SINGLE_CELLED = "Single Celled";
    private static final String FLAGELLA      = "Flagella";
    private static final String OCTOPI        = "Octopi";
    
    private JComboBox _variations;
    private PrimordialSimulation _simulation;
    private JCheckBox _singleCelled;
    private JCheckBox _flagella;
    private JCheckBox _octopi;
    
    public PrimordialControl(PrimordialSimulation simulation) {
        super(new GridLayout(0, 1), simulation);
        
        _simulation = simulation;
        
        // Various variation options of PrimordialSimulation
        JPanel variationsPanel = new JPanel();
        
        _variations = new JComboBox(VARIATIONS);
        _variations.setSelectedItem(VARIATION_DEFEAULT);
        _variations.addActionListener(this);
        
        variationsPanel.add(new JLabel("Drawing Variation:"));
        variationsPanel.add(_variations);
        
        JPanel types = new JPanel(new GridLayout(0, 3));
        
        JPanel p0 = new JPanel();
        p0.add(_singleCelled = new JCheckBox(SINGLE_CELLED, false));
        _singleCelled.addChangeListener(this);
        types.add(p0);
        
        JPanel p1 = new JPanel();
        p1.add(_flagella = new JCheckBox(FLAGELLA, true));
        _flagella.addChangeListener(this);
        types.add(p1);
        
        JPanel p2 = new JPanel();
        p2.add(_octopi = new JCheckBox(OCTOPI, false));
        _octopi.addChangeListener(this);
        types.add(p2);
        
        this.add(variationsPanel);
        this.add(types);
    }
    
    public void actionPerformed(ActionEvent e) {
        String str = (String) _variations.getSelectedItem();
        
        if (str == VARIATION_DEFEAULT)
            _simulation.setVariation(Variation.PRIMORDIAL_DEFAULT);
        else if (str == VARIATION_SNAKESKIN)
            _simulation.setVariation(Variation.PRIMORDIAL_SNAKESKIN);
    }
    
    public void stateChanged(ChangeEvent e) {
        Object source = e.getSource();
        
        if (source == _singleCelled)
            _simulation.setSingleCelled(_singleCelled.isSelected());
        else if (source == _flagella)
            _simulation.setFlagella(_flagella.isSelected());
        else if (source == _octopi)
            _simulation.setOctopi(_octopi.isSelected());
    }
}
