package artifice;
import static artifice.ArtificeConstants.*;

import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.*;

import java.awt.*;

public class SubstrateControl extends ControlPanel implements ActionListener, ChangeListener {
    private static final String LINEAR_TEXT  = "Linear";
    private static final String RADIAL_TEXT  = "Radial";
    private SubstrateSimulation _substrate;
    private JSlider _growthSlider;
    private JSlider _curvedSlider;
    
    public SubstrateControl(SubstrateSimulation substrate) {
        super(new GridLayout(0, 1), substrate);
        
        _substrate = substrate;
        
        JPanel spawnPanel = new JPanel();
        spawnPanel.add(new JLabel("Spawn Type:"));
        
        // Options for spawn type of new Cracks
        ButtonGroup renderOptions = new ButtonGroup();
        AbstractButton button;
        
        spawnPanel.add(button = new JRadioButton(LINEAR_TEXT, true));
        button.addActionListener(this);
        renderOptions.add(button);
        
        spawnPanel.add(button = new JRadioButton(RADIAL_TEXT));
        button.addActionListener(this);
        renderOptions.add(button);
        
        JPanel growthPanel = new JPanel();
        
        // Exponential Growth Speed of new Cracks
        // (determines the probability of multiple cracks being spawned each time 
        //  one expires)
        growthPanel.add(new JLabel("Growth Speed:"));
        growthPanel.add(_growthSlider = new JSlider(0, MAX_GROWTH, DEFAULT_GROWTH));
        _growthSlider.addChangeListener(this);
        _growthSlider.setSnapToTicks(true);
        
        JPanel curvedPanel = new JPanel();
        curvedPanel.add(new JLabel("Curved Cracks:"));
        curvedPanel.add(_curvedSlider = new JSlider(0, 100, 0));
        _curvedSlider.addChangeListener(this);
        
        this.add(spawnPanel);
        this.add(growthPanel);
        this.add(curvedPanel);
        this.add(new JLabel("Click anywhere above to spawn a new crack (drag for direction)."));
    }
    
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        
        if (action == LINEAR_TEXT)
            _substrate.setSpawnType(SPAWN_LINEAR);
        else if (action == RADIAL_TEXT)
            _substrate.setSpawnType(SPAWN_RADIAL);
    }
    
    public void stateChanged(ChangeEvent e) {
        Object source = e.getSource();
        
        if (source == _growthSlider)
            _substrate.setGrowth(MAX_GROWTH - _growthSlider.getValue());
        else if (source == _curvedSlider)
            Crack.setChanceOfCurved(_curvedSlider.getValue());
    }
}
