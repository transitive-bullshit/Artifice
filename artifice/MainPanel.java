package artifice;
import static artifice.ArtificeConstants.*;

import javax.swing.event.*;

import java.awt.event.*;

import javax.swing.*;

import java.awt.*;

/**
 * @author Travis Fischer
 * @acct tfischer
 * 
 * Concept and code base by J. Tarbell
 * @see http://www.complexification.net/
 */
public class MainPanel extends JPanel implements ChangeListener, ActionListener {
    private static final String PAUSE_TEXT   = "Pause";
    private static final String UNPAUSE_TEXT = "UnPause";
    private static final String RESTART_TEXT = "Restart";
    private static final String QUIT_TEXT    = "Quit";
    private Simulation _currentSimulation;
    private SimulationTimer _timer;
    private JTabbedPane _controls;
    private JButton _pauseButton;
    private DrawingPanel _dp;
    private boolean _running;
    
    public MainPanel() {
        super(new BorderLayout());
        
        _running = false;
        _dp = new DrawingPanel();
        _timer = new SimulationTimer();
        
        // Initialize All of the Simulations
        // =================================
        _controls = new JTabbedPane();
        
        _controls.addChangeListener(this);
        _controls.setBackground(Color.LIGHT_GRAY);
        
        // 'Substrate' Simulation
        SubstrateControl sc = new SubstrateControl(new SubstrateSimulation(_dp, _timer));
        _controls.addTab("Substrate", null, sc, "Lines with Airbrushed Sand");
        
        // 'The Ring' Simulation
        TheRingControl trc = new TheRingControl(new TheRingSimulation(_dp, _timer));
        _controls.addTab("The Ring", null, trc, "Ever seen the movie 'The Ring'?");
        
        if (!FULL_SCREEN) {
            this.setIgnoreRepaint(true);
            
            // 'Trema' Simulation
            TremaControl trema = new TremaControl(_dp, _timer, this);
            _controls.addTab("Trema", null, trema, "Sasquatch");
            
            // 'Orbital' Simulation
            ControlPanel orbital = new ControlPanel(new OrbitalSimulation(_dp, _timer));
            _controls.addTab("Orbital", null, orbital, "Particles sorta orbiting each other");
        }
        
        // 'Primordial' Simulation
        PrimordialControl primordial = new PrimordialControl(new PrimordialSimulation(_dp, _timer));
        _controls.addTab("Primordial", null, primordial, "Primordial Organisms");
        
        if (!FULL_SCREEN) {
            // 'Displaced' Simulation
            DisplacedControl displaced = new DisplacedControl(new DisplacedSimulation(_dp, _timer), _timer);
            _controls.addTab("Displaced", null, displaced, "A pond of sorts");
            
            // 'Gravity' Simulation
            GravityControl gravity = new GravityControl(new GravitySimulation(_dp, _timer));
            _controls.addTab("Gravity", null, gravity, "N-Body Simulation");
        }
        
        // 'Hilbert' Simulation
        HilbertControl hilbert = new HilbertControl(new HilbertSimulation(_dp, _timer));
        _controls.addTab("Hilbert", null, hilbert, "Hilbert Space Filling");

        // 'Intersection' Simulation
        IntersectionControl intersection = new IntersectionControl(new IntersectionSimulation(_dp, _timer));
        _controls.addTab("Intersection", null, intersection, "Aggregate intersections of invisible circles");

        // 'Tenebrous' Simulation
        TenebrousControl tenebrous = new TenebrousControl(new TenebrousSimulation(_dp, _timer));
        _controls.addTab("Tenebrous", null, tenebrous, "Dark growth");
        
        // 'Attractor' Simulation
        AttractorControl attractor = new AttractorControl(new AttractorSimulation(_dp, _timer));
        _controls.addTab("Attractor", null, attractor, "Attractive Math.. Hah, haha, Muaahahahaha!");

//        // 'Attractor' Simulation
//        ControlPanel colorAttractor = new ControlPanel(new ColorAttractorSimulation(_dp, _timer));
//        _controls.addTab("Attractor", null, colorAttractor, "Attractive Math.. Hah, haha, Muaahahahaha!");
        
        
        // Master Controls (static between different Simulations)
        // ======================================================
        
        // Pause Button
        JPanel masterControls = new JPanel(new BorderLayout());
        JPanel p = new JPanel();
        p.add(_pauseButton = new JButton(PAUSE_TEXT));
        masterControls.add(p, BorderLayout.WEST);
        _pauseButton.addActionListener(this);
        
        // Restart Simulation
        JButton restart = new JButton(RESTART_TEXT);
        p = new JPanel();
        p.add(restart);
        masterControls.add(p, BorderLayout.CENTER);
        restart.addActionListener(this);
        
        // Quit Application
        JButton quit = new JButton(QUIT_TEXT);
        p = new JPanel();
        p.add(quit);
        masterControls.add(p, BorderLayout.EAST);
        quit.addActionListener(this);
        
        this.add(_dp, BorderLayout.NORTH);
//        if (!FULL_SCREEN) {
            this.add(_controls, BorderLayout.CENTER);
            this.add(masterControls, BorderLayout.SOUTH);
//        }
        
        _running = true;
        this.switchTo(sc);
    }
    
    public void switchTo(ControlPanel newControl) {
        if (newControl != _controls.getSelectedComponent())
            _controls.setSelectedComponent(newControl);
        
        this.switchTo(newControl.getSimulation());
    }
    
    public void switchTo(Simulation newSimulation) {
        if (_currentSimulation != newSimulation) {
            _currentSimulation = newSimulation;
            _currentSimulation.run();
        }
    }
    
    public void stateChanged(ChangeEvent e) {
        ControlPanel selected = (ControlPanel) _controls.getSelectedComponent();
        
        if (selected != null && _running == true) {
            if (_pauseButton != null)
                _pauseButton.setText(PAUSE_TEXT);
            
            this.switchTo(selected);
        }
    }
    
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        
        if (action == PAUSE_TEXT) {
            _currentSimulation.setPaused(true);
            _pauseButton.setText(UNPAUSE_TEXT);
        } else if (action == UNPAUSE_TEXT) {
            _currentSimulation.setPaused(false);
            _pauseButton.setText(PAUSE_TEXT);
        } else if (action == RESTART_TEXT) {
            if (_currentSimulation.isPaused())
                _pauseButton.setText(PAUSE_TEXT);
            
            _currentSimulation.restart();
        } else if (action == QUIT_TEXT) {
            System.exit(0);
        }
    }
    
//    private class BorderPanel extends JPanel {
//        public BorderPanel(JPanel component) {
//            super(new GridLayout(0, 1));
//            
//            this.add(component);
//            this.add(new JSeparator());
//            //this.setBorder(new LineBorder(new Color(0,0,0),2));//EtchedBorder(EtchedBorder.RAISED));
//        }
//        
////        public Insets getInsets() {
////            return new Insets( 0, 0, 0, 0 );
////        }
//    }
}
