package artifice;
import static artifice.ArtificeConstants.*;

import javax.swing.event.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class HilbertControl extends ControlPanel implements ActionListener {
    private static final String VARIATION_DEFEAULT  = "Circle";
    private static final String VARIATION_RECTANGLE = "Rectangle";
    private static final String[] VARIATIONS = {
        VARIATION_DEFEAULT, VARIATION_RECTANGLE, 
    };

    private static final String COLOR_DEFEAULT  = "Colored";
    private static final String COLOR_NONE = "Black/White";
    private static final String[] COLORS = {
        COLOR_DEFEAULT, COLOR_NONE, 
    };
    
    private JComboBox _variations, _colors;
    private HilbertSimulation _simulation;
    
    public HilbertControl(HilbertSimulation simulation) {
        super(new GridLayout(0, 1), simulation);
        
        _simulation = simulation;
        
        // Various variation options of GravitySimulation
        JPanel variationsPanel = new JPanel();
        
        _variations = new JComboBox(VARIATIONS);
        _variations.setSelectedItem(VARIATION_DEFEAULT);
        _variations.addActionListener(this);
        
        variationsPanel.add(new JLabel("Variation:"));
        variationsPanel.add(_variations);
        
        JPanel p1 = new JPanel();
        
        _colors = new JComboBox(COLORS);
        _colors.setSelectedItem(COLOR_DEFEAULT);
        _colors.addActionListener(this);
        
        p1.add(new JLabel("Palette:"));
        p1.add(_colors);
        
        this.add(variationsPanel);
        this.add(p1);
    }
    
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        String str = "";
        
        if (source == _variations)
            str = (String) _variations.getSelectedItem();
        else if (source == _colors)
            str = (String) _colors.getSelectedItem();
        
        if (str == VARIATION_DEFEAULT)
            _simulation.setVariation(HilbertSystem.VARIATION_CIRCLE);
        else if (str == VARIATION_RECTANGLE)
            _simulation.setVariation(HilbertSystem.VARIATION_RECT);
        else if (str == COLOR_DEFEAULT)
            _simulation.setColor(HilbertSystem.COLOR_DEFAULT);
        else if (str == COLOR_NONE)
            _simulation.setColor(HilbertSystem.COLOR_NONE);
    }
}
