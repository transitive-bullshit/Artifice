package artifice;
import static artifice.ArtificeConstants.*;

import javax.swing.event.*;
import java.awt.event.*;
import javax.swing.*;

import java.awt.*;
import java.io.File;

public class DisplacedControl extends ControlPanel implements ActionListener, ChangeListener {
    private static final String LOAD_BACKGROUND_IMAGE = "Load Background Image";
    private static final String[] BACKGROUNDS = {
        "Displaced", "Vanish", "Cisco", "Ice", "Boardin"
    };
    
    private JComboBox _backgrounds;
    private DisplacedSimulation _simulation;
    private JCheckBox _automatic;
    private JSlider _waveStrength;
    private JFileChooser _fileChooser;
    private JButton _backgroundImage;
    private SimulationTimer _timer;
    
    public DisplacedControl(DisplacedSimulation simulation, SimulationTimer timer) {
        super(new BorderLayout(), simulation);
        
        _simulation = simulation;
        _timer      = timer;
        
        JPanel p0 = new JPanel();
        JPanel p1 = new JPanel(), p2 = new JPanel();
        JPanel p3 = new JPanel();
        
        p1.add(new JLabel("Wave Size"));
        p1.add(_waveStrength = new JSlider(2, 9, 4));
        _waveStrength.addChangeListener(this);
        
        p2.add(_automatic = new JCheckBox("Automatic Waves", true));
        _automatic.addChangeListener(this);
        
        _fileChooser = new JFileChooser();
        _fileChooser.setFileFilter(new ImageFileFilter());
        _fileChooser.setAcceptAllFileFilterUsed(false);
        
        p0.add(p1);
        p0.add(p2);
        
        JPanel p4 = new JPanel(), p5 = new JPanel();
        
        p4.add(_backgroundImage = new JButton(LOAD_BACKGROUND_IMAGE));
        _backgroundImage.addActionListener(this);
        
        p5.add(new JLabel("Default Backgrounds"));
        p5.add(_backgrounds = new JComboBox(BACKGROUNDS));
        _backgrounds.addActionListener(this);
        
        p3.add(p4);
        p3.add(p5);
        
        JPanel south = new JPanel(new BorderLayout());
        south.add(new JSeparator(), BorderLayout.NORTH);
        south.add(p3, BorderLayout.CENTER);
        south.add(new JLabel("Click/Drag anywhere above to spawn new ripples."), 
                BorderLayout.SOUTH);
        
        this.add(p0, BorderLayout.NORTH);
        this.add(south, BorderLayout.CENTER);
    }

    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        boolean running = _timer.isRunning();
        
        if (action == LOAD_BACKGROUND_IMAGE) {
            _timer.stop();
            
            if (_fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
                return; // Chose not to load a different Background Image
            
            File file = _fileChooser.getSelectedFile();
            String fileName = file.getAbsolutePath();
            
//            System.err.println(fileName);
            _simulation.loadBackgroundAbsolute(fileName);
        } else if (e.getSource() == _backgrounds) {
            _timer.stop();
            
            _simulation.loadBackground(DISPLACED_DEFAULT_BACKGROUNDS[_backgrounds.getSelectedIndex()]);
            _simulation.restart();
        }
    }
    
    
    public void stateChanged(ChangeEvent e) {
        Object source = e.getSource();
        
        if (source == _automatic)
            _simulation.setAutomaticWaves(_automatic.isSelected());
        else if (source == _waveStrength)
            _simulation.setWaveStrength(_waveStrength.getValue());
    }
}
