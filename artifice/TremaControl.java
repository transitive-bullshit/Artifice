package artifice;
import static artifice.ArtificeConstants.*;

import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.*;
import java.awt.*;

public class TremaControl extends ControlPanel implements ActionListener, ChangeListener {
    private static final String TREMA_ELLIPSE  = "Circle";
    private static final String TREMA_SPIKE    = "Spike";
    private static final String TREMA_SQUARE   = "Square";
    private static final String TREMA_WHITEOUT = "Whiteout";
    private static final String[] TREMA_VARIATIONS = {
        TREMA_ELLIPSE, TREMA_SQUARE, TREMA_SPIKE, TREMA_WHITEOUT, 
    };
    private MainPanel _mainPanel;
    private JComboBox _variations;
    private JSlider _opacity;
    private TremaEllipse _tremaEllipse;
    private TremaRectangle _tremaRectangle;
    private TremaSpike _tremaSpike;
    private TremaWhiteout _tremaWhiteout;
    
    public TremaControl(DrawingPanel dp, SimulationTimer timer, MainPanel main) {
        super(new GridLayout(0, 1), null);
        
        _mainPanel  = main;
        
        _tremaEllipse   = new TremaEllipse(dp, timer);
        _tremaRectangle = new TremaRectangle(dp, timer);
        _tremaSpike     = new TremaSpike(dp, timer);
        _tremaWhiteout  = new TremaWhiteout(dp, timer);
        
        this.setSimulation(_tremaEllipse);
        
        // Various variation options of TremaSimulation
        JPanel variationsPanel = new JPanel();
        
        _variations = new JComboBox(TREMA_VARIATIONS);
        _variations.setSelectedItem(TREMA_ELLIPSE);
        _variations.addActionListener(this);
        
        variationsPanel.add(new JLabel("Variation:"));
        variationsPanel.add(_variations);
        
        JPanel opacityPanel = new JPanel();
        
        // Opacity of Tremas
        opacityPanel.add(new JLabel("Opacity:"));
        opacityPanel.add(_opacity = new JSlider(10, 255, 255));
        _opacity.addChangeListener(this);
        
        this.add(variationsPanel);
        this.add(opacityPanel);
    }
    
    public void actionPerformed(ActionEvent e) {
//        String action = e.getActionCommand();
        String trema = (String) _variations.getSelectedItem();
        
        if (trema == TREMA_ELLIPSE)
            this.setSimulation(_tremaEllipse);
        else if (trema == TREMA_SQUARE)
            this.setSimulation(_tremaRectangle);
        else if (trema == TREMA_SPIKE)
            this.setSimulation(_tremaSpike);
        else if (trema == TREMA_WHITEOUT)
            this.setSimulation(_tremaWhiteout);
        
        _mainPanel.switchTo(this.getSimulation());
    }
    
    public void stateChanged(ChangeEvent e) {
        Object source = e.getSource();
        
        if (source == _opacity) {
            int opacity = _opacity.getValue();
            
            _tremaEllipse.setOpacity(opacity);
            _tremaRectangle.setOpacity(opacity);
            _tremaSpike.setOpacity(opacity);
            _tremaWhiteout.setOpacity(opacity);
        }
    }
}
