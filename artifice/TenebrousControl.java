package artifice;
import static artifice.ArtificeConstants.*;

import javax.swing.event.*;
import javax.swing.*;

import java.awt.event.*;
import java.awt.*;

public class TenebrousControl extends ControlPanel implements ActionListener, ChangeListener {
    private static final String VARIATION_ONE   = "High Quality";
    private static final String VARIATION_TWO   = "Low Quality";
    private static final String VARIATION_THREE = "Underlying";
    private static final String RANDOM_TEXT          = "Random";
    private static final String COLORED_TEXT         = "Colored";
    private static final String BLACK_AND_WHITE_TEXT = "Black & White";
    private static final String[] VARIATIONS = {
        VARIATION_ONE, VARIATION_TWO, VARIATION_THREE, 
    };
    private JComboBox _variations;
    private TenebrousSimulation _simulation;
    private JSlider _speed;
    
    public TenebrousControl(TenebrousSimulation simulation) {
        super(new GridLayout(0, 1), simulation);
        
        _simulation = simulation;
        
        JPanel p = new JPanel(new GridLayout(0, 1));
        
        // Various variation options of TenebrousSimulation
        JPanel variationsPanel = new JPanel();
        
        _variations = new JComboBox(VARIATIONS);
        _variations.setSelectedItem(VARIATION_ONE);
        _variations.addActionListener(this);
        
        variationsPanel.add(new JLabel("Drawing Variation:"));
        variationsPanel.add(_variations);
        
        p.add(variationsPanel);

        JPanel p2 = new JPanel();
        p2.add(new JLabel("Tentacle Speed:"));
        p2.add(_speed = new JSlider(80, 160, (int)(TENEBROUS_DEFAULT_SPEED * 100)));
        _speed.addChangeListener(this);
        
        p.add(p2);
        
        // 
        // Color Options Panel
        // 
        JPanel colorPanel = new JPanel();
        colorPanel.add(new JLabel("Color Options:"));
        ButtonGroup colorOptions = new ButtonGroup();
        AbstractButton button;
        
        colorPanel.add(button = new JRadioButton(RANDOM_TEXT, true));
        button.addActionListener(this);
        colorOptions.add(button);
        
        colorPanel.add(button = new JRadioButton(COLORED_TEXT, false));
        button.addActionListener(this);
        colorOptions.add(button);
        
        colorPanel.add(button = new JRadioButton(BLACK_AND_WHITE_TEXT, false));
        button.addActionListener(this);
        colorOptions.add(button);
        
        p.add(colorPanel);
        p.add(new JLabel("Click anywhere above to spawn a new tentacle."));
        
        this.add(p);
    }
    
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        String action = e.getActionCommand();
        
        if (source == _variations)
            _simulation.setVariation(_variations.getSelectedIndex());
        else if (action == RANDOM_TEXT)
            _simulation.setColoredType(0);
        else if (action == COLORED_TEXT)
            _simulation.setColoredType(1);
        else if (action == BLACK_AND_WHITE_TEXT)
            _simulation.setColoredType(2);
    }
    
    public void stateChanged(ChangeEvent e) {
        Object source = e.getSource();
        
        if (source == _speed)
            _simulation.setSpeed(((float)_speed.getValue()) / 100);
    }
}
