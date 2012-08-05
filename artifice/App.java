package artifice;
import javax.swing.*;
import java.awt.*;

/**
 * @author Travis Fischer
 * 
 */
public class App extends JFrame {
    
    public App(String title) {
        super(title);
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        
        if (ArtificeConstants.FULL_SCREEN) {
            this.setUndecorated(true);
            this.setIgnoreRepaint(true);
            
            GraphicsDevice _graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
//          DisplayMode displayMode = new DisplayMode(1440, 900, 32, 60);
//          if(_graphicsDevice.isDisplayChangeSupported())
//          _graphicsDevice.setDisplayMode(displayMode);
            
            if (_graphicsDevice.isFullScreenSupported())
                _graphicsDevice.setFullScreenWindow(this);
            
            ArtificeConstants.setSimulationDimensions(_graphicsDevice.getDisplayMode().getWidth(), 
                    _graphicsDevice.getDisplayMode().getHeight());
        }
        
        this.add(new MainPanel());
        if (!ArtificeConstants.FULL_SCREEN)
            this.pack();
        
        this.setVisible(true);
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
         App app = new App("Artifice");
    }
}
